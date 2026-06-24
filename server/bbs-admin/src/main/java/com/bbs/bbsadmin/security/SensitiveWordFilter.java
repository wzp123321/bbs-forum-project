package com.bbs.bbsadmin.security;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 简单敏感词过滤器
 *
 * 命中规则: 文本中出现任一敏感词则视为命中
 * - 大小写不敏感
 * - 大小写混合的文本也会被规范化后匹配
 * - 默认词表仅作占位, 部署时可扩展 bbs.sensitive.words 配置项
 */
@Component
public class SensitiveWordFilter {

    /** 默认词表, 仅作演示 */
    private static final List<String> DEFAULT_WORDS = Arrays.asList(
            "badword1",
            "badword2",
            "敏感词"
    );

    private volatile Set<String> words = normalize(DEFAULT_WORDS);

    /** 命中检测, 返回首个命中的词; 未命中返回 null */
    public String matchFirst(String text) {
        if (text == null || text.isEmpty() || words.isEmpty()) {
            return null;
        }
        String lower = text.toLowerCase();
        for (String w : words) {
            if (lower.contains(w)) {
                return w;
            }
        }
        return null;
    }

    public boolean contains(String text) {
        return matchFirst(text) != null;
    }

    /** 替换命中词为占位符 (单字符 *) */
    public String mask(String text) {
        if (text == null || text.isEmpty() || words.isEmpty()) {
            return text;
        }
        String lower = text.toLowerCase();
        String out = text;
        for (String w : words) {
            int idx = lower.indexOf(w);
            while (idx >= 0) {
                int end = idx + w.length();
                String head = out.substring(0, idx);
                String tail = out.substring(end);
                StringBuilder stars = new StringBuilder();
                for (int i = 0; i < w.length(); i++) {
                    stars.append('*');
                }
                out = head + stars.toString() + tail;
                lower = out.toLowerCase();
                idx = lower.indexOf(w, idx + w.length());
            }
        }
        return out;
    }

    private static Set<String> normalize(List<String> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> s = new HashSet<>();
        for (String w : list) {
            if (w == null) continue;
            String t = w.trim().toLowerCase();
            if (!t.isEmpty()) s.add(t);
        }
        return s;
    }
}
