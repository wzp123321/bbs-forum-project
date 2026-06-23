package com.bbs.bbsadmin.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentPageQuery extends PageQuery {

    /** 帖子ID */
    private Long postId;

    /** 评论人ID */
    private String userId;

    /** 父评论ID,0顶级 */
    private Long parentId;

    /** 状态: 1正常 0已删 */
    private Integer status;
}
