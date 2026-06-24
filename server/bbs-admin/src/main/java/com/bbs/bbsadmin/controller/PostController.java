package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.dto.PostPageQuery;
import com.bbs.bbsadmin.entity.dto.PostSaveDTO;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.PostService;
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
import java.util.List;
import java.util.Map;

@Tag(name = "PostController", description = "帖子管理")
@RestController
@RequestMapping("admin/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "分页查询帖子")
    @GetMapping("/page")
    public R<Map<String, Object>> page(PostPageQuery query) {
        IPage<PostVO> page = postService.pageQueryVO(query);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "帖子详情")
    @GetMapping("/{id}")
    public R<PostVO> detail(@PathVariable Long id) {
        return R.data(postService.detail(id));
    }

    @Operation(summary = "新增帖子")
    @RequireAuth
    @PostMapping
    public R<Long> create(@Valid @RequestBody PostSaveDTO dto) {
        return R.data(postService.create(dto));
    }

    @Operation(summary = "编辑帖子")
    @RequireAuth
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody PostSaveDTO dto) {
        postService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除帖子")
    @RequireAuth
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        postService.removeById(id);
        return R.success();
    }

    @Operation(summary = "切换置顶")
    @RequireAuth
    @PutMapping("/{id}/top")
    public R<Void> toggleTop(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer isTop = body == null ? null : body.get("isTop");
        postService.toggleTop(id, isTop);
        return R.success();
    }

    @Operation(summary = "切换精华")
    @RequireAuth
    @PutMapping("/{id}/essence")
    public R<Void> toggleEssence(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer isEssence = body == null ? null : body.get("isEssence");
        postService.toggleEssence(id, isEssence);
        return R.success();
    }

    @Operation(summary = "修改状态")
    @RequireAuth
    @PutMapping("/{id}/status")
    public R<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body == null ? null : body.get("status");
        postService.changeStatus(id, status);
        return R.success();
    }
}
