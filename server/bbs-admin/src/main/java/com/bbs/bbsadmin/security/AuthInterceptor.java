package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 鉴权拦截器
 * - 解析 Authorization: Bearer xxx
 * - 校验通过后写入 AuthContext
 * - 标注 @RequireAuth 才强制要求登录 (白名单接口可省略)
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跳过非 Controller 方法 (静态资源等)
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        boolean require = hm.hasMethodAnnotation(RequireAuth.class) || hm.getBeanType().isAnnotationPresent(RequireAuth.class);
        // 没有 @RequireAuth 也允许带 token (便于 /info /logout 等可选登录)
        String header = request.getHeader(HEADER);
        if (header == null || header.isBlank()) {
            if (require) {
                throw new BizException(ResponseCode.UNAUTHORIZED);
            }
            return true;
        }
        if (!header.startsWith(PREFIX)) {
            throw new BizException(ResponseCode.UNAUTHORIZED, "Authorization 格式错误");
        }
        String token = header.substring(PREFIX.length()).trim();
        try {
            Claims claims = jwtUtil.parse(token);
            String userId = claims.getSubject();
            String userName = claims.get("userName", String.class);
            AuthContext.set(userId, userName);
        } catch (JwtException e) {
            throw new BizException(ResponseCode.UNAUTHORIZED, "token 无效或已过期");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
