package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bbs.bbsadmin.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.RegisterDTO;
import com.bbs.bbsadmin.entity.dto.UserUpdateDTO;

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

    /**
     * 按账号查找用户,依次匹配 userName / email / phone
     * @param account 登录账号
     * @return 命中返回实体,未命中返回 null
     */
    UserInfo findByAccount(String account);

    /**
     * 修改密码
     * @param userId      用户ID
     * @param newPassword 已加密的密码
     * @return 是否成功
     */
    boolean updatePassword(String userId, String newPassword);

    /**
     * 注册 (用户名已加密)
     * @return 新用户ID
     */
    String register(RegisterDTO dto);

    /**
     * 分页查询
     */
    IPage<UserInfo> pageQuery(PageQuery query);

    /**
     * 修改资料
     */
    boolean updateProfile(String userId, UserUpdateDTO dto);
}
