package com.bbs.bbsadmin.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论新增/编辑入参
 */
@Data
public class CommentSaveDTO {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /** 父评论ID,0顶级 */
    private Long parentId;

    /** 回复给谁(楼中楼) */
    private String replyToUserId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不超过 1000 字符")
    private String content;

    /** 1正常 0已删 */
    private Integer status;
}
