package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.FollowRecord;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.FollowRecordMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.FollowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FollowRecordServiceImpl extends ServiceImpl<FollowRecordMapper, FollowRecord> implements FollowRecordService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional
    public boolean follow(String followUserId) {
        if (followUserId == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "参数不合法");
        }
        String userId = AuthContext.userId();
        if (userId.equals(followUserId)) {
            throw new BizException(ResponseCode.PARAM_ERROR, "不能关注自己");
        }
        // 校验被关注人存在
        UserInfo target = userInfoMapper.selectById(followUserId);
        if (target == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "用户不存在");
        }
        Long exist = baseMapper.findOne(userId, followUserId);
        if (exist != null) {
            return false;
        }
        FollowRecord r = new FollowRecord();
        r.setUserId(userId);
        r.setFollowUserId(followUserId);
        r.setCreateTime(LocalDateTime.now());
        baseMapper.insert(r);
        return true;
    }

    @Override
    public boolean cancel(String followUserId) {
        if (followUserId == null) {
            return false;
        }
        int n = baseMapper.softCancel(AuthContext.userId(), followUserId);
        return n > 0;
    }

    @Override
    public boolean isFollowing(String followUserId) {
        return baseMapper.findOne(AuthContext.userId(), followUserId) != null;
    }

    @Override
    public List<String> listFollowerIds(String userId) {
        LambdaQueryWrapper<FollowRecord> w = new LambdaQueryWrapper<>();
        w.eq(FollowRecord::getFollowUserId, userId).select(FollowRecord::getUserId);
        return baseMapper.selectList(w).stream().map(FollowRecord::getUserId).collect(Collectors.toList());
    }

    @Override
    public List<String> listFollowingIds(String userId) {
        LambdaQueryWrapper<FollowRecord> w = new LambdaQueryWrapper<>();
        w.eq(FollowRecord::getUserId, userId).select(FollowRecord::getFollowUserId);
        return baseMapper.selectList(w).stream().map(FollowRecord::getFollowUserId).collect(Collectors.toList());
    }

    @Override
    public long countFollowers(String userId) {
        return baseMapper.countFollowers(userId);
    }

    @Override
    public long countFollowing(String userId) {
        return baseMapper.countFollowing(userId);
    }

    @Override
    public IPage<UserVO> pageFollowers(String userId, int pageNum, int pageSize) {
        Page<UserVO> page = new Page<>(pageNum, pageSize);
        if (userId == null || userId.isEmpty()) return page;
        LambdaQueryWrapper<FollowRecord> w = new LambdaQueryWrapper<>();
        w.eq(FollowRecord::getFollowUserId, userId)
                .orderByDesc(FollowRecord::getCreateTime)
                .select(FollowRecord::getUserId);
        List<FollowRecord> records = baseMapper.selectList(w);
        return buildUserVOPage(records, FollowRecord::getUserId, page, pageNum, pageSize);
    }

    @Override
    public IPage<UserVO> pageFollowing(String userId, int pageNum, int pageSize) {
        Page<UserVO> page = new Page<>(pageNum, pageSize);
        if (userId == null || userId.isEmpty()) return page;
        LambdaQueryWrapper<FollowRecord> w = new LambdaQueryWrapper<>();
        w.eq(FollowRecord::getUserId, userId)
                .orderByDesc(FollowRecord::getCreateTime)
                .select(FollowRecord::getFollowUserId);
        List<FollowRecord> records = baseMapper.selectList(w);
        return buildUserVOPage(records, FollowRecord::getFollowUserId, page, pageNum, pageSize);
    }

    @SuppressWarnings("unchecked")
    private IPage<UserVO> buildUserVOPage(List<FollowRecord> records,
                                          java.util.function.Function<FollowRecord, String> idGetter,
                                          Page<UserVO> page, int pageNum, int pageSize) {
        if (records.isEmpty()) return page;
        List<String> ids = records.stream().map(idGetter).collect(Collectors.toList());
        List<UserInfo> users = userInfoMapper.selectBatchIds(ids);
        Map<String, UserInfo> map = users.stream().collect(Collectors.toMap(UserInfo::getUserId, u -> u));
        List<UserVO> all = new ArrayList<>();
        for (String id : ids) {
            UserInfo u = map.get(id);
            if (u != null) {
                UserVO vo = UserVO.from(u);
                if (vo != null) all.add(vo);
            }
        }
        long total = all.size();
        int from = Math.min((pageNum - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        page.setRecords(new ArrayList<>(all.subList(from, to)));
        page.setTotal(total);
        return page;
    }
}
