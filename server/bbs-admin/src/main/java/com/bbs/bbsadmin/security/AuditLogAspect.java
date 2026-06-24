package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.entity.OperationLog;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.security.annotation.AuditLog;
import com.bbs.bbsadmin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 审计日志切面
 * - 环绕 @AuditLog 注解方法
 * - 自动写入: 成功/失败 + userId + IP + 耗时 + 错误信息
 * - 写入失败不影响业务
 */
@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer paramNames = new DefaultParameterNameDiscoverer();
    private final ConcurrentHashMap<String, Expression> exprCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.bbs.bbsadmin.security.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        AuditLog ann = method.getAnnotation(AuditLog.class);

        OperationLog log = buildBaseLog(method, ann);
        log.setUri(currentUri());
        log.setMethod(currentMethod());
        log.setIp(clientIp());
        log.setUserAgent(currentUserAgent());
        if (log.getUserId() == null) {
            try {
                log.setUserId(com.bbs.bbsadmin.security.AuthContext.userId());
                log.setUserName(com.bbs.bbsadmin.security.AuthContext.userName());
            } catch (Exception ignore) { }
        }

        // 解析 targetId
        if (ann != null && !ann.targetIdSpEl().isEmpty()) {
            log.setTargetId(evalSpEl(ann.targetIdSpEl(), method, pjp.getArgs()));
        }

        try {
            Object result = pjp.proceed();
            log.setSuccess(1);
            return result;
        } catch (Throwable e) {
            log.setSuccess(0);
            String msg = e.getMessage();
            log.setErrorMsg(msg == null ? e.getClass().getSimpleName() : (msg.length() > 500 ? msg.substring(0, 500) : msg));
            throw e;
        } finally {
            log.setCostMs(System.currentTimeMillis() - start);
            log.setCreateTime(LocalDateTime.now());
            try {
                operationLogService.asyncLog(log);
            } catch (Exception ignore) { }
        }
    }

    private OperationLog buildBaseLog(Method method, AuditLog ann) {
        OperationLog log = new OperationLog();
        if (ann != null) {
            log.setAction(ann.action());
            log.setDescription(ann.description());
            log.setTargetType(ann.targetType());
        }
        return log;
    }

    private String evalSpEl(String spEl, Method method, Object[] args) {
        try {
            Expression expr = exprCache.computeIfAbsent(spEl, parser::parseExpression);
            EvaluationContext ctx = new StandardEvaluationContext();
            String[] names = paramNames.getParameterNames(method);
            if (names != null) {
                for (int i = 0; i < names.length && i < args.length; i++) {
                    ctx.setVariable(names[i], args[i]);
                }
            }
            Object val = expr.getValue(ctx);
            return val == null ? null : String.valueOf(val);
        } catch (EvaluationException e) {
            return null;
        }
    }

    private String currentUri() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs == null ? null : attrs.getRequest().getRequestURI();
        } catch (Exception e) { return null; }
    }

    private String currentMethod() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs == null ? null : attrs.getRequest().getMethod();
        } catch (Exception e) { return null; }
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
        } catch (Exception e) { return null; }
    }

    private String currentUserAgent() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            String ua = attrs.getRequest().getHeader("User-Agent");
            if (ua != null && ua.length() > 500) ua = ua.substring(0, 500);
            return ua;
        } catch (Exception e) { return null; }
    }
}
