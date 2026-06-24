package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Comment;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.CommentPageQuery;
import com.bbs.bbsadmin.entity.dto.CommentSaveDTO;
import com.bbs.bbsadmin.entity.vo.CommentVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CommentMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public IPage<CommentVO> pageQueryVO(CommentPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Comment::getContent, query.getKeyword());
        }
        if (query.getPostId() != null) {
            wrapper.eq(Comment::getPostId, query.getPostId());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(Comment::getUserId, query.getUserId());
        }
        if (query.getParentId() != null) {
            wrapper.eq(Comment::getParentId, query.getParentId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Comment::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> p = baseMapper.selectPage(page, wrapper);

        IPage<CommentVO> voPage = p.convert(CommentVO::from);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    @Override
    public CommentVO detail(Long id) {
        Comment c = baseMapper.selectById(id);
        if (c == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "评论不存在");
        }
        CommentVO vo = CommentVO.from(c);
        fillAssociations(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public Long create(CommentSaveDTO dto) {
        // 校验帖子
        Post post = postMapper.selectById(dto.getPostId());
        if (post == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "帖子不存在");
        }
        // 校验父评论
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Comment parent = baseMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BizException(ResponseCode.PARAM_ERROR, "父评论不存在");
            }
        }
        Comment c = new Comment();
        c.setPostId(dto.getPostId());
        c.setUserId(AuthContext.userId());
        c.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        c.setReplyToUserId(dto.getReplyToUserId());
        c.setContent(dto.getContent());
        c.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        c.setLikeCount(0);
        c.setCreateBy(AuthContext.userId());
        c.setCreateTime(LocalDateTime.now());
        baseMapper.insert(c);

        // 更新帖子 comment_count
        post.setCommentCount(post.getCommentCount() == null ? 1 : post.getCommentCount() + 1);
        post.setUpdateBy(AuthContext.userId());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(post);

        return c.getId();
    }

    @Override
    @Transactional
    public boolean update(Long id, CommentSaveDTO dto) {
        Comment exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "评论不存在");
        }
        Comment c = new Comment();
        c.setId(id);
        c.setContent(dto.getContent());
        c.setStatus(dto.getStatus());
        c.setUpdateBy(AuthContext.userId());
        c.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(c) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Comment exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "评论不存在");
        }
        boolean ok = baseMapper.deleteById(id) > 0;
        if (ok) {
            Post post = postMapper.selectById(exist.getPostId());
            if (post != null) {
                int curr = post.getCommentCount() == null ? 0 : post.getCommentCount();
                post.setCommentCount(Math.max(0, curr - 1));
                post.setUpdateBy(AuthContext.userId());
                post.setUpdateTime(LocalDateTime.now());
                postMapper.updateById(post);
            }
        }
        return ok;
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        Comment exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "评论不存在");
        }
        Comment c = new Comment();
        c.setId(id);
        c.setStatus(status);
        c.setUpdateBy(AuthContext.userId());
        c.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(c) > 0;
    }

    /**
     * 批量填充: 用户名/帖子标题/回复对象用户名
     */
    private void fillAssociations(List<CommentVO> vos) {
        if (CollectionUtils.isEmpty(vos)) return;

        // 用户ID集合
        Set<String> userIds = new HashSet<>();
        Set<Long> postIds = new HashSet<>();
        for (CommentVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) userIds.add(v.getUserId());
            if (StringUtils.hasText(v.getReplyToUserId())) userIds.add(v.getReplyToUserId());
            if (v.getPostId() != null) postIds.add(v.getPostId());
        }
        Map<String, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
            for (UserInfo u : users) {
                userNameMap.put(u.getUserId(), u.getUserName());
            }
        }
        Map<Long, String> postTitleMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<Post> posts = postMapper.selectBatchIds(postIds);
            for (Post p : posts) {
                postTitleMap.put(p.getId(), p.getTitle());
            }
        }
        for (CommentVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) {
                v.setUserName(userNameMap.get(v.getUserId()));
            }
            if (StringUtils.hasText(v.getReplyToUserId())) {
                v.setReplyToUserName(userNameMap.get(v.getReplyToUserId()));
            }
            if (v.getPostId() != null) {
                v.setPostTitle(postTitleMap.get(v.getPostId()));
            }
        }
    }
}
