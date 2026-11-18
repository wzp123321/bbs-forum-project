package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.Dictionary;
import com.bbs.bbsadmin.entity.dto.DictionaryPageQuery;
import com.bbs.bbsadmin.entity.dto.DictionarySaveDTO;

import java.util.List;

/**
 * 字典项
 */
public interface DictionaryService {

    IPage<Dictionary> pageQuery(DictionaryPageQuery query);

    /**
     * 按类型编码取启用中的字典项, 供业务端下拉使用
     */
    List<Dictionary> listOptions(String typeCode);

    Long create(DictionarySaveDTO dto);

    boolean update(Long id, DictionarySaveDTO dto);

    boolean delete(Long id);

    boolean changeStatus(Long id, Integer status);
}
