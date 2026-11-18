package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Comment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 评论 VO
 */
@Data
public class CommentVO {

    private Long id;
    private Long postId;
    private String postTitle;
    private String userId;
    private String userName;
    /** 评论者昵称 (为空时已回退为 userName) */
    private String nickname;
    /** 评论者头像 URL */
    private String avatar;
    private Long parentId;
    private String replyToUserId;
    private String replyToUserName;
    /** 被回复者昵称 */
    private String replyToNickname;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static CommentVO from(Comment c) {
        if (c == null) return null;
        CommentVO v = new CommentVO();
        BeanUtils.copyProperties(c, v);
        return v;
    }
}
