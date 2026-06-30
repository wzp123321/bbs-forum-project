package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.dto.ReportHandleDTO;
import com.bbs.bbsadmin.entity.dto.ReportPageQuery;
import com.bbs.bbsadmin.entity.dto.ReportSaveDTO;
import com.bbs.bbsadmin.entity.vo.ReportVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.AuditLog;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "ReportController", description = "举报管理")
@RestController
@RequestMapping("admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Operation(summary = "分页查询举报")
    @RequireAuth
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody ReportPageQuery query) {
        IPage<ReportVO> page = reportService.pageQueryVO(query);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "举报详情")
    @RequireAuth
    @PostMapping("/detail")
    public R<ReportVO> detail(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return R.data(reportService.detail(id));
    }

    @Operation(summary = "提交举报")
    @RequireAuth
    @RateLimit(key = "report", capacity = 5, refillSeconds = 60, message = "举报过于频繁")
    @AuditLog(action = "REPORT_CREATE", description = "提交举报", targetType = "REPORT", targetIdSpEl = "#dto.targetId")
    @PostMapping("/create")
    public R<Long> create(@Valid @RequestBody ReportSaveDTO dto) {
        return R.data(reportService.create(dto));
    }

    @Operation(summary = "处理举报(0驳回 1已处理)")
    @RequireAuth
    @AuditLog(action = "REPORT_HANDLE", description = "处理举报", targetType = "REPORT", targetIdSpEl = "#id")
    @PostMapping("/{id}/handle")
    public R<Void> handle(@PathVariable Long id, @Valid @RequestBody ReportHandleDTO dto) {
        reportService.handle(id, dto);
        return R.success();
    }

    @Operation(summary = "删除举报")
    @RequireAuth
    @AuditLog(action = "REPORT_DELETE", description = "删除举报", targetType = "REPORT", targetIdSpEl = "#id")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        reportService.delete(id);
        return R.success();
    }
}
