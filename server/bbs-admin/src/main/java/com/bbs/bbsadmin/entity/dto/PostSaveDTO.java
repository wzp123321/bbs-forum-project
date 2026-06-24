package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 帖子新增/编辑入参
 */
@Data
public class PostSaveDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题不超过 200 字符")
    private String title;

    @NotBlank(message = "正文不能为空")
    private String content;

    /** 正文类型: 1富文本 2markdown */
    private Integer contentType;

    /** 板块ID,可不选 */
    @NotNull(message = "请选择板块")
    private Long categoryId;

    /** 标签ID列表 */
    private List<Long> tagIds;

    /** 1正常 0已删 2审核中 3审核不通过 */
    private Integer status;

    /** 是否置顶 1是 0否 */
    private Integer isTop;

    /** 是否精华 1是 0否 */
    private Integer isEssence;

    /** 阅读权限: 1公开 2登录可见 3粉丝可见 4仅作者 */
    private Integer readPerm;
}
