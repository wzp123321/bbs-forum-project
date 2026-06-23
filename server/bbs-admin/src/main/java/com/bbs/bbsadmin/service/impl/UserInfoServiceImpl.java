package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.RegisterDTO;
import com.bbs.bbsadmin.entity.dto.UserUpdateDTO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Override
    public UserInfo findByAccount(String account) {
        if (account == null || account.isBlank()) {
            return null;
        }
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .and(w -> w.eq(UserInfo::getUserName, account)
                        .or().eq(UserInfo::getEmail, account)
                        .or().eq(UserInfo::getPhone, account))
                .last("LIMIT 1"));
    }

    @Override
    public boolean updatePassword(String userId, String newPassword) {
        UserInfo update = new UserInfo();
        update.setUserId(userId);
        update.setPassword(newPassword);
        return userInfoMapper.updateById(update) > 0;
    }

    @Override
    public String register(RegisterDTO dto) {
        // 校验用户名唯一
        long exists = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUserName, dto.getUserName()));
        if (exists > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "用户名已存在");
        }
        UserInfo user = new UserInfo();
        user.setUserId(UUID.randomUUID().toString().replace("-", ""));
        user.setUserName(dto.getUserName());
        user.setPassword(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender() == null ? "0" : dto.getGender());
        user.setCreateTime(LocalDateTime.now());
        userInfoMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public IPage<UserInfo> pageQuery(PageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<UserInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String kw = query.getKeyword();
            wrapper.and(w -> w.like(UserInfo::getUserName, kw)
                    .or().like(UserInfo::getEmail, kw)
                    .or().like(UserInfo::getPhone, kw));
        }
        wrapper.orderByDesc(UserInfo::getCreateTime);
        return userInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateProfile(String userId, UserUpdateDTO dto) {
        UserInfo update = new UserInfo();
        update.setUserId(userId);
        update.setEmail(dto.getEmail());
        update.setPhone(dto.getPhone());
        update.setGender(dto.getGender());
        return userInfoMapper.updateById(update) > 0;
    }
}
