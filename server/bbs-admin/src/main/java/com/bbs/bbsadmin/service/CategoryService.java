package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.dto.CategorySaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;

public interface CategoryService extends IService<Category> {

    /**
     * 分页查询板块
     */
    IPage<Category> pageQuery(PageQuery query);

    /**
     * 查询启用中的板块(按 sort 升序),用于下拉
     */
    java.util.List<Category> listEnabled();

    /**
     * 新增
     * @return 新板块ID
     */
    Long create(CategorySaveDTO dto);

    /**
     * 编辑
     */
    boolean update(Long id, CategorySaveDTO dto);
}
