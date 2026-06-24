package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.config.UploadProperties;
import com.bbs.bbsadmin.entity.Attachment;
import com.bbs.bbsadmin.entity.vo.AttachmentVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.AttachmentMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.service.AttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 附件 Service 实现
 */
@Slf4j
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {

    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public AttachmentVO upload(MultipartFile file, String userId, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BizException(ResponseCode.PARAM_ERROR, "上传文件为空");
        }
        String originName = file.getOriginalFilename();
        if (!StringUtils.hasText(originName)) {
            throw new BizException(ResponseCode.PARAM_ERROR, "无法识别的文件名");
        }
        // 取扩展名
        String ext = "";
        int dotIdx = originName.lastIndexOf('.');
        if (dotIdx > -1 && dotIdx < originName.length() - 1) {
            ext = originName.substring(dotIdx + 1).toLowerCase();
        }
        // 扩展名校验
        List<String> allowedExt = uploadProperties.getAllowedExtensionList();
        if (!allowedExt.isEmpty() && !allowedExt.contains(ext)) {
            throw new BizException(ResponseCode.PARAM_ERROR, "不支持的文件类型: " + ext);
        }
        // content-type 校验
        String contentType = file.getContentType();
        List<String> allowedCt = uploadProperties.getAllowedContentTypePrefixList();
        if (!allowedCt.isEmpty()) {
            boolean ok = contentType != null && allowedCt.stream().anyMatch(contentType::startsWith);
            if (!ok) {
                throw new BizException(ResponseCode.PARAM_ERROR, "不支持的 MIME 类型: " + contentType);
            }
        }

        // 生成存储路径: yyyy/MM/dd/<uuid>.<ext>
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + (StringUtils.hasText(ext) ? "." + ext : "");
        String bizDir = (StringUtils.hasText(bizType) ? bizType : "common") + "/" + dateDir;
        String relativePath = bizDir + "/" + storedName;

        // 落盘
        Path target = Paths.get(uploadProperties.getBaseDir(), bizDir, storedName).toAbsolutePath().normalize();
        try {
            Files.createDirectories(target.getParent());
            try (var in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("文件保存失败: {}", target, e);
            throw new BizException(ResponseCode.ERROR, "文件保存失败");
        }

        // 构造访问URL
        String urlPrefix = uploadProperties.getUrlPrefix();
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        String url = urlPrefix + relativePath;

        // 入库
        Attachment att = new Attachment();
        att.setUserId(userId);
        att.setBizType(bizType);
        att.setOriginName(originName);
        att.setFileName(storedName);
        att.setFilePath(relativePath);
        att.setUrl(url);
        att.setContentType(contentType);
        att.setSize(file.getSize());
        att.setStorageType("local");
        att.setStatus(1);
        att.setCreateBy(userId);
        att.setUpdateBy(userId);
        save(att);

        return AttachmentVO.from(att);
    }

    @Override
    public void bindBiz(Long attachmentId, String bizType, Long bizId) {
        if (attachmentId == null) {
            return;
        }
        Attachment att = getById(attachmentId);
        if (att == null) {
            return;
        }
        att.setBizType(bizType);
        att.setBizId(bizId);
        updateById(att);
    }

    @Override
    public void bindContentImages(String content, String bizType, Long bizId) {
        if (!StringUtils.hasText(content) || bizId == null) {
            return;
        }
        // 从 HTML 中抽取 <img src="..."> 的 URL
        Pattern p = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(content);
        Set<String> urls = new HashSet<>();
        while (m.find()) {
            String src = m.group(1);
            if (StringUtils.hasText(src)) {
                urls.add(src);
            }
        }
        if (urls.isEmpty()) {
            return;
        }
        // 取出所有 URL 末尾的文件名
        List<String> fileNames = new ArrayList<>();
        String urlPrefix = uploadProperties.getUrlPrefix();
        if (!urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix + "/";
        }
        for (String src : urls) {
            // 兼容前端拼接的完整 URL (去除 host 前缀)
            String pure = src;
            int idx = src.indexOf(urlPrefix);
            if (idx > -1) {
                pure = src.substring(idx + urlPrefix.length());
            } else if (src.startsWith("/upload/")) {
                pure = src.substring("/upload/".length());
            }
            // pure 形如 post/2025/06/24/abc.png
            if (StringUtils.hasText(pure)) {
                fileNames.add(pure);
            }
        }
        if (fileNames.isEmpty()) {
            return;
        }
        // 更新这些附件的 biz 信息
        List<Attachment> records = baseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Attachment>()
                        .in(Attachment::getFilePath, fileNames)
        );
        for (Attachment att : records) {
            att.setBizType(bizType);
            att.setBizId(bizId);
            updateById(att);
        }
    }
}
