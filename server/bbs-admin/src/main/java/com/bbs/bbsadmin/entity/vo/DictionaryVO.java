package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Dictionary;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 字典项 VO
 */
@Data
public class DictionaryVO {

    private Long id;
    private Long typeId;
    /** 冗余类型名, 列表页直接展示 */
    private String typeName;
    private String label;
    private String value;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;

    public static DictionaryVO from(Dictionary d) {
        if (d == null) return null;
        DictionaryVO v = new DictionaryVO();
        BeanUtils.copyProperties(d, v);
        return v;
    }
}
