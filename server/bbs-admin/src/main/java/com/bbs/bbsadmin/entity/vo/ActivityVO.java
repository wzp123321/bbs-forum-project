package com.bbs.bbsadmin.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仪表盘最近动态
 */
@Data
public class ActivityVO {

    /** 事件类型: post 新帖 / comment 新评论 / report 新举报 */
    private String type;

    /** 对象ID */
    private Long id;

    /** 摘要, 已截断 */
    private String summary;

    /** 发生时间 */
    private LocalDateTime time;
}
