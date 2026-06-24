package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Tag;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.TagSaveDTO;

public interface TagService extends IService<Tag> {

    IPage<Tag> pageQuery(PageQuery query);

    java.util.List<Tag> listEnabled();

    Long create(TagSaveDTO dto);

    boolean update(Long id, TagSaveDTO dto);
}
