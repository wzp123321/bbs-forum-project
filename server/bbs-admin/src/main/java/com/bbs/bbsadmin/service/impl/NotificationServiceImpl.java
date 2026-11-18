package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Notification;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.vo.NotificationVO;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.mapper.NotificationMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public void push(String receiverId, String actorId, int type, long postId, long commentId, String content) {
        // 自己触发的不通知自己; 接收者为空直接忽略
        if (!StringUtils.hasText(receiverId) || receiverId.equals(actorId)) {
            return;
        }
        baseMapper.upsert(receiverId, actorId, type, postId, commentId, truncate(content));
    }

    @Override
    public void revoke(String receiverId, String actorId, int type, long postId, long commentId) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(actorId)) {
            return;
        }
        baseMapper.revoke(receiverId, actorId, type, postId, commentId);
    }

    @Override
    public long unreadCount(String userId) {
        if (!StringUtils.hasText(userId)) {
            return 0;
        }
        Long n = baseMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0));
        return n == null ? 0 : n;
    }

    @Override
    public IPage<NotificationVO> pageQuery(String userId, boolean onlyUnread, int pageNum, int pageSize) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notification> w = new LambdaQueryWrapper<>();
        w.eq(Notification::getUserId, userId);
        if (onlyUnread) {
            w.eq(Notification::getIsRead, 0);
        }
        w.orderByDesc(Notification::getCreateTime);
        IPage<Notification> p = baseMapper.selectPage(page, w);

        IPage<NotificationVO> voPage = p.convert(NotificationVO::from);
        List<NotificationVO> records = voPage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            fillAssociations(records);
        }
        return voPage;
    }

    @Override
    public boolean markRead(Long id, String userId) {
        return baseMapper.markRead(id, userId) > 0;
    }

    @Override
    public int markAllRead(String userId) {
        return baseMapper.markAllRead(userId);
    }

    /**
     * 批量填充触发者展示名/头像与帖子标题, 一次查询解决, 不产生 N+1
     */
    private void fillAssociations(List<NotificationVO> records) {
        Set<String> actorIds = new HashSet<>();
        Set<Long> postIds = new HashSet<>();
        for (NotificationVO v : records) {
            if (StringUtils.hasText(v.getActorId())) actorIds.add(v.getActorId());
            if (v.getPostId() != null && v.getPostId() > 0) postIds.add(v.getPostId());
        }
        Map<String, UserInfo> userMap = new HashMap<>();
        if (!actorIds.isEmpty()) {
            for (UserInfo u : userInfoMapper.selectBatchIds(actorIds)) {
                userMap.put(u.getUserId(), u);
            }
        }
        Map<Long, String> titleMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            for (Post p : postMapper.selectBatchIds(postIds)) {
                titleMap.put(p.getId(), p.getTitle());
            }
        }
        for (NotificationVO v : records) {
            if (StringUtils.hasText(v.getActorId())) {
                UserInfo u = userMap.get(v.getActorId());
                if (u != null) {
                    v.setActorName(UserVO.displayName(u));
                    v.setActorAvatar(u.getAvatar());
                }
            }
            if (v.getPostId() != null && v.getPostId() > 0) {
                v.setPostTitle(titleMap.get(v.getPostId()));
            }
        }
    }

    private String truncate(String text) {
        if (!StringUtils.hasText(text)) return null;
        String t = text.trim();
        return t.length() <= 100 ? t : t.substring(0, 100);
    }
}
