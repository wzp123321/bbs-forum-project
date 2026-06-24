package com.bbs.bbsadmin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "成功"),
    ERROR(500, "失败"),

    // ========== 通用 4xx ==========
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    TOO_MANY_REQUESTS(429, "请求过于频繁,请稍后再试"),
    SENSITIVE_WORD(422, "内容包含敏感词"),
    LOCKED(423, "账号已被临时锁定"),

    // ========== 用户模块 1xxx ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    BAD_CREDENTIALS(1002, "账号或密码错误"),
    USER_DISABLED(1003, "账号已被禁用");

    /**
     * 响应码
     */
    private final Integer code;
    /**
     * 响应信息
     */
    private final String message;
}
