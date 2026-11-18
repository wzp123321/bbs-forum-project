package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.LikeRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    // bbs_like 无 deleted 列, 且带 uk_like(user_id,target_id,target_type) 唯一键:
    // 取消点赞必须物理删除, 否则旧行占着唯一键, 再次点赞会插入失败
    @Select("SELECT id FROM bbs_like WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId} LIMIT 1")
    Long findOne(@Param("userId") String userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    @Delete("DELETE FROM bbs_like WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    int cancelLike(@Param("userId") String userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);
}
