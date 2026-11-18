package com.bbs.bbsadmin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.Dictionary;
import com.bbs.bbsadmin.entity.DictionaryType;
import com.bbs.bbsadmin.entity.dto.DictionaryPageQuery;
import com.bbs.bbsadmin.entity.dto.DictionarySaveDTO;
import com.bbs.bbsadmin.entity.vo.DictionaryVO;
import com.bbs.bbsadmin.mapper.DictionaryTypeMapper;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "DictionaryController", description = "字典项管理")
@RestController
@RequestMapping("admin/dictionary")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private DictionaryTypeMapper dictionaryTypeMapper;

    @Autowired
    private Authz authz;

    @Operation(summary = "分页查询字典项")
    @RequireAuth(adminOnly = true)
    @PostMapping("/page")
    public R<Map<String, Object>> page(@RequestBody DictionaryPageQuery query) {
        IPage<Dictionary> page = dictionaryService.pageQuery(query);
        List<DictionaryVO> rows = page.getRecords().stream().map(DictionaryVO::from).collect(Collectors.toList());
        fillTypeName(rows);
        Map<String, Object> data = new HashMap<>();
        data.put("list", rows);
        data.put("total", page.getTotal());
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        return R.data(data);
    }

    @Operation(summary = "按类型编码取启用中的字典项")
    @PostMapping("/options")
    public R<List<Dictionary>> options(@RequestBody Map<String, String> body) {
        String typeCode = body == null ? null : body.get("typeCode");
        return R.data(dictionaryService.listOptions(typeCode));
    }

    @Operation(summary = "新增字典项")
    @RequireAuth(adminOnly = true)
    @PostMapping("/create")
    public R<Long> create(@Valid @RequestBody DictionarySaveDTO dto) {
        authz.assertAdmin();
        return R.data(dictionaryService.create(dto));
    }

    @Operation(summary = "编辑字典项")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/update")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody DictionarySaveDTO dto) {
        authz.assertAdmin();
        dictionaryService.update(id, dto);
        return R.success();
    }

    @Operation(summary = "删除字典项")
    @RequireAuth(adminOnly = true)
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Map<String, Long> body) {
        authz.assertAdmin();
        dictionaryService.delete(body.get("id"));
        return R.success();
    }

    @Operation(summary = "启用/停用字典项")
    @RequireAuth(adminOnly = true)
    @PostMapping("/{id}/changeStatus")
    public R<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        authz.assertAdmin();
        dictionaryService.changeStatus(id, body.get("status"));
        return R.success();
    }

    /** 列表补类型名, 避免前端逐条再查 */
    private void fillTypeName(List<DictionaryVO> rows) {
        if (rows == null || rows.isEmpty()) return;
        Set<Long> typeIds = rows.stream().map(DictionaryVO::getTypeId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (typeIds.isEmpty()) return;
        Map<Long, String> nameMap = dictionaryTypeMapper.selectBatchIds(typeIds).stream()
                .collect(Collectors.toMap(DictionaryType::getId, DictionaryType::getName, (a, b) -> a));
        for (DictionaryVO v : rows) {
            String name = nameMap.get(v.getTypeId());
            if (StringUtils.hasText(name)) {
                v.setTypeName(name);
            }
        }
    }
}
