package com.bbs.bbsadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 互动通知
 * <p>
 * 通知是流水: 撤销互动时物理删除, 不设 deleted 列。
 * 唯一键 uk_notify(user_id, actor_id, type, post_id, comment_id) 负责去重,
 * 因此 post_id / comment_id 用 0 占位而不是 NULL —— MySQL 唯一键对 NULL 不生效。
 */
@Getter
@Setter
@TableName("bbs_notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 赞了帖子 */
    public static final int TYPE_LIKE = 1;
    /** 评论了帖子 */
    public static final int TYPE_COMMENT = 2;
    /** 回复了评论 */
    public static final int TYPE_REPLY = 3;
    /** 关注了你 */
    public static final int TYPE_FOLLOW = 4;
    /** 系统公告 */
    public static final int TYPE_SYSTEM = 5;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 接收者 */
    @TableField("user_id")
    private String userId;

    /** 触发者, 系统公告为 null */
    @TableField("actor_id")
    private String actorId;

    @TableField("type")
    private Integer type;

    /** 关联帖子, 无则 0 */
    @TableField("post_id")
    private Long postId;

    /** 关联评论, 无则 0 */
    @TableField("comment_id")
    private Long commentId;

    @TableField("content")
    private String content;

    /** 0 未读 1 已读 */
    @TableField("is_read")
    private Integer isRead;

    @TableField("create_time")
    private LocalDateTime createTime;
}
