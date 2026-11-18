package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型新增/编辑入参
 */
@Data
public class DictionaryTypeSaveDTO {

    @NotBlank(message = "类型名称不能为空")
    @Size(max = 50, message = "类型名称不超过 50 字符")
    private String name;

    @NotBlank(message = "类型编码不能为空")
    @Size(max = 50, message = "类型编码不超过 50 字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "类型编码需以字母开头, 只含字母/数字/下划线")
    private String code;

    @Size(max = 200, message = "描述不超过 200 字符")
    private String description;

    /** 1启用 0停用 */
    private Integer status;
}
