package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.FollowRecord;
import com.bbs.bbsadmin.entity.vo.UserVO;

import java.util.List;

public interface FollowRecordService extends IService<FollowRecord> {

    /**
     * 关注
     */
    boolean follow(String followUserId);

    /**
     * 取消关注
     */
    boolean cancel(String followUserId);

    /**
     * 是否已关注
     */
    boolean isFollowing(String followUserId);

    /**
     * 我的粉丝ID列表
     */
    List<String> listFollowerIds(String userId);

    /**
     * 我的关注ID列表
     */
    List<String> listFollowingIds(String userId);

    /**
     * 粉丝数
     */
    long countFollowers(String userId);

    /**
     * 关注数
     */
    long countFollowing(String userId);

    /**
     * 分页查询我的粉丝 (返回 UserVO)
     */
    IPage<UserVO> pageFollowers(String userId, int pageNum, int pageSize);

    /**
     * 分页查询我的关注 (返回 UserVO)
     */
    IPage<UserVO> pageFollowing(String userId, int pageNum, int pageSize);
}
