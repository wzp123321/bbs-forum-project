package com.bbs.bbsadmin.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解
 * - 标注后由 AuditLogAspect 自动捕获: userId/userName/ip/uri/method/cost
 * - 失败抛异常时自动记 success=0 + errorMsg
 *
 * 用法:
 *   @AuditLog(action = "POST_CREATE", description = "发帖", targetType = "POST", targetIdSpEl = "#dto.title")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    /** 操作类型 (大写下划线), 例: LOGIN / POST_CREATE / POST_DELETE */
    String action();
    /** 操作描述 (中文) */
    String description() default "";
    /** 目标类型 (POST / COMMENT / REPORT / FEEDBACK / USER) */
    String targetType() default "";
    /** 目标 ID SpEL 表达式, 支持 #id / #dto.userId / 'literal' 等; 留空则不取 */
    String targetIdSpEl() default "";
}
