package com.bbs.bbsadmin.config;

import com.bbs.bbsadmin.security.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置: 注册鉴权拦截器 + 静态资源映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 登录接口、Knife4j 文档、Druid 监控、错误页、附件 放行
                .excludePathPatterns(
                        "/admin/auth/login",
                        "/admin/auth/captcha",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/druid/**",
                        "/error",
                        // 附件上传路径 (包括上传接口和静态资源)
                        "/admin/attachment/**",
                        // 静态资源 (按 urlPrefix 动态放行)
                        uploadProperties.getUrlPrefix() + "**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String prefix = uploadProperties.getUrlPrefix();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        // 把 url-prefix 映射到 base-dir 目录,实现本地文件对外访问
        registry.addResourceHandler(prefix + "**")
                .addResourceLocations("file:" + uploadProperties.getBaseDir() + "/");
    }
}
