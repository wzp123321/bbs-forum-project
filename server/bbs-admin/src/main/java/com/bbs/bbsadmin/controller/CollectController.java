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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/collect")
    public R<Void> collect(@RequestBody Map<String, Long> body) {
        Long postId = body.get("postId");
        collectRecordService.collect(postId);
        return R.success();
    }

    @Operation(summary = "取消收藏")
    @RequireAuth
    @PostMapping("/cancel")
    public R<Void> cancel(@RequestBody Map<String, Long> body) {
        Long postId = body.get("postId");
        collectRecordService.cancel(postId);
        return R.success();
    }

    @Operation(summary = "是否已收藏")
    @RequireAuth
    @PostMapping("/status")
    public R<Map<String, Object>> status(@RequestBody Map<String, Long> body) {
        Long postId = body.get("postId");
        Map<String, Object> data = new HashMap<>();
        data.put("collected", collectRecordService.isCollected(postId));
        return R.data(data);
    }

    @Operation(summary = "我收藏的帖子")
    @RequireAuth
    @PostMapping("/page")
    public R<Map<String, Object>> pageMyCollected(@RequestBody Map<String, Object> body) {
        int pageNum = body.get("pageNum") != null ? Integer.parseInt(body.get("pageNum").toString()) : 1;
        int pageSize = body.get("pageSize") != null ? Integer.parseInt(body.get("pageSize").toString()) : 10;
        IPage<PostVO> page = collectRecordService.pageMyCollectedPosts(AuthContext.userId(), pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }
}
