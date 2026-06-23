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
 * 帖子
 */
@Getter
@Setter
@TableName("bbs_post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 发帖人ID */
    @TableField("user_id")
    private String userId;

    /** 板块ID */
    @TableField("category_id")
    private Long categoryId;

    /** 标题 */
    @TableField("title")
    private String title;

    /** 正文 */
    @TableField("content")
    private String content;

    /** 正文类型: 1富文本 2markdown */
    @TableField("content_type")
    private Integer contentType;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("like_count")
    private Integer likeCount;

    @TableField("comment_count")
    private Integer commentCount;

    @TableField("collect_count")
    private Integer collectCount;

    /** 1正常 0已删 2审核中 3审核不通过 */
    @TableField("status")
    private Integer status;

    /** 是否置顶 1是 0否 */
    @TableField("is_top")
    private Integer isTop;

    /** 是否精华 1是 0否 */
    @TableField("is_essence")
    private Integer isEssence;

    @TableField("top_time")
    private LocalDateTime topTime;

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
