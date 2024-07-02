package com.bbs.bbsadmin.service.impl;

import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhcao
 * @since 2024-07-01
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public UserInfo findByUserId(String userId) {
        return userInfoMapper.selectById(userId);
    }
}
