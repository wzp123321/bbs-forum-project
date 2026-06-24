package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 反馈新增入参 (用户提交)
 */
@Data
public class FeedbackSaveDTO {

    /** 类型: bug/建议/投诉/其他 */
    @NotBlank(message = "请选择反馈类型")
    @Size(max = 20, message = "类型不超过 20 字符")
    private String type;

    @NotBlank(message = "请输入反馈内容")
    @Size(max = 2000, message = "内容不超过 2000 字符")
    private String content;
}
