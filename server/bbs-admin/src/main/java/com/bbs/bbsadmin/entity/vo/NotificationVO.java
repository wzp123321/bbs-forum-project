package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Notification;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知 VO (展示用, 已带上触发者昵称头像与帖子标题)
 */
@Data
public class NotificationVO {

    private Long id;
    private Integer type;
    /** 类型文案: 赞 / 评论 / 回复 / 关注 / 公告 */
    private String typeText;
    private String actorId;
    /** 触发者展示名, 昵称优先 */
    private String actorName;
    private String actorAvatar;
    private Long postId;
    private Long commentId;
    private String postTitle;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;

    public static NotificationVO from(Notification n) {
        if (n == null) return null;
        NotificationVO v = new NotificationVO();
        v.setId(n.getId());
        v.setType(n.getType());
        v.setTypeText(typeText(n.getType()));
        v.setActorId(n.getActorId());
        v.setPostId(n.getPostId());
        v.setCommentId(n.getCommentId());
        v.setContent(n.getContent());
        v.setIsRead(n.getIsRead());
        v.setCreateTime(n.getCreateTime());
        return v;
    }

    private static String typeText(Integer type) {
        if (type == null) return "";
        switch (type) {
            case Notification.TYPE_LIKE: return "点赞";
            case Notification.TYPE_COMMENT: return "评论";
            case Notification.TYPE_REPLY: return "回复";
            case Notification.TYPE_FOLLOW: return "关注";
            case Notification.TYPE_SYSTEM: return "公告";
            default: return "";
        }
    }
}
