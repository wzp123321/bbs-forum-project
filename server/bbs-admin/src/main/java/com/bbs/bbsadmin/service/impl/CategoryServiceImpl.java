package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.dto.CategorySaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CategoryMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public IPage<Category> pageQuery(PageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Category> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Category::getName, query.getKeyword());
        }
        wrapper.orderByAsc(Category::getSort)
               .orderByDesc(Category::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public Long create(CategorySaveDTO dto) {
        // 校验名称唯一 (未软删)
        long exists = baseMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, dto.getName()));
        if (exists > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "板块名称已存在");
        }
        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setSort(dto.getSort() == null ? 0 : dto.getSort());
        c.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        c.setCreateBy(AuthContext.userId());
        c.setCreateTime(LocalDateTime.now());
        baseMapper.insert(c);
        return c.getId();
    }

    @Override
    public boolean update(Long id, CategorySaveDTO dto) {
        Category exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "板块不存在");
        }
        // 名称唯一性校验 (排除自身)
        long dup = baseMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, dto.getName())
                .ne(Category::getId, id));
        if (dup > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "板块名称已存在");
        }
        Category c = new Category();
        c.setId(id);
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setSort(dto.getSort());
        c.setStatus(dto.getStatus());
        c.setUpdateBy(AuthContext.userId());
        c.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(c) > 0;
    }
}
