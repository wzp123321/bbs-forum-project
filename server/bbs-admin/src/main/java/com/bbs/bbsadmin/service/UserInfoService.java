package com.bbs.bbsadmin.service;

import com.bbs.bbsadmin.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhcao
 * @since 2024-07-01
 */
public interface UserInfoService extends IService<UserInfo> {

    UserInfo findByUserId(String userId);
}
