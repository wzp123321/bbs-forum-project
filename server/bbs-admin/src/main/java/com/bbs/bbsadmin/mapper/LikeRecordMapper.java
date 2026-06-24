package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.LikeRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    @Select("SELECT id FROM bbs_like WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId} AND deleted = 0 LIMIT 1")
    Long findOne(@Param("userId") String userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    @Delete("UPDATE bbs_like SET deleted = 1 WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId} AND deleted = 0")
    int softCancel(@Param("userId") String userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);
}
