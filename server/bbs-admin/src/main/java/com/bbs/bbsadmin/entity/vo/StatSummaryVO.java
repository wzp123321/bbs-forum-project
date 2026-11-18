package com.bbs.bbsadmin.entity.vo;

import lombok.Data;

/**
 * 站点公开统计 (用户端首页使用)
 */
@Data
public class StatSummaryVO {

    /** 注册用户数 */
    private Long userCount;

    /** 正常状态帖子数 */
    private Long postCount;

    /** 启用板块数 */
    private Long categoryCount;
}
