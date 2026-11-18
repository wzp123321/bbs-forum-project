package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.vo.NotificationVO;

/**
 * 互动通知
 */
public interface NotificationService {

    /**
     * 写入一条通知。接收者与触发者相同时直接忽略。
     *
     * @param receiverId 接收者 user_id
     * @param actorId    触发者 user_id
     * @param type       见 Notification.TYPE_*
     * @param postId     关联帖子, 无则 0
     * @param commentId  关联评论, 无则 0
     * @param content    摘要
     */
    void push(String receiverId, String actorId, int type, long postId, long commentId, String content);

    /**
     * 撤销互动时删除对应通知
     */
    void revoke(String receiverId, String actorId, int type, long postId, long commentId);

    long unreadCount(String userId);

    IPage<NotificationVO> pageQuery(String userId, boolean onlyUnread, int pageNum, int pageSize);

    boolean markRead(Long id, String userId);

    int markAllRead(String userId);
}
