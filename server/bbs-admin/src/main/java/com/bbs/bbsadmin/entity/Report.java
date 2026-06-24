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
 * 举报
 */
@Getter
@Setter
@TableName("bbs_report")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 举报人ID */
    @TableField("user_id")
    private String userId;

    /** 举报目标: 1帖子 2评论 */
    @TableField("target_type")
    private Integer targetType;

    /** 目标ID */
    @TableField("target_id")
    private Long targetId;

    /** 举报原因类型 */
    @TableField("reason_type")
    private String reasonType;

    /** 补充说明 */
    @TableField("content")
    private String content;

    /** 0待处理 1已处理 2已驳回 */
    @TableField("status")
    private Integer status;

    /** 处理人 */
    @TableField("handle_user_id")
    private String handleUserId;

    /** 处理备注 */
    @TableField("handle_remark")
    private String handleRemark;

    /** 处理时间 */
    @TableField("handle_time")
    private LocalDateTime handleTime;

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
