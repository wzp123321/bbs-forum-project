package com.bbs.bbsadmin.service;

import com.bbs.bbsadmin.entity.vo.StatOverviewVO;
import com.bbs.bbsadmin.entity.vo.StatSummaryVO;

/**
 * 站点统计
 */
public interface StatService {

    /**
     * 公开统计 (用户端首页统计条)
     */
    StatSummaryVO summary();

    /**
     * 后台仪表盘统计 (含今日新增、待处理举报、最近动态)
     */
    StatOverviewVO overview();
}
