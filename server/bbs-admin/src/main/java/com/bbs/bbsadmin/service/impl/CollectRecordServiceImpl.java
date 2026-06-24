package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.CollectRecord;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CollectRecordMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.CollectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CollectRecordServiceImpl extends ServiceImpl<CollectRecordMapper, CollectRecord> implements CollectRecordService {

    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional
    public boolean collect(Long postId) {
        Post p = postMapper.selectById(postId);
        if (p == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        String userId = AuthContext.userId();
        Long exist = baseMapper.findOne(userId, postId);
        if (exist != null) {
            return false;
        }
        CollectRecord r = new CollectRecord();
        r.setUserId(userId);
        r.setPostId(postId);
        r.setCreateTime(LocalDateTime.now());
        baseMapper.insert(r);

        Post upd = new Post();
        upd.setId(postId);
        upd.setCollectCount((p.getCollectCount() == null ? 0 : p.getCollectCount()) + 1);
        postMapper.updateById(upd);
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(Long postId) {
        String userId = AuthContext.userId();
        Long exist = baseMapper.findOne(userId, postId);
        if (exist == null) {
            return false;
        }
        baseMapper.softCancel(userId, postId);
        Post p = postMapper.selectById(postId);
        if (p != null) {
            Post upd = new Post();
            upd.setId(postId);
            upd.setCollectCount(Math.max(0, (p.getCollectCount() == null ? 0 : p.getCollectCount()) - 1));
            postMapper.updateById(upd);
        }
        return true;
    }

    @Override
    public boolean isCollected(Long postId) {
        return baseMapper.findOne(AuthContext.userId(), postId) != null;
    }
}
