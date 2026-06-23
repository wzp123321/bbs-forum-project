package com.bbs.bbsadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论 (支持楼中楼)
 */
@Getter
@Setter
@TableName("bbs_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("post_id")
    private Long postId;

    @TableField("user_id")
    private String userId;

    /** 父评论ID,0顶级 */
    @TableField("parent_id")
    private Long parentId;

    /** 回复给谁(楼中楼) */
    @TableField("reply_to_user_id")
    private String replyToUserId;

    @TableField("content")
    private String content;

    @TableField("like_count")
    private Integer likeCount;

    /** 1正常 0已删 */
    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
