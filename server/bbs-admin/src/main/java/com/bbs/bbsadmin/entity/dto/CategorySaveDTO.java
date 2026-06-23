package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 板块新增/编辑入参
 */
@Data
public class CategorySaveDTO {

    @NotBlank(message = "板块名称不能为空")
    @Size(max = 50, message = "板块名称不超过 50 字符")
    private String name;

    @Size(max = 200, message = "描述不超过 200 字符")
    private String description;

    /** 排序,升序 */
    private Integer sort;

    /** 1启用 0停用 */
    private Integer status;
}
