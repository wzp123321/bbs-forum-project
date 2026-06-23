package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 板块 VO
 */
@Data
public class CategoryVO {

    private Long id;
    private String name;
    private String description;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static CategoryVO from(Category c) {
        if (c == null) return null;
        CategoryVO v = new CategoryVO();
        BeanUtils.copyProperties(c, v);
        return v;
    }
}
