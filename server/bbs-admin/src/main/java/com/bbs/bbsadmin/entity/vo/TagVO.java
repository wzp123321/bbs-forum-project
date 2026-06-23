package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Tag;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 标签 VO
 */
@Data
public class TagVO {

    private Long id;
    private String name;
    private String description;
    private Integer postCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static TagVO from(Tag t) {
        if (t == null) return null;
        TagVO v = new TagVO();
        BeanUtils.copyProperties(t, v);
        return v;
    }
}
