package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.dto.FeedbackPageQuery;
import com.bbs.bbsadmin.entity.dto.FeedbackReplyDTO;
import com.bbs.bbsadmin.entity.dto.FeedbackSaveDTO;
import com.bbs.bbsadmin.entity.vo.FeedbackVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @GetMapping("/page")
    public R<Map<String, Object>> page(FeedbackPageQuery query) {
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
    @GetMapping("/{id}")
    public R<FeedbackVO> detail(@PathVariable Long id) {
        return R.data(feedbackService.detail(id));
    }

    @Operation(summary = "提交反馈")
    @RequireAuth
    @PostMapping
    public R<Long> create(@Valid @RequestBody FeedbackSaveDTO dto) {
        return R.data(feedbackService.create(dto));
    }

    @Operation(summary = "回复反馈")
    @RequireAuth
    @PutMapping("/{id}/reply")
    public R<Void> reply(@PathVariable Long id, @Valid @RequestBody FeedbackReplyDTO dto) {
        feedbackService.reply(id, dto);
        return R.success();
    }

    @Operation(summary = "删除反馈")
    @RequireAuth
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return R.success();
    }
}
