package com.bbs.bbsadmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具
 */
@Component
public class JwtUtil {

    /** 默认密钥 (仅供开发期使用,生产请通过 yml 配置) */
    private static final String DEFAULT_SECRET = "bbs-admin-default-secret-key-please-change-in-production-32+bytes";
    /** 默认有效期 7 天 (7*24*60*60*1000) */
    private static final long DEFAULT_EXPIRE_MILLIS = 604800000L;

    @Value("${bbs.jwt.secret:}")
    private String secret;

    @Value("${bbs.jwt.expire-millis:604800000}")
    private long expireMillis;

    private SecretKey key;

    @PostConstruct
    public void init() {
        String useSecret = (secret == null || secret.isBlank()) ? DEFAULT_SECRET : secret;
        // jjwt 要求 HMAC-SHA 密钥至少 256 bit (32 字节)
        byte[] bytes = useSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            // 不足时右补 0 凑齐
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            bytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 生成 token
     * @param userId 用户ID (subject)
     * @param claims 附加信息
     */
    public String generate(String userId, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(userId)
                .claims(claims == null ? new HashMap<>() : claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireMillis))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并校验 token
     * @throws JwtException 校验失败
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
