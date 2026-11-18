package com.bbs.bbsadmin.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 草稿保存入参
 * <p>
 * 与 {@link PostSaveDTO} 的区别: 标题与正文都允许为空(写一半就能存),
 * 由 Service 断言"至少有一项非空"; 不允许通过这里改 status, 草稿状态由服务端固定。
 */
@Data
public class DraftSaveDTO {

    /** 草稿 ID, 为空表示新建 */
    private Long id;

    private String title;

    private String content;

    /** 正文类型: 1富文本 2markdown */
    private Integer contentType;

    /** 板块 ID, 草稿阶段可不选, 发布时必须选 */
    private Long categoryId;

    /** 标签 ID 列表 */
    private List<Long> tagIds;
}
