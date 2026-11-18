package com.bbs.bbsadmin.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 找回密码的一次性令牌存储
 * <p>
 * - token -> userId, 10 分钟过期, 用一次即作废
 * - account -> 最近一次签发的 token, 用于 60 秒内重复申请时复用, 避免刷接口签发一堆令牌
 * <p>
 * 说明: 项目没有邮件/短信通道, 令牌由接口直接返回给前端。
 * 接入 SMTP 后应改为只走邮件下发, 接口不再返回值。
 */
@Component
public class PasswordResetStore {

    /** 令牌有效期(秒) */
    private static final long EXPIRE_SECONDS = 600L;

    /** 同账号复用窗口(毫秒) */
    private static final long REUSE_WINDOW_MILLIS = 60_000L;

    private final Cache<String, String> tokenCache = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS)
            .maximumSize(10_000)
            .build();

    private final Cache<String, Issued> accountCache = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS)
            .maximumSize(10_000)
            .build();

    /**
     * 为账号签发令牌; 60 秒内重复申请返回同一个仍然有效的令牌
     */
    public String issue(String account, String userId) {
        long now = System.currentTimeMillis();
        Issued old = accountCache.getIfPresent(account);
        if (old != null
                && now - old.issuedAt() < REUSE_WINDOW_MILLIS
                && tokenCache.getIfPresent(old.token()) != null) {
            return old.token();
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenCache.put(token, userId);
        accountCache.put(account, new Issued(token, now));
        return token;
    }

    /**
     * 取出并作废令牌
     *
     * @return 关联的 userId, 令牌无效时返回 null
     */
    public String consume(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String userId = tokenCache.getIfPresent(token);
        if (userId != null) {
            tokenCache.invalidate(token);
        }
        return userId;
    }

    public long expireSeconds() {
        return EXPIRE_SECONDS;
    }

    private record Issued(String token, long issuedAt) {
    }
}
