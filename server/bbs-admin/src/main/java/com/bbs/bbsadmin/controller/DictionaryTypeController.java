package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.DictionaryType;
import com.bbs.bbsadmin.entity.dto.DictionaryTypeSaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.vo.DictionaryTypeVO;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.DictionaryTypeService;
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
import java.util.stream.Collectors;

@Tag(name = "DictionaryTypeController", description = "字典类型管理")
@RestController
@RequestMapping("admin/dictionaryType")
public class DictionaryTypeController {

    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    @Autowired
    private Authz authz;

    @Operation(summary = "分页查询字典类型")
    @RequireAuth(adminOnly = true)
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody PageQuery query) {
        IPage<DictionaryType> page = dictionaryTypeService.pageQuery(query);
        List<DictionaryTypeVO> rows = page.getRecords().stream()
                .map(DictionaryTypeVO::from).collect(Collectors.toList());
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "启用中的字典类型列表")
    @PostMapping("/list")
    public R<List<DictionaryTypeVO>> list() {
        return R.data(dictionaryTypeService.listEnabled().stream()
                .map(DictionaryTypeVO::from).collect(Collectors.toList()));
    }

    @Operation(summary = "新增字典类型")
    @RequireAuth(adminOnly = true)
    @PostMapping("/create")
    public R<Long> create(@Valid @RequestBody DictionaryTypeSaveDTO dto) {
        authz.assertAdmin();
        return R.data(dictionaryTypeService.create(dto));
    }

    @Operation(summary = "编辑字典类型")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/update")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody DictionaryTypeSaveDTO dto) {
        authz.assertAdmin();
        dictionaryTypeService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除字典类型")
    @RequireAuth(adminOnly = true)
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Map<String, Long> body) {
        authz.assertAdmin();
        dictionaryTypeService.delete(body.get("id"));
        return R.success();
    }
}
