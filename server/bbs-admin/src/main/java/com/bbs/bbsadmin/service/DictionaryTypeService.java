package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.DictionaryType;
import com.bbs.bbsadmin.entity.dto.DictionaryTypeSaveDTO;
import com.bbs.bbsadmin.entity.dto.PageQuery;

import java.util.List;

/**
 * 字典类型
 */
public interface DictionaryTypeService {

    IPage<DictionaryType> pageQuery(PageQuery query);

    /** 启用中的类型列表, 供下拉选择 */
    List<DictionaryType> listEnabled();

    Long create(DictionaryTypeSaveDTO dto);

    boolean update(Long id, DictionaryTypeSaveDTO dto);

    /**
     * 删除类型
     * 该类型下仍有字典项时拒绝删除
     */
    boolean delete(Long id);
}
