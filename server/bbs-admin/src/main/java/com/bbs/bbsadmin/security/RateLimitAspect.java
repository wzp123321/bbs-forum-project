package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 限流切面 (固定窗口 + 滑动窗口混合)
 * - 每个 (ip, key) 维护一个时间戳队列, 队列中只保留 refillSeconds 内的请求
 * - 当队列长度达到 capacity, 拒绝请求
 * - 队列每 10s 清理一次过期 bucket
 *
 * 注意: 单机内存版, 集群场景可改为 Redis INCR + EXPIRE
 */
@Aspect
@Component
public class RateLimitAspect {

    /** key -> 时间戳队列 (毫秒) */
    private final ConcurrentHashMap<String, Deque<Long>> windows = new ConcurrentHashMap<>();
    private final AtomicReference<Long> lastSweep = new AtomicReference<>(System.currentTimeMillis());

    @Around("@annotation(com.bbs.bbsadmin.security.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        RateLimit ann = method.getAnnotation(RateLimit.class);
        if (ann == null) return pjp.proceed();

        String key = clientIp() + "::" + (ann.key().isEmpty() ? method.getDeclaringClass().getSimpleName() + "#" + method.getName() : ann.key());
        long windowMs = ann.refillSeconds() * 1000L;
        long now = System.currentTimeMillis();

        Deque<Long> q = windows.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) {
            // 弹出窗口外的旧时间戳
            long threshold = now - windowMs;
            while (!q.isEmpty() && q.peekFirst() < threshold) {
                q.pollFirst();
            }
            if (q.size() >= ann.capacity()) {
                long wait = (q.peekFirst() + windowMs - now + 999) / 1000;
                throw new BizException(ResponseCode.TOO_MANY_REQUESTS, ann.message() + " (" + wait + "s 后可重试)");
            }
            q.offerLast(now);
        }

        // 周期性清理
        maybeSweep();

        return pjp.proceed();
    }

    /** 简易清理: 超过 1w 桶或 60s 未清理则触发 */
    private void maybeSweep() {
        Long prev = lastSweep.get();
        long now = System.currentTimeMillis();
        if (now - prev < 60_000) return;
        if (!lastSweep.compareAndSet(prev, now)) return;
        long threshold = now - 300_000L; // 5 分钟未访问的桶清掉
        windows.forEach((k, q) -> {
            synchronized (q) {
                while (!q.isEmpty() && q.peekFirst() < threshold) {
                    q.pollFirst();
                }
                if (q.isEmpty()) {
                    windows.remove(k, q);
                }
            }
        });
        // 防止内存无限增长
        if (windows.size() > 10_000) {
            windows.clear();
        }
    }

    private String clientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
            String real = req.getHeader("X-Real-IP");
            if (real != null && !real.isBlank()) return real.trim();
            return req.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
