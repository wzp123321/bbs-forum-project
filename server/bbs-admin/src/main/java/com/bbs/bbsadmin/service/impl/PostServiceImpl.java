package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.PostTag;
import com.bbs.bbsadmin.entity.Tag;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.PostPageQuery;
import com.bbs.bbsadmin.entity.dto.PostSaveDTO;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CategoryMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.PostTagMapper;
import com.bbs.bbsadmin.mapper.TagMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostTagMapper postTagMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public IPage<PostVO> pageQueryVO(PostPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Post> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Post::getTitle, query.getKeyword());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Post::getCategoryId, query.getCategoryId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Post::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(Post::getUserId, query.getUserId());
        }
        // 标签筛选: 子查询 postId 集合
        if (query.getTagId() != null) {
            List<PostTag> pts = postTagMapper.selectByTagId(query.getTagId());
            if (CollectionUtils.isEmpty(pts)) {
                return page.convert(PostVO::from);
            }
            Set<Long> postIds = pts.stream().map(PostTag::getPostId).collect(Collectors.toSet());
            wrapper.in(Post::getId, postIds);
        }
        wrapper.orderByDesc(Post::getIsTop)
               .orderByDesc(Post::getTopTime)
               .orderByDesc(Post::getCreateTime);
        IPage<Post> p = baseMapper.selectPage(page, wrapper);

        // 转 VO 并填充关联信息
        IPage<PostVO> voPage = p.convert(this::toVO);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    @Override
    public PostVO detail(Long id) {
        Post p = baseMapper.selectById(id);
        if (p == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        PostVO vo = toVO(p);
        fillAssociations(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public Long create(PostSaveDTO dto) {
        validateCategory(dto.getCategoryId());
        Post p = new Post();
        p.setUserId(AuthContext.userId());
        p.setCategoryId(dto.getCategoryId());
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setContentType(dto.getContentType() == null ? 1 : dto.getContentType());
        p.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        p.setIsTop(dto.getIsTop() == null ? 0 : dto.getIsTop());
        p.setIsEssence(dto.getIsEssence() == null ? 0 : dto.getIsEssence());
        p.setViewCount(0);
        p.setLikeCount(0);
        p.setCommentCount(0);
        p.setCollectCount(0);
        if (p.getIsTop() != null && p.getIsTop() == 1) {
            p.setTopTime(LocalDateTime.now());
        }
        p.setCreateBy(AuthContext.userId());
        p.setCreateTime(LocalDateTime.now());
        baseMapper.insert(p);

        saveTags(p.getId(), dto.getTagIds());
        return p.getId();
    }

    @Override
    @Transactional
    public boolean update(Long id, PostSaveDTO dto) {
        Post exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        validateCategory(dto.getCategoryId());
        Post p = new Post();
        p.setId(id);
        p.setCategoryId(dto.getCategoryId());
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setContentType(dto.getContentType());
        p.setStatus(dto.getStatus());
        p.setIsTop(dto.getIsTop());
        p.setIsEssence(dto.getIsEssence());
        p.setUpdateBy(AuthContext.userId());
        p.setUpdateTime(LocalDateTime.now());
        boolean ok = baseMapper.updateById(p) > 0;
        if (ok) {
            saveTags(id, dto.getTagIds());
        }
        return ok;
    }

    @Override
    public boolean toggleTop(Long id, Integer isTop) {
        Post exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        Post p = new Post();
        p.setId(id);
        p.setIsTop(isTop == null ? 0 : isTop);
        p.setTopTime(isTop != null && isTop == 1 ? LocalDateTime.now() : null);
        p.setUpdateBy(AuthContext.userId());
        p.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(p) > 0;
    }

    @Override
    public boolean toggleEssence(Long id, Integer isEssence) {
        Post exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        Post p = new Post();
        p.setId(id);
        p.setIsEssence(isEssence == null ? 0 : isEssence);
        p.setUpdateBy(AuthContext.userId());
        p.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(p) > 0;
    }

    @Override
    public boolean changeStatus(Long id, Integer status) {
        Post exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        Post p = new Post();
        p.setId(id);
        p.setStatus(status);
        p.setUpdateBy(AuthContext.userId());
        p.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(p) > 0;
    }

    /**
     * 校验板块存在
     */
    private void validateCategory(Long categoryId) {
        if (categoryId == null) return;
        Category c = categoryMapper.selectById(categoryId);
        if (c == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "板块不存在");
        }
    }

    /**
     * 保存帖子-标签关联 (全删全插)
     */
    private void saveTags(Long postId, List<Long> tagIds) {
        postTagMapper.deleteByPostId(postId);
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        List<Long> valid = tagIds.stream().filter(id -> id != null).collect(Collectors.toList());
        if (valid.isEmpty()) {
            return;
        }
        List<PostTag> rows = new ArrayList<>(valid.size());
        LocalDateTime now = LocalDateTime.now();
        for (Long tagId : valid) {
            PostTag pt = new PostTag();
            pt.setPostId(postId);
            pt.setTagId(tagId);
            pt.setCreateTime(now);
            rows.add(pt);
        }
        for (PostTag pt : rows) {
            postTagMapper.insert(pt);
        }
    }

    private PostVO toVO(Post p) {
        return PostVO.from(p);
    }

    /**
     * 批量填充用户/板块/标签信息
     */
    private void fillAssociations(List<PostVO> vos) {
        if (CollectionUtils.isEmpty(vos)) return;
        // 用户
        Set<String> userIds = vos.stream().map(PostVO::getUserId)
                .filter(StringUtils::hasText).collect(Collectors.toSet());
        Map<String, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
            for (UserInfo u : users) {
                userNameMap.put(u.getUserId(), u.getUserName());
            }
        }
        // 板块
        Set<Long> categoryIds = vos.stream().map(PostVO::getCategoryId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<Category> cs = categoryMapper.selectBatchIds(categoryIds);
            for (Category c : cs) {
                categoryNameMap.put(c.getId(), c.getName());
            }
        }
        // 标签
        List<Long> postIds = vos.stream().map(PostVO::getId).collect(Collectors.toList());
        Map<Long, List<Long>> postTagIds = new HashMap<>();
        Map<Long, List<String>> postTagNames = new HashMap<>();
        Set<Long> allTagIds = new HashSet<>();
        if (!postIds.isEmpty()) {
            List<PostTag> pts = postTagMapper.selectByPostIds(postIds);
            for (PostTag pt : pts) {
                postTagIds.computeIfAbsent(pt.getPostId(), k -> new ArrayList<>()).add(pt.getTagId());
                allTagIds.add(pt.getTagId());
            }
        }
        Map<Long, String> tagNameMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            List<Tag> ts = tagMapper.selectBatchIds(allTagIds);
            for (Tag t : ts) {
                tagNameMap.put(t.getId(), t.getName());
            }
            for (Map.Entry<Long, List<Long>> e : postTagIds.entrySet()) {
                List<String> names = e.getValue().stream()
                        .map(tagNameMap::get)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toList());
                postTagNames.put(e.getKey(), names);
            }
        }
        for (PostVO v : vos) {
            if (v.getUserId() != null) {
                v.setUserName(userNameMap.get(v.getUserId()));
            }
            if (v.getCategoryId() != null) {
                v.setCategoryName(categoryNameMap.get(v.getCategoryId()));
            }
            v.setTagIds(postTagIds.getOrDefault(v.getId(), Collections.emptyList()));
            v.setTagNames(postTagNames.getOrDefault(v.getId(), Collections.emptyList()));
        }
    }
}
