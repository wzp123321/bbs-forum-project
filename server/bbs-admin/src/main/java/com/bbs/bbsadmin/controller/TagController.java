package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.Tag;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.TagSaveDTO;
import com.bbs.bbsadmin.entity.vo.TagVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.stream.Collectors;

@io.swagger.v3.oas.annotations.tags.Tag(name = "TagController", description = "标签管理")
@RestController
@RequestMapping("admin/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @Operation(summary = "分页查询标签")
    @RequireAuth
    @GetMapping("/page")
    public R<Map<String, Object>> page(PageQuery query) {
        IPage<Tag> page = tagService.pageQuery(query);
        List<TagVO> rows = page.getRecords().stream().map(TagVO::from).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "全部标签 (启用中,下拉用)")
    @GetMapping("/list")
    public R<List<TagVO>> list() {
        List<Tag> all = tagService.lambdaQuery()
                .eq(Tag::getStatus, 1)
                .orderByDesc(Tag::getCreateTime)
                .list();
        List<TagVO> rows = all.stream().map(TagVO::from).collect(Collectors.toList());
        return R.data(rows);
    }

    @Operation(summary = "标签详情")
    @RequireAuth
    @GetMapping("/{id}")
    public R<TagVO> detail(@PathVariable Long id) {
        Tag t = tagService.getById(id);
        return R.data(TagVO.from(t));
    }

    @Operation(summary = "新增标签")
    @RequireAuth
    @PostMapping
    public R<Long> create(@Valid @RequestBody TagSaveDTO dto) {
        return R.data(tagService.create(dto));
    }

    @Operation(summary = "编辑标签")
    @RequireAuth
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody TagSaveDTO dto) {
        tagService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除标签")
    @RequireAuth
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        tagService.removeById(id);
        return R.success();
    }
}
