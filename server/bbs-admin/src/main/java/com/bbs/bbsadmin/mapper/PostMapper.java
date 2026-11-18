package com.bbs.bbsadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.bbsadmin.entity.Post;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface PostMapper extends BaseMapper<Post> {

    /**
     * 阅读数原子自增
     * 用 SQL 自增而非"读出 +1 再更新", 避免并发下的丢失更新
     */
    @Update("UPDATE bbs_post SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrViewCount(@Param("id") Long id);

    /**
     * 点赞数原子增减 (delta 为 1 或 -1)
     * 用 GREATEST 兜底, 避免取消点赞时被减成负数
     */
    @Update("UPDATE bbs_post SET like_count = GREATEST(like_count + #{delta}, 0) WHERE id = #{id} AND deleted = 0")
    int incrLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
