package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 举报处理入参 (管理后台)
 */
@Data
public class ReportHandleDTO {

    /** 0驳回 1处理(已下架) */
    @NotNull(message = "请选择处理结果")
    private Integer status;

    @Size(max = 500, message = "处理备注不超过 500 字符")
    private String handleRemark;
}
