package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.PostTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

public interface PostTagMapper extends BaseMapper<PostTag> {

    @Select("SELECT id, post_id, tag_id, create_time FROM bbs_post_tag WHERE post_id = #{postId}")
    List<PostTag> selectByPostId(@Param("postId") Long postId);

    @Select("<script>" +
            "SELECT id, post_id, tag_id, create_time FROM bbs_post_tag WHERE post_id IN " +
            "<foreach collection='postIds' item='pid' open='(' separator=',' close=')'>" +
            "#{pid}" +
            "</foreach>" +
            "</script>")
    List<PostTag> selectByPostIds(@Param("postIds") Collection<Long> postIds);

    @Delete("DELETE FROM bbs_post_tag WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Long postId);

    @Select("SELECT id, post_id, tag_id, create_time FROM bbs_post_tag WHERE tag_id = #{tagId}")
    List<PostTag> selectByTagId(@Param("tagId") Long tagId);
}
