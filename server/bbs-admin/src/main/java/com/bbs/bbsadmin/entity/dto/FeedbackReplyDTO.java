package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 反馈回复入参 (管理员处理)
 */
@Data
public class FeedbackReplyDTO {

    @NotBlank(message = "请输入回复内容")
    @Size(max = 2000, message = "回复内容不超过 2000 字符")
    private String reply;
}
