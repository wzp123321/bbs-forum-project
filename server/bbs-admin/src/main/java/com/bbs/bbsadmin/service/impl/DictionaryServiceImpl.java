package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Dictionary;
import com.bbs.bbsadmin.entity.DictionaryType;
import com.bbs.bbsadmin.entity.dto.DictionaryPageQuery;
import com.bbs.bbsadmin.entity.dto.DictionarySaveDTO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.DictionaryMapper;
import com.bbs.bbsadmin.mapper.DictionaryTypeMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
        implements DictionaryService {

    @Autowired
    private DictionaryTypeMapper dictionaryTypeMapper;

    @Override
    public IPage<Dictionary> pageQuery(DictionaryPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        LambdaQueryWrapper<Dictionary> wrapper = new LambdaQueryWrapper<>();
        if (query.getTypeId() != null) {
            wrapper.eq(Dictionary::getTypeId, query.getTypeId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Dictionary::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword();
            wrapper.and(w -> w.like(Dictionary::getLabel, kw).or().like(Dictionary::getValue, kw));
        }
        wrapper.orderByAsc(Dictionary::getSort).orderByDesc(Dictionary::getId);
        return baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Dictionary> listOptions(String typeCode) {
        if (!StringUtils.hasText(typeCode)) {
            return Collections.emptyList();
        }
        DictionaryType type = dictionaryTypeMapper.selectOne(new LambdaQueryWrapper<DictionaryType>()
                .eq(DictionaryType::getCode, typeCode)
                .eq(DictionaryType::getStatus, 1));
        if (type == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new LambdaQueryWrapper<Dictionary>()
                .eq(Dictionary::getTypeId, type.getId())
                .eq(Dictionary::getStatus, 1)
                .orderByAsc(Dictionary::getSort));
    }

    @Override
    public Long create(DictionarySaveDTO dto) {
        requireType(dto.getTypeId());
        Dictionary d = new Dictionary();
        d.setTypeId(dto.getTypeId());
        d.setLabel(dto.getLabel());
        d.setValue(dto.getValue());
        d.setSort(dto.getSort() == null ? 0 : dto.getSort());
        d.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        d.setCreateBy(AuthContext.userId());
        d.setCreateTime(LocalDateTime.now());
        baseMapper.insert(d);
        return d.getId();
    }

    @Override
    public boolean update(Long id, DictionarySaveDTO dto) {
        Dictionary exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "字典项不存在");
        }
        requireType(dto.getTypeId());
        Dictionary d = new Dictionary();
        d.setId(id);
        d.setTypeId(dto.getTypeId());
        d.setLabel(dto.getLabel());
        d.setValue(dto.getValue());
        d.setSort(dto.getSort() == null ? exist.getSort() : dto.getSort());
        d.setStatus(dto.getStatus() == null ? exist.getStatus() : dto.getStatus());
        d.setUpdateBy(AuthContext.userId());
        d.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(d) > 0;
    }

    @Override
    public boolean delete(Long id) {
        Dictionary exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "字典项不存在");
        }
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        Dictionary exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "字典项不存在");
        }
        Dictionary d = new Dictionary();
        d.setId(id);
        d.setStatus(status == null ? 0 : status);
        d.setUpdateBy(AuthContext.userId());
        d.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(d) > 0;
    }

    private void requireType(Long typeId) {
        if (typeId == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "字典类型不能为空");
        }
        DictionaryType type = dictionaryTypeMapper.selectById(typeId);
        if (type == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "字典类型不存在");
        }
    }
}
