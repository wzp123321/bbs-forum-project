package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.FollowRecordService;
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

@Tag(name = "FollowController", description = "关注管理")
@RestController
@RequestMapping("admin/follow")
public class FollowController {

    @Autowired
    private FollowRecordService followRecordService;

    @Operation(summary = "关注")
    @RequireAuth
    @PostMapping("/{followUserId}")
    public R<Void> follow(@PathVariable String followUserId) {
        followRecordService.follow(followUserId);
        return R.success();
    }

    @Operation(summary = "取消关注")
    @RequireAuth
    @DeleteMapping("/{followUserId}")
    public R<Void> cancel(@PathVariable String followUserId) {
        followRecordService.cancel(followUserId);
        return R.success();
    }

    @Operation(summary = "是否已关注")
    @RequireAuth
    @GetMapping("/{followUserId}/status")
    public R<Map<String, Object>> status(@PathVariable String followUserId) {
        Map<String, Object> data = new HashMap<>();
        data.put("following", followRecordService.isFollowing(followUserId));
        return R.data(data);
    }

    @Operation(summary = "粉丝数/关注数")
    @RequireAuth
    @GetMapping("/count")
    public R<Map<String, Object>> count(@RequestParam String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("followers", followRecordService.countFollowers(userId));
        data.put("following", followRecordService.countFollowing(userId));
        return R.data(data);
    }
}
