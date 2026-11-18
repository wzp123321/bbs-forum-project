package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.DictionaryType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 字典类型 VO
 */
@Data
public class DictionaryTypeVO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private LocalDateTime createTime;

    public static DictionaryTypeVO from(DictionaryType t) {
        if (t == null) return null;
        DictionaryTypeVO v = new DictionaryTypeVO();
        BeanUtils.copyProperties(t, v);
        return v;
    }
}
