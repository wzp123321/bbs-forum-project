package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.dto.FeedbackPageQuery;
import com.bbs.bbsadmin.entity.dto.FeedbackReplyDTO;
import com.bbs.bbsadmin.entity.dto.FeedbackSaveDTO;
import com.bbs.bbsadmin.entity.vo.FeedbackVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.AuditLog;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.FeedbackService;
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

@Tag(name = "FeedbackController", description = "反馈管理")
@RestController
@RequestMapping("admin/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Operation(summary = "分页查询反馈")
    @RequireAuth
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody FeedbackPageQuery query) {
        IPage<FeedbackVO> page = feedbackService.pageQueryVO(query);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "反馈详情")
    @RequireAuth
    @PostMapping("/detail")
    public R<FeedbackVO> detail(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return R.data(feedbackService.detail(id));
    }

    @Operation(summary = "提交反馈")
    @RequireAuth
    @RateLimit(key = "feedback", capacity = 3, refillSeconds = 60, message = "反馈过于频繁")
    @AuditLog(action = "FEEDBACK_CREATE", description = "提交反馈", targetType = "FEEDBACK")
    @PostMapping("/create")
    public R<Long> create(@Valid @RequestBody FeedbackSaveDTO dto) {
        return R.data(feedbackService.create(dto));
    }

    @Operation(summary = "回复反馈")
    @RequireAuth
    @AuditLog(action = "FEEDBACK_REPLY", description = "回复反馈", targetType = "FEEDBACK", targetIdSpEl = "#id")
    @PostMapping("/{id}/reply")
    public R<Void> reply(@PathVariable Long id, @Valid @RequestBody FeedbackReplyDTO dto) {
        feedbackService.reply(id, dto);
        return R.success();
    }

    @Operation(summary = "删除反馈")
    @RequireAuth
    @AuditLog(action = "FEEDBACK_DELETE", description = "删除反馈", targetType = "FEEDBACK", targetIdSpEl = "#id")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        feedbackService.delete(id);
        return R.success();
    }
}
