package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.dto.CategorySaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.vo.CategoryVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.CategoryService;
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
import java.util.stream.Collectors;

@Tag(name = "CategoryController", description = "板块管理")
@RestController
@RequestMapping("admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "分页查询板块")
    @RequireAuth
    @GetMapping("/page")
    public R<Map<String, Object>> page(PageQuery query) {
        IPage<Category> page = categoryService.pageQuery(query);
        List<CategoryVO> rows = page.getRecords().stream().map(CategoryVO::from).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "全部板块 (启用中,下拉用)")
    @GetMapping("/list")
    public R<List<CategoryVO>> list() {
        List<Category> all = categoryService.lambdaQuery()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort)
                .list();
        List<CategoryVO> rows = all.stream().map(CategoryVO::from).collect(Collectors.toList());
        return R.data(rows);
    }

    @Operation(summary = "板块详情")
    @RequireAuth
    @GetMapping("/{id}")
    public R<CategoryVO> detail(@PathVariable Long id) {
        Category c = categoryService.getById(id);
        return R.data(CategoryVO.from(c));
    }

    @Operation(summary = "新增板块")
    @RequireAuth
    @PostMapping
    public R<Long> create(@Valid @RequestBody CategorySaveDTO dto) {
        return R.data(categoryService.create(dto));
    }

    @Operation(summary = "编辑板块")
    @RequireAuth
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO dto) {
        categoryService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除板块")
    @RequireAuth
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return R.success();
    }
}
