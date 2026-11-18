package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Dictionary;
import com.bbs.bbsadmin.entity.DictionaryType;
import com.bbs.bbsadmin.entity.dto.DictionaryTypeSaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.DictionaryMapper;
import com.bbs.bbsadmin.mapper.DictionaryTypeMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.DictionaryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DictionaryTypeServiceImpl extends ServiceImpl<DictionaryTypeMapper, DictionaryType>
        implements DictionaryTypeService {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Override
    public IPage<DictionaryType> pageQuery(PageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        LambdaQueryWrapper<DictionaryType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword();
            wrapper.and(w -> w.like(DictionaryType::getName, kw).or().like(DictionaryType::getCode, kw));
        }
        wrapper.orderByDesc(DictionaryType::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<DictionaryType> listEnabled() {
        return baseMapper.selectList(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getStatus, 1)
                .orderByAsc(DictionaryType::getId));
    }

    @Override
    public Long create(DictionaryTypeSaveDTO dto) {
        long sameName = baseMapper.selectCount(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getName, dto.getName()));
        if (sameName > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "类型名称已存在");
        }
        long sameCode = baseMapper.selectCount(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getCode, dto.getCode()));
        if (sameCode > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "类型编码已存在");
        }
        DictionaryType t = new DictionaryType();
        t.setName(dto.getName());
        t.setCode(dto.getCode());
        t.setDescription(dto.getDescription());
        t.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        t.setCreateBy(AuthContext.userId());
        t.setCreateTime(LocalDateTime.now());
        baseMapper.insert(t);
        return t.getId();
    }

    @Override
    public boolean update(Long id, DictionaryTypeSaveDTO dto) {
        DictionaryType exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "字典类型不存在");
        }
        long sameName = baseMapper.selectCount(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getName, dto.getName())
                .ne(DictionaryType::getId, id));
        if (sameName > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "类型名称已存在");
        }
        long sameCode = baseMapper.selectCount(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getCode, dto.getCode())
                .ne(DictionaryType::getId, id));
        if (sameCode > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "类型编码已存在");
        }
        DictionaryType t = new DictionaryType();
        t.setId(id);
        t.setName(dto.getName());
        t.setCode(dto.getCode());
        t.setDescription(dto.getDescription());
        t.setStatus(dto.getStatus() == null ? exist.getStatus() : dto.getStatus());
        t.setUpdateBy(AuthContext.userId());
        t.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(t) > 0;
    }

    @Override
    public boolean delete(Long id) {
        DictionaryType exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "字典类型不存在");
        }
        Long children = dictionaryMapper.selectCount(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getTypeId, id));
        if (children != null && children > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "仍有 " + children + " 条字典项, 请先删除");
        }
        return baseMapper.deleteById(id) > 0;
    }
}
