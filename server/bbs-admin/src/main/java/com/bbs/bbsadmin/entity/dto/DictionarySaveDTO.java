package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典项新增/编辑入参
 */
@Data
public class DictionarySaveDTO {

    @NotNull(message = "字典类型不能为空")
    private Long typeId;

    @NotBlank(message = "展示名不能为空")
    @Size(max = 50, message = "展示名不超过 50 字符")
    private String label;

    @NotBlank(message = "存储值不能为空")
    @Size(max = 100, message = "存储值不超过 100 字符")
    private String value;

    private Integer sort;

    /** 1启用 0停用 */
    private Integer status;
}
