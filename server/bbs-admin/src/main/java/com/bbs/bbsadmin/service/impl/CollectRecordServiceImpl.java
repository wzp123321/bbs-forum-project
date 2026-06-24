package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.CollectRecord;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CollectRecordMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.CollectRecordService;
import com.bbs.bbsadmin.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectRecordServiceImpl extends ServiceImpl<CollectRecordMapper, CollectRecord> implements CollectRecordService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

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

    @Override
    public IPage<PostVO> pageMyCollectedPosts(String userId, int pageNum, int pageSize) {
        Page<PostVO> page = new Page<>(pageNum, pageSize);
        if (userId == null || userId.isEmpty()) {
            return page;
        }
        LambdaQueryWrapper<CollectRecord> w = new LambdaQueryWrapper<>();
        w.eq(CollectRecord::getUserId, userId)
                .orderByDesc(CollectRecord::getCreateTime)
                .select(CollectRecord::getPostId);
        List<CollectRecord> records = baseMapper.selectList(w);
        if (records.isEmpty()) {
            return page;
        }
        List<Long> postIds = records.stream().map(CollectRecord::getPostId).collect(Collectors.toList());
        List<Post> posts = postMapper.selectBatchIds(postIds);
        if (posts.isEmpty()) {
            return page;
        }
        // 保留收藏顺序
        java.util.Map<Long, Post> map = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));
        List<PostVO> all = new java.util.ArrayList<>();
        for (Long pid : postIds) {
            Post p = map.get(pid);
            if (p != null && p.getStatus() != null && p.getStatus() == 1) {
                PostVO vo = PostVO.from(p);
                if (vo != null) all.add(vo);
            }
        }
        long total = all.size();
        int from = Math.min((pageNum - 1) * pageSize, all.size());
        int to = Math.min(from + pageSize, all.size());
        List<PostVO> pageRecords = all.subList(from, to);
        // 复用 PostService 的填充逻辑
        postService.fillAssociationsPublic(pageRecords);
        page.setRecords(pageRecords);
        page.setTotal(total);
        return page;
    }
}
