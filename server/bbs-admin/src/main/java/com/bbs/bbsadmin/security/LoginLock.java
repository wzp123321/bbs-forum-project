package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 登录失败锁定
 * - 同一账号连续 5 次失败 → 锁定 5 分钟
 * - 同一 IP 连续 10 次失败 → 锁定 IP 5 分钟
 * - 锁定期间抛 423 LOCKED
 * - 登录成功调用 clear() 清零
 * - 每 60s 清理过期记录, 防止内存泄漏
 *
 * 单机内存版, 集群需替换为 Redis (account/ip 维度计数 + 过期时间)
 */
@Component
public class LoginLock {

    /** 账号最大失败次数 */
    private static final int ACCOUNT_MAX_FAILS = 5;
    /** IP 最大失败次数 */
    private static final int IP_MAX_FAILS = 10;
    /** 锁定时长 (毫秒) */
    private static final long LOCK_MILLIS = 5L * 60_000L;

    /** 维度类型 */
    private enum Type { ACCOUNT, IP }

    private final ConcurrentHashMap<String, Record> records = new ConcurrentHashMap<>();
    private final AtomicReference<Long> lastSweep = new AtomicReference<>(System.currentTimeMillis());

    private static class Record {
        int fails;
        long lockedUntil; // 0 = 未锁
    }

    /**
     * 登录前调用, 命中锁定则抛 423
     */
    public void check(String account) {
        sweep();
        long now = System.currentTimeMillis();
        // 账号维度
        checkOne(Type.ACCOUNT, account, ACCOUNT_MAX_FAILS, now);
        // IP 维度
        String ip = clientIp();
        if (ip != null) {
            checkOne(Type.IP, ip, IP_MAX_FAILS, now);
        }
    }

    private void checkOne(Type type, String key, int max, long now) {
        Record r = records.get(keyOf(type, key));
        if (r == null) return;
        if (r.lockedUntil > now) {
            long sec = (r.lockedUntil - now + 999) / 1000;
            String label = type == Type.ACCOUNT ? "账号" : "IP";
            throw new BizException(ResponseCode.LOCKED, label + "已被临时锁定, 请 " + sec + " 秒后重试");
        }
        // 锁定已过期, 清零
        if (r.lockedUntil > 0 && r.lockedUntil <= now) {
            records.remove(keyOf(type, key), r);
        }
    }

    /**
     * 登录失败时调用, 累加并检查是否触发锁定
     */
    public void onFailure(String account) {
        long now = System.currentTimeMillis();
        failOne(Type.ACCOUNT, account, ACCOUNT_MAX_FAILS, now);
        String ip = clientIp();
        if (ip != null) {
            failOne(Type.IP, ip, IP_MAX_FAILS, now);
        }
    }

    private void failOne(Type type, String key, int max, long now) {
        String k = keyOf(type, key);
        Record r = records.computeIfAbsent(k, x -> new Record());
        synchronized (r) {
            // 锁定已过期则重置
            if (r.lockedUntil > 0 && r.lockedUntil <= now) {
                r.fails = 0;
                r.lockedUntil = 0;
            }
            r.fails++;
            if (r.fails >= max) {
                r.lockedUntil = now + LOCK_MILLIS;
            }
        }
    }

    /**
     * 登录成功时清零
     */
    public void clear(String account) {
        records.remove(keyOf(Type.ACCOUNT, account));
        String ip = clientIp();
        if (ip != null) {
            records.remove(keyOf(Type.IP, ip));
        }
    }

    private String keyOf(Type type, String val) {
        return type.name() + "::" + val;
    }

    private void sweep() {
        Long prev = lastSweep.get();
        long now = System.currentTimeMillis();
        if (now - prev < 60_000) return;
        if (!lastSweep.compareAndSet(prev, now)) return;
        records.forEach((k, r) -> {
            synchronized (r) {
                if (r.lockedUntil > 0 && r.lockedUntil <= now) {
                    records.remove(k, r);
                }
            }
        });
    }

    private String clientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
            String real = req.getHeader("X-Real-IP");
            if (real != null && !real.isBlank()) return real.trim();
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
}
