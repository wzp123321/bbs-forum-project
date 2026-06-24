package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.LikeRecord;
import com.bbs.bbsadmin.entity.vo.PostVO;

public interface LikeRecordService extends IService<LikeRecord> {

    /**
     * 点赞 (通用)
     * @param targetType 1帖子 2评论
     * @param targetId 目标ID
     * @return true=点赞成功 false=已点赞过
     */
    boolean like(Integer targetType, Long targetId);

    /**
     * 取消点赞
     */
    boolean cancel(Integer targetType, Long targetId);

    /**
     * 是否已点赞
     */
    boolean isLiked(Integer targetType, Long targetId);

    /**
     * 分页查询我点赞的帖子
     */
    IPage<PostVO> pageMyLikedPosts(String userId, int pageNum, int pageSize);
}
