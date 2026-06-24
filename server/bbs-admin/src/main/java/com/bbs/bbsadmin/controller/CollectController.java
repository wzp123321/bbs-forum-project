package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.CollectRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "CollectController", description = "收藏管理")
@RestController
@RequestMapping("admin/collect")
public class CollectController {

    @Autowired
    private CollectRecordService collectRecordService;

    @Operation(summary = "收藏帖子")
    @RequireAuth
    @PostMapping("/{postId}")
    public R<Void> collect(@PathVariable Long postId) {
        collectRecordService.collect(postId);
        return R.success();
    }

    @Operation(summary = "取消收藏")
    @RequireAuth
    @DeleteMapping("/{postId}")
    public R<Void> cancel(@PathVariable Long postId) {
        collectRecordService.cancel(postId);
        return R.success();
    }

    @Operation(summary = "是否已收藏")
    @RequireAuth
    @GetMapping("/{postId}/status")
    public R<Map<String, Object>> status(@PathVariable Long postId) {
        Map<String, Object> data = new HashMap<>();
        data.put("collected", collectRecordService.isCollected(postId));
        return R.data(data);
    }

    @Operation(summary = "我收藏的帖子")
    @RequireAuth
    @GetMapping("/page")
    public R<Map<String, Object>> pageMyCollected(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<PostVO> page = collectRecordService.pageMyCollectedPosts(AuthContext.userId(), pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }
}
