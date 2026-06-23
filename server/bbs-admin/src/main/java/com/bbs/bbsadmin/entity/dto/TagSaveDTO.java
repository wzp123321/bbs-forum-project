package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签新增/编辑入参
 */
@Data
public class TagSaveDTO {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称不超过 50 字符")
    private String name;

    @Size(max = 200, message = "描述不超过 200 字符")
    private String description;

    /** 1启用 0停用 */
    private Integer status;
}
