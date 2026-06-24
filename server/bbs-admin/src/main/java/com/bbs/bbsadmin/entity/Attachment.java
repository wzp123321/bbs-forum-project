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
 * 附件
 */
@Getter
@Setter
@TableName("bbs_attachment")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 上传人 */
    @TableField("user_id")
    private String userId;

    /** 业务类型: post/comment/avatar */
    @TableField("biz_type")
    private String bizType;

    /** 业务ID(帖子/评论ID等) */
    @TableField("biz_id")
    private Long bizId;

    /** 原始文件名 */
    @TableField("origin_name")
    private String originName;

    /** 存储文件名 */
    @TableField("file_name")
    private String fileName;

    /** 存储相对路径 */
    @TableField("file_path")
    private String filePath;

    /** 访问URL */
    @TableField("url")
    private String url;

    /** MIME 类型 */
    @TableField("content_type")
    private String contentType;

    /** 字节数 */
    @TableField("size")
    private Long size;

    /** 存储类型: local/oss */
    @TableField("storage_type")
    private String storageType;

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
