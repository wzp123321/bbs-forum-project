package com.bbs.bbsadmin.entity.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 后台仪表盘统计
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StatOverviewVO extends StatSummaryVO {

    /** 今日新增帖子数 (服务器本地日期) */
    private Long todayPostCount;

    /** 待处理举报数 */
    private Long pendingReportCount;

    /** 最近动态, 最多 10 条 */
    private List<ActivityVO> recentActivities;
}
