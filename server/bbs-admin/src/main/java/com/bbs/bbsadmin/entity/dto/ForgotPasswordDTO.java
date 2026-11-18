package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 忘记密码: 身份校验入参
 */
@Data
public class ForgotPasswordDTO {

    @NotBlank(message = "请输入用户名")
    private String account;

    /** 账号绑定的邮箱或手机号 */
    @NotBlank(message = "请输入绑定的邮箱或手机号")
    private String contact;
}
