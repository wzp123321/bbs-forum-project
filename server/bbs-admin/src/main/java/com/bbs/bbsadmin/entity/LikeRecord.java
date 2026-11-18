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
 * 点赞 (通用: 帖子/评论)
 * <p>
 * 取消点赞采用物理删除: bbs_like 表没有 deleted 列, 且带唯一键
 * uk_like(user_id, target_id, target_type), 逻辑删除会让旧行占住唯一键,
 * 导致同一用户无法再次点赞。
 */
@Getter
@Setter
@TableName("bbs_like")
public class LikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    /** 1帖子 2评论 */
    @TableField("target_type")
    private Integer targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
