package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.CollectRecord;
import com.bbs.bbsadmin.entity.vo.PostVO;

public interface CollectRecordService extends IService<CollectRecord> {

    /**
     * 收藏
     */
    boolean collect(Long postId);

    /**
     * 取消收藏
     */
    boolean cancel(Long postId);

    /**
     * 是否已收藏
     */
    boolean isCollected(Long postId);

    /**
     * 分页查询我收藏的帖子
     */
    IPage<PostVO> pageMyCollectedPosts(String userId, int pageNum, int pageSize);
}
