package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.CollectRecord;

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
}
