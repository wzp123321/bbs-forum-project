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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @AuditLog(action = "LIKE", description = "点赞", targetIdSpEl = "#body.targetId")
    @PostMapping("/like")
    public R<Void> like(@RequestBody Map<String, Object> body) {
        Integer targetType = (Integer) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        likeRecordService.like(targetType, targetId);
        return R.success();
    }

    @Operation(summary = "取消点赞")
    @RequireAuth
    @RateLimit(key = "like:cancel", capacity = 60, refillSeconds = 60, message = "操作过于频繁")
    @AuditLog(action = "LIKE_CANCEL", description = "取消点赞", targetIdSpEl = "#body.targetId")
    @PostMapping("/cancel")
    public R<Void> cancel(@RequestBody Map<String, Object> body) {
        Integer targetType = (Integer) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        likeRecordService.cancel(targetType, targetId);
        return R.success();
    }

    @Operation(summary = "是否已点赞")
    @RequireAuth
    @PostMapping("/status")
    public R<Map<String, Object>> status(@RequestBody Map<String, Object> body) {
        Integer targetType = (Integer) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        Map<String, Object> data = new HashMap<>();
        data.put("liked", likeRecordService.isLiked(targetType, targetId));
        return R.data(data);
    }

    @Operation(summary = "我点赞的帖子")
    @RequireAuth
    @PostMapping("/page")
    public R<Map<String, Object>> pageMyLiked(@RequestBody Map<String, Object> body) {
        int pageNum = body.get("pageNum") != null ? Integer.parseInt(body.get("pageNum").toString()) : 1;
        int pageSize = body.get("pageSize") != null ? Integer.parseInt(body.get("pageSize").toString()) : 10;
        IPage<PostVO> page = likeRecordService.pageMyLikedPosts(AuthContext.userId(), pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }
}
