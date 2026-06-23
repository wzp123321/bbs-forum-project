package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Tag;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.TagSaveDTO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.TagMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public IPage<Tag> pageQuery(PageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Tag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Tag::getName, query.getKeyword());
        }
        wrapper.orderByDesc(Tag::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Long create(TagSaveDTO dto) {
        long exists = baseMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, dto.getName()));
        if (exists > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "标签名称已存在");
        }
        Tag t = new Tag();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        t.setPostCount(0);
        t.setCreateBy(AuthContext.userId());
        t.setCreateTime(LocalDateTime.now());
        baseMapper.insert(t);
        return t.getId();
    }

    @Override
    public boolean update(Long id, TagSaveDTO dto) {
        Tag exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "标签不存在");
        }
        long dup = baseMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, dto.getName())
                .ne(Tag::getId, id));
        if (dup > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "标签名称已存在");
        }
        Tag t = new Tag();
        t.setId(id);
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setStatus(dto.getStatus());
        t.setUpdateBy(AuthContext.userId());
        t.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(t) > 0;
    }
}
