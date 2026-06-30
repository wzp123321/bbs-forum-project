package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.FollowRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @PostMapping("/follow")
    public R<Void> follow(@RequestBody Map<String, String> body) {
        String followUserId = body.get("followUserId");
        followRecordService.follow(followUserId);
        return R.success();
    }

    @Operation(summary = "取消关注")
    @RequireAuth
    @PostMapping("/cancel")
    public R<Void> cancel(@RequestBody Map<String, String> body) {
        String followUserId = body.get("followUserId");
        followRecordService.cancel(followUserId);
        return R.success();
    }

    @Operation(summary = "是否已关注")
    @RequireAuth
    @PostMapping("/status")
    public R<Map<String, Object>> status(@RequestBody Map<String, String> body) {
        String followUserId = body.get("followUserId");
        Map<String, Object> data = new HashMap<>();
        data.put("following", followRecordService.isFollowing(followUserId));
        return R.data(data);
    }

    @Operation(summary = "粉丝数/关注数 (公开)")
    @PostMapping("/count")
    public R<Map<String, Object>> count(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        Map<String, Object> data = new HashMap<>();
        data.put("followers", followRecordService.countFollowers(userId));
        data.put("following", followRecordService.countFollowing(userId));
        return R.data(data);
    }

    @Operation(summary = "我的粉丝列表")
    @RequireAuth
    @PostMapping("/followers")
    public R<Map<String, Object>> pageFollowers(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        int pageNum = body.get("pageNum") != null ? Integer.parseInt(body.get("pageNum").toString()) : 1;
        int pageSize = body.get("pageSize") != null ? Integer.parseInt(body.get("pageSize").toString()) : 20;
        IPage<UserVO> page = followRecordService.pageFollowers(userId, pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "我的关注列表")
    @RequireAuth
    @PostMapping("/following")
    public R<Map<String, Object>> pageFollowing(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        int pageNum = body.get("pageNum") != null ? Integer.parseInt(body.get("pageNum").toString()) : 1;
        int pageSize = body.get("pageSize") != null ? Integer.parseInt(body.get("pageSize").toString()) : 20;
        IPage<UserVO> page = followRecordService.pageFollowing(userId, pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }
}
