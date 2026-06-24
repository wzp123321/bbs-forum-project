package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.annotation.AuditLog;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.LikeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "LikeController", description = "点赞管理")
@RestController
@RequestMapping("admin/like")
public class LikeController {

    @Autowired
    private LikeRecordService likeRecordService;

    @Operation(summary = "点赞 (1帖子 2评论)")
    @RequireAuth
    @RateLimit(key = "like", capacity = 60, refillSeconds = 60, message = "操作过于频繁")
    @AuditLog(action = "LIKE", description = "点赞", targetIdSpEl = "#targetId")
    @PostMapping("/{targetType}/{targetId}")
    public R<Void> like(@PathVariable Integer targetType, @PathVariable Long targetId) {
        likeRecordService.like(targetType, targetId);
        return R.success();
    }

    @Operation(summary = "取消点赞")
    @RequireAuth
    @RateLimit(key = "like:cancel", capacity = 60, refillSeconds = 60, message = "操作过于频繁")
    @AuditLog(action = "LIKE_CANCEL", description = "取消点赞", targetIdSpEl = "#targetId")
    @DeleteMapping("/{targetType}/{targetId}")
    public R<Void> cancel(@PathVariable Integer targetType, @PathVariable Long targetId) {
        likeRecordService.cancel(targetType, targetId);
        return R.success();
    }

    @Operation(summary = "是否已点赞")
    @RequireAuth
    @GetMapping("/{targetType}/{targetId}/status")
    public R<Map<String, Object>> status(@PathVariable Integer targetType, @PathVariable Long targetId) {
        Map<String, Object> data = new HashMap<>();
        data.put("liked", likeRecordService.isLiked(targetType, targetId));
        return R.data(data);
    }

    @Operation(summary = "我点赞的帖子")
    @RequireAuth
    @GetMapping("/page")
    public R<Map<String, Object>> pageMyLiked(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<PostVO> page = likeRecordService.pageMyLikedPosts(AuthContext.userId(), pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }
}
