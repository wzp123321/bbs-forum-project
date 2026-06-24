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
 * 反馈
 */
@Getter
@Setter
@TableName("bbs_feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    /** 类型: bug/建议/投诉/其他 */
    @TableField("type")
    private String type;

    @TableField("content")
    private String content;

    @TableField("reply")
    private String reply;

    @TableField("reply_user_id")
    private String replyUserId;

    @TableField("reply_time")
    private LocalDateTime replyTime;

    /** 0未处理 1已处理 */
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
