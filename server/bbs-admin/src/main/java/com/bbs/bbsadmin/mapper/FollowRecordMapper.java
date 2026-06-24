package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.FollowRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FollowRecordMapper extends BaseMapper<FollowRecord> {

    @Select("SELECT id FROM bbs_follow WHERE user_id = #{userId} AND follow_user_id = #{followUserId} AND deleted = 0 LIMIT 1")
    Long findOne(@Param("userId") String userId, @Param("followUserId") String followUserId);

    @Delete("UPDATE bbs_follow SET deleted = 1 WHERE user_id = #{userId} AND follow_user_id = #{followUserId} AND deleted = 0")
    int softCancel(@Param("userId") String userId, @Param("followUserId") String followUserId);

    @Select("SELECT COUNT(1) FROM bbs_follow WHERE follow_user_id = #{followUserId} AND deleted = 0")
    long countFollowers(@Param("followUserId") String followUserId);

    @Select("SELECT COUNT(1) FROM bbs_follow WHERE user_id = #{userId} AND deleted = 0")
    long countFollowing(@Param("userId") String userId);
}
