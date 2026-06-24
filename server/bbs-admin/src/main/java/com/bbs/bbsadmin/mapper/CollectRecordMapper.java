package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.CollectRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CollectRecordMapper extends BaseMapper<CollectRecord> {

    @Select("SELECT id FROM bbs_collect WHERE user_id = #{userId} AND post_id = #{postId} AND deleted = 0 LIMIT 1")
    Long findOne(@Param("userId") String userId, @Param("postId") Long postId);

    @Delete("UPDATE bbs_collect SET deleted = 1 WHERE user_id = #{userId} AND post_id = #{postId} AND deleted = 0")
    int softCancel(@Param("userId") String userId, @Param("postId") Long postId);
}
