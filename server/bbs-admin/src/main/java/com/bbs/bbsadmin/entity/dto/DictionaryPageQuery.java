package com.bbs.bbsadmin.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典项分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictionaryPageQuery extends PageQuery {

    /** 所属字典类型ID */
    private Long typeId;

    /** 状态: 1启用 0停用, 空表示不限 */
    private Integer status;
}
