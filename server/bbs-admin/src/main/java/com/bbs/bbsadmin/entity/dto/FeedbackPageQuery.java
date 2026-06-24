package com.bbs.bbsadmin.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackPageQuery extends PageQuery {

    /** 类型 */
    private String type;

    /** 状态: 0未处理 1已处理 */
    private Integer status;

    /** 反馈人ID */
    private String userId;
}
