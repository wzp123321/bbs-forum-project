package com.bbs.bbsadmin.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * - 容量桶算法 (Bucket4j)
 * - 默认按 IP+方法 维度计数
 * - 通过 SpEL 表达式可指定 dimension 为 userId 等
 *
 * 用法:
 *   @RateLimit(capacity = 5, refillTokens = 5, refillSeconds = 60)   // 60s 内最多 5 次
 *   @RateLimit(key = "publishPost", capacity = 10, refillSeconds = 60)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /** 业务键, 区分不同限流场景, 不填则用方法签名 */
    String key() default "";
    /** 桶容量 (瞬时允许通过的最大请求数) */
    int capacity() default 10;
    /** 单位时间补充的令牌数, 默认 = capacity (即跑满后等满才能继续) */
    int refillTokens() default -1;
    /** 补充周期 (秒) */
    int refillSeconds() default 60;
    /** 提示语 */
    String message() default "请求过于频繁,请稍后再试";
}
