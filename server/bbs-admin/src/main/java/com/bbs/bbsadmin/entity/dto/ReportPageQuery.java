package com.bbs.bbsadmin.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 举报分页查询 (管理后台)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportPageQuery extends PageQuery {

    /** 举报目标: 1帖子 2评论 */
    private Integer targetType;

    /** 举报原因类型 */
    private String reasonType;

    /** 处理状态: 0待处理 1已处理 2已驳回 */
    private Integer status;

    /** 举报人ID */
    private String userId;
}
