package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.CollectRecord;
import com.bbs.bbsadmin.entity.Comment;
import com.bbs.bbsadmin.entity.LikeRecord;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CollectRecordMapper;
import com.bbs.bbsadmin.mapper.CommentMapper;
import com.bbs.bbsadmin.mapper.LikeRecordMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.CollectRecordService;
import com.bbs.bbsadmin.service.LikeRecordService;
import com.bbs.bbsadmin.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public boolean like(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "参数不合法");
        }
        // 校验目标存在
        ensureTargetExists(targetType, targetId);

        String userId = AuthContext.userId();
        // 查重
        Long exist = baseMapper.findOne(userId, targetType, targetId);
        if (exist != null) {
            return false;
        }
        LikeRecord r = new LikeRecord();
        r.setUserId(userId);
        r.setTargetType(targetType);
        r.setTargetId(targetId);
        r.setCreateTime(LocalDateTime.now());
        baseMapper.insert(r);

        // 目标 +1
        if (targetType == 1) {
            Post p = new Post();
            p.setId(targetId);
            Post exist2 = postMapper.selectById(targetId);
            p.setLikeCount(exist2.getLikeCount() == null ? 1 : exist2.getLikeCount() + 1);
            postMapper.updateById(p);
        } else if (targetType == 2) {
            commentMapper.incrLikeCount(targetId, 1);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean cancel(Integer targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "参数不合法");
        }
        String userId = AuthContext.userId();
        Long exist = baseMapper.findOne(userId, targetType, targetId);
        if (exist == null) {
            return false;
        }
        baseMapper.softCancel(userId, targetType, targetId);

        if (targetType == 1) {
            Post p = postMapper.selectById(targetId);
            if (p != null) {
                Post upd = new Post();
                upd.setId(targetId);
                upd.setLikeCount(Math.max(0, (p.getLikeCount() == null ? 0 : p.getLikeCount()) - 1));
                postMapper.updateById(upd);
            }
        } else if (targetType == 2) {
            commentMapper.incrLikeCount(targetId, -1);
        }
        return true;
    }

    @Override
    public boolean isLiked(Integer targetType, Long targetId) {
        return baseMapper.findOne(AuthContext.userId(), targetType, targetId) != null;
    }

    @Override
    public IPage<PostVO> pageMyLikedPosts(String userId, int pageNum, int pageSize) {
        Page<PostVO> page = new Page<>(pageNum, pageSize);
        if (userId == null || userId.isEmpty()) {
            return page;
        }
        LambdaQueryWrapper<LikeRecord> w = new LambdaQueryWrapper<>();
        w.eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, 1)
                .orderByDesc(LikeRecord::getCreateTime)
                .select(LikeRecord::getTargetId);
        List<LikeRecord> records = baseMapper.selectList(w);
        if (records.isEmpty()) {
            return page;
        }
        List<Long> postIds = records.stream().map(LikeRecord::getTargetId).collect(Collectors.toList());
        List<Post> posts = postMapper.selectBatchIds(postIds);
        if (posts.isEmpty()) {
            return page;
        }
        Map<Long, Post> map = posts.stream().collect(Collectors.toMap(Post::getId, p -> p));
        List<PostVO> all = new ArrayList<>();
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
        postService.fillAssociationsPublic(pageRecords);
        page.setRecords(pageRecords);
        page.setTotal(total);
        return page;
    }

    private void ensureTargetExists(Integer targetType, Long targetId) {
        if (targetType == 1) {
            Post p = postMapper.selectById(targetId);
            if (p == null) throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        } else if (targetType == 2) {
            Comment c = commentMapper.selectById(targetId);
            if (c == null) throw new BizException(ResponseCode.NOT_FOUND, "评论不存在");
        } else {
            throw new BizException(ResponseCode.PARAM_ERROR, "不支持的点赞类型");
        }
    }
}
