package com.bbs.bbsadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 附件上传配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "bbs.upload")
public class UploadProperties {

    /** 本地存储根目录 */
    private String baseDir = "./uploads";

    /** 对外访问 URL 前缀 */
    private String urlPrefix = "/upload/";

    /** 允许的扩展名 (逗号分隔) */
    private String allowedExtensions = "";

    /** 允许的 MIME 类型前缀 (逗号分隔) */
    private String allowedContentTypesPrefix = "";

    public List<String> getAllowedExtensionList() {
        if (allowedExtensions == null || allowedExtensions.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(allowedExtensions.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public List<String> getAllowedContentTypePrefixList() {
        if (allowedContentTypesPrefix == null || allowedContentTypesPrefix.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(allowedContentTypesPrefix.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
