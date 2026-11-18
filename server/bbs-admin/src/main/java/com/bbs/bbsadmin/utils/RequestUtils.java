package com.bbs.bbsadmin.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求上下文工具
 */
public final class RequestUtils {

    private RequestUtils() {
    }

    /**
     * 取调用方 IP
     * 只取 remoteAddr, 不信任 X-Forwarded-For —— 该头可被客户端伪造,
     * 用于阅读去重时会导致绕过计数
     */
    public static String clientIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return "unknown";
        }
        HttpServletRequest request = attrs.getRequest();
        String ip = request.getRemoteAddr();
        return StringUtils.hasText(ip) ? ip : "unknown";
    }
}
