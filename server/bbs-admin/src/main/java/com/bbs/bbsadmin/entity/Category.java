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
 * 板块 / 分类
 */
@Getter
@Setter
@TableName("bbs_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 板块名称 */
    @TableField("name")
    private String name;

    /** 板块描述 */
    @TableField("description")
    private String description;

    /** 排序,升序 */
    @TableField("sort")
    private Integer sort;

    /** 1启用 0停用 */
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

    /** 0未删 1已删 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
