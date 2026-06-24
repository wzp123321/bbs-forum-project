package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 举报新增入参 (用户提交)
 */
@Data
public class ReportSaveDTO {

    /** 举报目标: 1帖子 2评论 */
    @NotNull(message = "请选择举报目标")
    private Integer targetType;

    /** 目标ID */
    @NotNull(message = "请提供举报目标ID")
    private Long targetId;

    /** 举报原因类型 */
    @NotBlank(message = "请选择举报原因")
    @Size(max = 20, message = "原因类型不超过 20 字符")
    private String reasonType;

    /** 补充说明 */
    @Size(max = 500, message = "补充说明不超过 500 字符")
    private String content;
}
