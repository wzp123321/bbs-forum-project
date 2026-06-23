package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    @Select("<script>" +
            "SELECT id, post_id, user_id, parent_id, reply_to_user_id, content, like_count, status, " +
            "create_time, update_time, create_by, update_by, deleted FROM bbs_comment " +
            "WHERE post_id IN " +
            "<foreach collection='postIds' item='pid' open='(' separator=',' close=')'>" +
            "#{pid}" +
            "</foreach>" +
            "</script>")
    List<Comment> selectByPostIds(@Param("postIds") Collection<Long> postIds);

    @Update("UPDATE bbs_comment SET like_count = like_count + #{delta} WHERE id = #{id} AND deleted = 0")
    int incrLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
