package com.bbs.bbsadmin.entity.dto;

import lombok.Data;

/**
 * 通用分页查询
 */
@Data
public class PageQuery {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 关键字 */
    private String keyword;
}
