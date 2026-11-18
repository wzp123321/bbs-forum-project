package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.vo.NotificationVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 互动通知
 * <p>
 * 所有接口都在登录态下调用, 且只作用于当前登录用户:
 * 入参里不接收 userId, 避免越权读别人的通知。
 */
@Tag(name = "NotificationController", description = "互动通知")
@RestController
@RequestMapping("admin/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "未读通知数")
    @RequireAuth
    @PostMapping("/unreadCount")
    public R<Map<String, Object>> unreadCount() {
        Map<String, Object> data = new HashMap<>();
        data.put("count", notificationService.unreadCount(AuthContext.userId()));
        return R.data(data);
    }

    @Operation(summary = "通知分页")
    @RequireAuth
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> b = body == null ? new HashMap<>() : body;
        int pageNum = toInt(b.get("pageNum"), 1);
        int pageSize = toInt(b.get("pageSize"), 10);
        boolean onlyUnread = Boolean.TRUE.equals(b.get("onlyUnread"));
        IPage<NotificationVO> page = notificationService.pageQuery(
                AuthContext.userId(), onlyUnread, pageNum, pageSize);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "单条已读")
    @RequireAuth
    @PostMapping("/read")
    public R<Void> read(@RequestBody Map<String, Object> body) {
        Long id = body.get("id") == null ? null : Long.valueOf(body.get("id").toString());
        if (id == null) {
            return R.fail("缺少通知 ID");
        }
        notificationService.markRead(id, AuthContext.userId());
        return R.success();
    }

    @Operation(summary = "全部已读")
    @RequireAuth
    @PostMapping("/readAll")
    public R<Map<String, Object>> readAll() {
        int n = notificationService.markAllRead(AuthContext.userId());
        Map<String, Object> data = new HashMap<>();
        data.put("updated", n);
        return R.data(data);
    }

    private int toInt(Object v, int def) {
        if (v == null) return def;
        try {
            return Integer.parseInt(v.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
