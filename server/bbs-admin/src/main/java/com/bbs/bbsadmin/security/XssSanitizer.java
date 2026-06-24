package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.ResponseCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本清洗器 (XSS 防护 + 敏感词拦截)
 * - 用 Jsoup 保留允许的标签, 清洗脚本/事件/javascript: 等
 * - 命中敏感词抛 422, 调用方自行决定阻断还是转审核
 *
 * 允许的标签范围 (白名单):
 *   排版: p, br, h1-h6, blockquote, pre, code, hr
 *   文本: strong, em, b, i, u, s, del, ins, mark, sub, sup, small, span
 *   列表: ul, ol, li
 *   媒体: img (仅 http/https/data src, 无 onerror 等)
 *   链接: a (仅 http/https/mailto scheme)
 *
 * 已禁用 (即便后端允许, 前端也不应再用):
 *   script, style, iframe, object, embed, form, input, button, link, meta
 */
@Component
public class XssSanitizer {

    /** 危险协议 (内联事件里的 javascript: 等) */
    private static final Pattern DANGEROUS_HREF = Pattern.compile("(?i)^\\s*(javascript|vbscript|data|mocha|livescript):");

    /** 所有 on* 事件属性名 (Jsoup 已默认清除, 这里兜底) */
    private static final Set<String> EVENT_ATTRS = new HashSet<>(Arrays.asList(
            "onload", "onerror", "onclick", "onmouseover", "onmouseout",
            "onkeydown", "onkeyup", "onkeypress", "onfocus", "onblur",
            "onchange", "onsubmit", "onreset", "onselect", "onabort"
    ));

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    /** 构造白名单 */
    private Safelist safelist() {
        return Safelist.relaxed()
                // 限制 a 仅 http/https/mailto
                .addAttributes("a", "href", "title", "target", "rel")
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addAttributes("code", "class")
                .addAttributes("pre", "class")
                // 禁止所有 on* 事件
                .addProtocols("a", "href", "http", "https", "mailto")
                .addProtocols("img", "src", "http", "https", "data");
    }

    /**
     * 清洗富文本 (保留原样, 仅清洗 XSS)
     * @return 清洗后的安全 HTML
     */
    public String clean(String html) {
        if (html == null || html.isEmpty()) return html;
        Cleaner cleaner = new Cleaner(safelist());
        Document dirty = Jsoup.parseBodyFragment(html);
        Document.OutputSettings settings = new Document.OutputSettings()
                .prettyPrint(false)
                .outline(false);
        Document clean = cleaner.clean(dirty);
        clean.outputSettings(settings);

        // 二次兜底: 遍历清洗后的 DOM, 主动剔除危险协议
        for (Element el : clean.getAllElements()) {
            if (el.hasAttr("href")) {
                String href = el.attr("href");
                if (DANGEROUS_HREF.matcher(href).find()) {
                    el.removeAttr("href");
                }
            }
            // 兜底剔除事件属性 (虽然 Cleaner 已处理)
            for (String attr : el.attributes().asList().stream().map(a -> a.getKey()).toList()) {
                if (EVENT_ATTRS.contains(attr.toLowerCase()) || attr.toLowerCase().startsWith("on")) {
                    el.removeAttr(attr);
                }
            }
        }

        return clean.body().html();
    }

    /**
     * 清洗 + 敏感词检测
     * @param html 入参富文本
     * @param throwOnHit 命中敏感词是否直接抛异常
     * @return 清洗后的 HTML; 命中敏感词时 throwOnHit=false 返回首个敏感词 (null = 未命中)
     */
    public String cleanAndCheck(String html, boolean throwOnHit) {
        String safe = clean(html);
        if (html != null && !html.isEmpty()) {
            // 同时检查原文 + 清洗后结果 (避免绕过)
            String hit = sensitiveWordFilter.matchFirst(html);
            if (hit == null) hit = sensitiveWordFilter.matchFirst(safe);
            if (hit != null) {
                if (throwOnHit) {
                    throw new BizException(ResponseCode.SENSITIVE_WORD, "内容包含敏感词: " + hit);
                }
                return null; // 标记命中, 调用方看返回值判定
            }
        }
        return safe;
    }

    /**
     * 仅检测敏感词 (不修改内容)
     */
    public boolean hasSensitive(String text) {
        return text != null && sensitiveWordFilter.contains(text);
    }
}
