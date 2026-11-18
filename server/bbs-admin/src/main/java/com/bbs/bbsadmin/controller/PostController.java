package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.dto.DraftSaveDTO;
import com.bbs.bbsadmin.entity.dto.PostPageQuery;
import com.bbs.bbsadmin.entity.dto.PostSaveDTO;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.annotation.AuditLog;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.PostService;
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
import java.util.List;
import java.util.Map;

@Tag(name = "PostController", description = "帖子管理")
@RestController
@RequestMapping("admin/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private Authz authz;

    @Operation(summary = "分页查询帖子")
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody PostPageQuery query) {
        IPage<PostVO> page = postService.pageQueryVO(query);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "帖子详情")
    @PostMapping("/detail")
    public R<PostVO> detail(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        return R.data(postService.detail(id));
    }

    @Operation(summary = "新增帖子")
    @RequireAuth
    @RateLimit(key = "post:create", capacity = 10, refillSeconds = 60, message = "发帖过于频繁,请稍后再试")
    @PostMapping("/create")
    public R<Long> create(@Valid @RequestBody PostSaveDTO dto) {
        return R.data(postService.create(dto));
    }

    @Operation(summary = "编辑帖子")
    @RequireAuth
    @PostMapping("/{id}/update")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody PostSaveDTO dto) {
        postService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除帖子")
    @RequireAuth
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Map<String, Long> body) {
        Long id = body.get("id");
        if (id == null) {
            return R.fail("缺少帖子 ID");
        }
        Post exist = postService.getById(id);
        if (exist == null) {
            return R.success();
        }
        // 原先直接 removeById, 等于任何登录用户都能删别人的帖子
        authz.assertOwnerOrAdmin(exist.getUserId());
        postService.removeById(id);
        return R.success();
    }

    @Operation(summary = "切换置顶")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/toggleTop")
    public R<Void> toggleTop(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer isTop = body == null ? null : body.get("isTop");
        postService.toggleTop(id, isTop);
        return R.success();
    }

    @Operation(summary = "切换精华")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/toggleEssence")
    public R<Void> toggleEssence(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer isEssence = body == null ? null : body.get("isEssence");
        postService.toggleEssence(id, isEssence);
        return R.success();
    }

    @Operation(summary = "修改状态")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/changeStatus")
    public R<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body == null ? null : body.get("status");
        postService.changeStatus(id, status);
        return R.success();
    }

    @Operation(summary = "关注动态流")
    @RequireAuth
    @PostMapping("/followFeed")
    public R<Map<String, Object>> followFeed(@RequestBody(required = false) Map<String, Object> body) {
        int pageNum = toInt(body, "pageNum", 1);
        int pageSize = toInt(body, "pageSize", 10);
        IPage<PostVO> page = postService.followFeed(pageNum, pageSize);
        return R.data(toPageData(page));
    }

    @Operation(summary = "保存草稿")
    @RequireAuth
    @PostMapping("/saveDraft")
    public R<Long> saveDraft(@RequestBody DraftSaveDTO dto) {
        return R.data(postService.saveDraft(dto));
    }

    @Operation(summary = "我的草稿")
    @RequireAuth
    @PostMapping("/myDrafts")
    public R<Map<String, Object>> myDrafts(@RequestBody(required = false) Map<String, Object> body) {
        int pageNum = toInt(body, "pageNum", 1);
        int pageSize = toInt(body, "pageSize", 10);
        return R.data(toPageData(postService.myDrafts(pageNum, pageSize)));
    }

    @Operation(summary = "草稿详情")
    @RequireAuth
    @PostMapping("/draftDetail")
    public R<PostVO> draftDetail(@RequestBody Map<String, Long> body) {
        return R.data(postService.draftDetail(body == null ? null : body.get("id")));
    }

    @Operation(summary = "发布草稿")
    @RequireAuth
    @PostMapping("/{id}/publishDraft")
    public R<Map<String, Object>> publishDraft(@PathVariable Long id) {
        Integer status = postService.publishDraft(id);
        Map<String, Object> data = new HashMap<>();
        data.put("status", status);
        // 命中敏感词时转审核, 由前端提示"已提交审核"
        data.put("needAudit", status != null && status == 2);
        return R.data(data);
    }

    private Map<String, Object> toPageData(IPage<PostVO> page) {
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return data;
    }

    private int toInt(Map<String, Object> body, String key, int def) {
        if (body == null || body.get(key) == null) {
            return def;
        }
        try {
            return Integer.parseInt(body.get(key).toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
