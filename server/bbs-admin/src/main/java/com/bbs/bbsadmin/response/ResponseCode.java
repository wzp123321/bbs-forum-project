package com.bbs.bbsadmin.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "成功"),
    ERROR(500, "失败");

    /**
     * 响应码
     */
    private final Integer code;
    /**
     * 相应信息
     */
    private final String message;
}
