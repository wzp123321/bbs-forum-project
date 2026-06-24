package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.dto.CommentPageQuery;
import com.bbs.bbsadmin.entity.dto.CommentSaveDTO;
import com.bbs.bbsadmin.entity.vo.CommentVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RateLimit;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.CommentService;
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

@Tag(name = "CommentController", description = "评论管理")
@RestController
@RequestMapping("admin/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "分页查询评论")
    @GetMapping("/page")
    public R<Map<String, Object>> page(CommentPageQuery query) {
        IPage<CommentVO> page = commentService.pageQueryVO(query);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "评论详情")
    @RequireAuth
    @GetMapping("/{id}")
    public R<CommentVO> detail(@PathVariable Long id) {
        return R.data(commentService.detail(id));
    }

    @Operation(summary = "新增评论")
    @RequireAuth
    @RateLimit(key = "comment:create", capacity = 20, refillSeconds = 60, message = "评论过于频繁")
    @PostMapping
    public R<Long> create(@Valid @RequestBody CommentSaveDTO dto) {
        return R.data(commentService.create(dto));
    }

    @Operation(summary = "编辑评论")
    @RequireAuth
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody CommentSaveDTO dto) {
        commentService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除评论")
    @RequireAuth
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return R.success();
    }

    @Operation(summary = "修改评论状态")
    @RequireAuth
    @PutMapping("/{id}/status")
    public R<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body == null ? null : body.get("status");
        commentService.changeStatus(id, status);
        return R.success();
    }
}
