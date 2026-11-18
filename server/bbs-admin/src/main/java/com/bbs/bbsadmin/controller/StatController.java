package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.vo.StatOverviewVO;
import com.bbs.bbsadmin.entity.vo.StatSummaryVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.StatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "StatController", description = "站点统计")
@RestController
@RequestMapping("admin/stat")
public class StatController {

    @Autowired
    private StatService statService;

    @Autowired
    private Authz authz;

    @Operation(summary = "公开统计 (用户端首页)")
    @PostMapping("/summary")
    public R<StatSummaryVO> summary() {
        return R.data(statService.summary());
    }

    @Operation(summary = "仪表盘统计 (仅管理员)")
    @RequireAuth
    @PostMapping("/overview")
    public R<StatOverviewVO> overview() {
        authz.assertAdmin();
        return R.data(statService.overview());
    }
}
