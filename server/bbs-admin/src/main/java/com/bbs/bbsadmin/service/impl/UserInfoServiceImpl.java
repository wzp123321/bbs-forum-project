package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbs.bbsadmin.config.UploadProperties;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.PageQuery;
import com.bbs.bbsadmin.entity.dto.RegisterDTO;
import com.bbs.bbsadmin.entity.dto.UserUpdateDTO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.SensitiveWordFilter;
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

    /** 昵称长度上下限 */
    private static final int NICKNAME_MIN = 2;
    private static final int NICKNAME_MAX = 20;
    /** 签名长度上限 */
    private static final int SIGNATURE_MAX = 100;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private UploadProperties uploadProperties;

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
        // 昵称默认取用户名, 之后可在资料页单独修改
        user.setNickname(dto.getUserName());
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
                    .or().like(UserInfo::getNickname, kw)
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

        // 昵称: 传了才校验, 不传保持不变
        if (dto.getNickname() != null) {
            String nickname = dto.getNickname().trim();
            if (nickname.length() < NICKNAME_MIN || nickname.length() > NICKNAME_MAX) {
                throw new BizException(ResponseCode.PARAM_ERROR,
                        "昵称长度需在 " + NICKNAME_MIN + "-" + NICKNAME_MAX + " 个字符之间");
            }
            if (sensitiveWordFilter.contains(nickname)) {
                throw new BizException(ResponseCode.PARAM_ERROR, "昵称包含敏感词");
            }
            update.setNickname(nickname);
        }

        // 签名: 允许清空
        if (dto.getSignature() != null) {
            String signature = dto.getSignature().trim();
            if (signature.length() > SIGNATURE_MAX) {
                throw new BizException(ResponseCode.PARAM_ERROR, "个性签名不能超过 " + SIGNATURE_MAX + " 个字符");
            }
            if (sensitiveWordFilter.contains(signature)) {
                throw new BizException(ResponseCode.PARAM_ERROR, "个性签名包含敏感词");
            }
            update.setSignature(signature);
        }

        // 头像: 只接受本站上传目录下的地址, 防止外链与失效图片
        if (dto.getAvatar() != null) {
            String avatar = dto.getAvatar().trim();
            if (!avatar.isEmpty() && !avatar.startsWith(uploadProperties.getUrlPrefix())) {
                throw new BizException(ResponseCode.PARAM_ERROR, "头像必须使用本站上传的图片");
            }
            update.setAvatar(avatar.isEmpty() ? null : avatar);
        }

        // 邮箱/手机: 不能被他人占用
        if (StringUtils.hasText(dto.getEmail())) {
            Long taken = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getEmail, dto.getEmail())
                    .ne(UserInfo::getUserId, userId));
            if (taken != null && taken > 0) {
                throw new BizException(ResponseCode.PARAM_ERROR, "邮箱已被其他账号使用");
            }
            update.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            Long taken = userInfoMapper.selectCount(new LambdaQueryWrapper<UserInfo>()
                    .eq(UserInfo::getPhone, dto.getPhone())
                    .ne(UserInfo::getUserId, userId));
            if (taken != null && taken > 0) {
                throw new BizException(ResponseCode.PARAM_ERROR, "手机号已被其他账号使用");
            }
            update.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            update.setGender(dto.getGender());
        }
        return userInfoMapper.updateById(update) > 0;
    }
}
