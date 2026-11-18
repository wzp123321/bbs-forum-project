package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码入参
 */
@Data
public class PasswordResetDTO {

    @NotBlank(message = "重置令牌不能为空")
    private String resetToken;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度 6-32")
    private String newPassword;
}
