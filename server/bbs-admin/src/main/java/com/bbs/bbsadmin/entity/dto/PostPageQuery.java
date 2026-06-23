package com.bbs.bbsadmin.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostPageQuery extends PageQuery {

    /** 板块ID */
    private Long categoryId;

    /** 标签ID */
    private Long tagId;

    /** 状态: 1正常 0已删 2审核中 3审核不通过 */
    private Integer status;

    /** 发帖人ID */
    private String userId;
}
