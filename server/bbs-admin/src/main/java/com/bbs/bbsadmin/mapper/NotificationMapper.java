package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.Notification;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 写入通知, 命中唯一键时刷新时间并重置为未读
     * 用 upsert 而不是 "先查再插", 避免并发重复插入
     */
    @Insert("INSERT INTO bbs_notification (user_id, actor_id, type, post_id, comment_id, content, is_read, create_time) "
            + "VALUES (#{userId}, #{actorId}, #{type}, #{postId}, #{commentId}, #{content}, 0, NOW()) "
            + "ON DUPLICATE KEY UPDATE content = VALUES(content), is_read = 0, create_time = NOW()")
    int upsert(@Param("userId") String userId,
               @Param("actorId") String actorId,
               @Param("type") Integer type,
               @Param("postId") Long postId,
               @Param("commentId") Long commentId,
               @Param("content") String content);

    /**
     * 撤销互动时物理删除对应通知
     */
    @Delete("DELETE FROM bbs_notification WHERE user_id = #{userId} AND actor_id = #{actorId} "
            + "AND type = #{type} AND post_id = #{postId} AND comment_id = #{commentId}")
    int revoke(@Param("userId") String userId,
               @Param("actorId") String actorId,
               @Param("type") Integer type,
               @Param("postId") Long postId,
               @Param("commentId") Long commentId);

    /**
     * 单条已读: 带 user_id 条件, 别人的通知改不动
     */
    @Update("UPDATE bbs_notification SET is_read = 1 WHERE id = #{id} AND user_id = #{userId}")
    int markRead(@Param("id") Long id, @Param("userId") String userId);

    @Update("UPDATE bbs_notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    int markAllRead(@Param("userId") String userId);
}
