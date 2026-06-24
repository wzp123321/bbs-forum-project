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
 * 操作审计日志
 * - 异步写入, 不阻塞主业务
 * - 失败不影响业务结果
 * - 90 天后由定时任务清理 (TODO)
 */
@Getter
@Setter
@TableName("bbs_operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 操作用户 ID (未登录时为 null) */
    @TableField("user_id")
    private String userId;

    /** 操作用户名 */
    @TableField("user_name")
    private String userName;

    /** 操作类型: LOGIN/POST_CREATE/POST_DELETE/... */
    @TableField("action")
    private String action;

    /** 操作描述 */
    @TableField("description")
    private String description;

    /** 目标类型: POST/COMMENT/REPORT/FEEDBACK/... */
    @TableField("target_type")
    private String targetType;

    /** 目标 ID */
    @TableField("target_id")
    private String targetId;

    /** 请求 URI */
    @TableField("uri")
    private String uri;

    /** HTTP 方法 */
    @TableField("method")
    private String method;

    /** 客户端 IP */
    @TableField("ip")
    private String ip;

    /** User-Agent */
    @TableField("user_agent")
    private String userAgent;

    /** 是否成功 */
    @TableField("success")
    private Integer success;

    /** 失败原因 */
    @TableField("error_msg")
    private String errorMsg;

    /** 耗时 (毫秒) */
    @TableField("cost_ms")
    private Long costMs;

    /** 操作时间 */
    @TableField("create_time")
    private LocalDateTime createTime;
}
