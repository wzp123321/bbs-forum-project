package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.PostTag;
import com.bbs.bbsadmin.entity.Tag;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.DraftSaveDTO;
import com.bbs.bbsadmin.entity.dto.PostPageQuery;
import com.bbs.bbsadmin.entity.dto.PostSaveDTO;
import com.bbs.bbsadmin.entity.vo.PostVO;
import com.bbs.bbsadmin.entity.vo.UserVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CategoryMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.PostTagMapper;
import com.bbs.bbsadmin.mapper.TagMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.SensitiveWordFilter;
import com.bbs.bbsadmin.security.XssSanitizer;
import com.bbs.bbsadmin.service.AttachmentService;
import com.bbs.bbsadmin.service.FollowRecordService;
import com.bbs.bbsadmin.service.PostService;
import com.bbs.bbsadmin.search.EsPostSearchClient;
import com.bbs.bbsadmin.utils.RequestUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Autowired
    private Authz authz;

    @Autowired
    private FollowRecordService followRecordService;

    @Autowired
    private ObjectProvider<EsPostSearchClient> esPostSearchClient;

    @Value("${bbs.search.engine:sql}")
    private String searchEngine;

    /** 同一访问者 5 分钟内重复访问同一帖子不重复计数 */
    private static final long VIEW_DEDUP_MINUTES = 5;

    /** 帖子状态: 草稿 */
    private static final int STATUS_DRAFT = 4;

    private final Cache<String, Boolean> viewDedup = Caffeine.newBuilder()
            .expireAfterWrite(VIEW_DEDUP_MINUTES, TimeUnit.MINUTES)
            .maximumSize(100_000)
            .build();

    @Override
    public IPage<PostVO> pageQueryVO(PostPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        String sort = normalizeSort(query.getSort());
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            List<Long> esIds = null;
            if ("elasticsearch".equals(searchEngine)) {
                EsPostSearchClient client = esPostSearchClient.getIfAvailable();
                if (client != null) {
                    // 最多取 200 个候选；ES 不可用/索引为空时返回 null，自动回退 SQL LIKE
                    esIds = client.searchIds(query.getKeyword(), 0, 200);
                }
            }
            if (esIds != null && !esIds.isEmpty()) {
                wrapper.in(Post::getId, esIds);
            } else {
                wrapper.like(Post::getTitle, query.getKeyword());
            }
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
        if ("essence".equals(sort)) {
            wrapper.eq(Post::getIsEssence, 1);
        }
        // 标签筛选: 子查询 postId 集合
        if (query.getTagId() != null) {
            List<PostTag> pts = postTagMapper.selectByTagId(query.getTagId());
            Page<Post> emptyPage = new Page<>(pageNum, pageSize, false);
            if (CollectionUtils.isEmpty(pts)) {
                return emptyPage.convert(PostVO::from);
            }
            Set<Long> postIds = pts.stream().map(PostTag::getPostId).collect(Collectors.toSet());
            wrapper.in(Post::getId, postIds);
        }
        // 排序含表达式(热门加权), 先单独 count 再查列表, 不让 MP 自动 count 去解析 ORDER BY
        Long total = baseMapper.selectCount(wrapper);
        Page<Post> page = new Page<>(pageNum, pageSize, false);
        // searchCount=false 时 MP 不会回填 total, 必须手动写入, 否则前端分页总数恒为 0
        page.setTotal(total == null ? 0 : total);
        IPage<Post> p = (total == null || total == 0)
                ? page
                : baseMapper.selectPage(page, wrapper.last(orderBySql(sort)));

        // 转 VO 并填充关联信息
        IPage<PostVO> voPage = p.convert(this::toVO);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    /** 非法排序值统一降级为最新 */
    private String normalizeSort(String sort) {
        if ("hot".equals(sort) || "essence".equals(sort)) {
            return sort;
        }
        return "latest";
    }

    /**
     * 排序片段
     * 常量字符串, 不含外部输入, 可用 last() 拼接
     */
    private String orderBySql(String sort) {
        if ("hot".equals(sort)) {
            return "ORDER BY is_top DESC, (like_count * 3 + comment_count * 2 + view_count) DESC, create_time DESC";
        }
        if ("essence".equals(sort)) {
            return "ORDER BY create_time DESC";
        }
        return "ORDER BY is_top DESC, top_time DESC, create_time DESC";
    }

    @Override
    public PostVO detail(Long id) {
        Post p = baseMapper.selectById(id);
        if (p == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        // 阅读权限检查 (不通过时直接抛异常, 因此不会计数)
        assertReadable(p);
        PostVO vo = toVO(p);
        fillAssociations(Collections.singletonList(vo));
        if (recordView(p.getId())) {
            vo.setViewCount((p.getViewCount() == null ? 0 : p.getViewCount()) + 1);
        }
        return vo;
    }

    /**
     * 记录一次阅读
     * 同一访问者(登录按 userId, 游客按 IP)在去重窗口内只计一次
     *
     * @return 本次是否真的累加
     */
    private boolean recordView(Long postId) {
        String subject = AuthContext.userId();
        if (!StringUtils.hasText(subject)) {
            subject = "ip:" + RequestUtils.clientIp();
        }
        String key = "read:" + postId + ":" + subject;
        if (viewDedup.getIfPresent(key) != null) {
            return false;
        }
        viewDedup.put(key, Boolean.TRUE);
        return baseMapper.incrViewCount(postId) > 0;
    }

    /**
     * 阅读权限:
     * 1 公开 任何人都能看
     * 2 登录可见
     * 3 粉丝可见 (含作者本人)
     * 4 仅作者
     * 不通过时:
     * - 状态非正常(status!=1): 抛出 404
     * - 已删/审核中: 同样 404
     * - 权限不足: 把 content/title 置为占位提示
     */
    private void assertReadable(Post p) {
        // 状态过滤: 未传/非法 -> 视为已删
        if (p.getStatus() == null || p.getStatus() != 1) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        Integer perm = p.getReadPerm() == null ? 1 : p.getReadPerm();
        if (perm == 1) return;
        String me = AuthContext.userId();
        if (me != null && me.equals(p.getUserId())) return;
        if (perm == 2) {
            if (me == null) throwNoPerm();
            return;
        }
        if (perm == 3) {
            if (me == null) throwNoPerm();
            if (!followRecordService.isFollowing(p.getUserId())) {
                throw new BizException(ResponseCode.FORBIDDEN, "该帖仅粉丝可读");
            }
            return;
        }
        if (perm == 4) {
            throw new BizException(ResponseCode.FORBIDDEN, "该帖仅作者可见");
        }
    }

    private void throwNoPerm() {
        throw new BizException(ResponseCode.UNAUTHORIZED);
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
        p.setReadPerm(dto.getReadPerm() == null ? 1 : dto.getReadPerm());
        p.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        // 敏感词过滤: 命中标题/正文则转审核中
        if (sensitiveWordFilter.contains(dto.getTitle()) || sensitiveWordFilter.contains(dto.getContent())) {
            p.setStatus(2);
        }
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
        indexIfEnabled(p);
        return p.getId();
    }

    @Override
    @Transactional
    public boolean update(Long id, PostSaveDTO dto) {
        Post exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "帖子不存在");
        }
        // 越权检查: 仅作者本人或管理员可编辑
        authz.assertOwnerOrAdmin(exist.getUserId());
        validateCategory(dto.getCategoryId());
        Post p = new Post();
        p.setId(id);
        p.setCategoryId(dto.getCategoryId());
        p.setTitle(xssSanitizer.clean(dto.getTitle()));
        p.setContent(xssSanitizer.clean(dto.getContent()));
        p.setContentType(dto.getContentType());
        p.setReadPerm(dto.getReadPerm());
        p.setStatus(dto.getStatus());
        // 敏感词过滤
        if (sensitiveWordFilter.contains(dto.getTitle()) || sensitiveWordFilter.contains(dto.getContent())) {
            p.setStatus(2);
        }
        p.setIsTop(dto.getIsTop());
        p.setIsEssence(dto.getIsEssence());
        p.setUpdateBy(AuthContext.userId());
        p.setUpdateTime(LocalDateTime.now());
        boolean ok = baseMapper.updateById(p) > 0;
        if (ok) {
            saveTags(id, dto.getTagIds());
            indexIfEnabled(p);
        }
        return ok;
    }

    /** ES 启用时才同步索引；未启用时零开销。 */
    private void indexIfEnabled(Post post) {
        if (!"elasticsearch".equals(searchEngine)) return;
        EsPostSearchClient client = esPostSearchClient.getIfAvailable();
        if (client != null) {
            client.indexPost(post);
        }
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

    @Override
    public void fillAssociationsPublic(List<PostVO> vos) {
        fillAssociations(vos);
    }

    /**
     * 批量填充用户/板块/标签信息
     */
    private void fillAssociations(List<PostVO> vos) {
        if (CollectionUtils.isEmpty(vos)) return;
        // 用户
        Set<String> userIds = vos.stream().map(PostVO::getUserId)
                .filter(StringUtils::hasText).collect(Collectors.toSet());
        Map<String, UserInfo> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
            for (UserInfo u : users) {
                userMap.put(u.getUserId(), u);
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
                UserInfo author = userMap.get(v.getUserId());
                if (author != null) {
                    // 展示口径统一由 UserVO.displayName 决定: 昵称优先, 为空回退用户名
                    v.setNickname(UserVO.displayName(author));
                    v.setAvatar(author.getAvatar());
                }
                v.setUserName(userMap.containsKey(v.getUserId())
                        ? userMap.get(v.getUserId()).getUserName() : null);
            }
            if (v.getCategoryId() != null) {
                v.setCategoryName(categoryNameMap.get(v.getCategoryId()));
            }
            v.setTagIds(postTagIds.getOrDefault(v.getId(), Collections.emptyList()));
            v.setTagNames(postTagNames.getOrDefault(v.getId(), Collections.emptyList()));
        }
    }

    // ==================== 关注动态流 ====================

    @Override
    public IPage<PostVO> followFeed(int pageNum, int pageSize) {
        String me = AuthContext.userId();
        if (!StringUtils.hasText(me)) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        int num = pageNum < 1 ? 1 : pageNum;
        int size = pageSize < 1 ? 10 : pageSize;

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                // 自己发的帖子不进关注流
                .ne(Post::getUserId, me)
                // 关注关系用 EXISTS 子查询: 关注量大时不必拼超长 IN; {0} 由 MyBatis 参数绑定
                .apply("EXISTS (SELECT 1 FROM bbs_follow f WHERE f.follow_user_id = bbs_post.user_id"
                        + " AND f.user_id = {0} AND f.deleted = 0)", me);

        Long total = baseMapper.selectCount(wrapper);
        Page<Post> page = new Page<>(num, size, false);
        page.setTotal(total == null ? 0 : total);
        IPage<Post> p = (total == null || total == 0)
                ? page
                : baseMapper.selectPage(page, wrapper.last("ORDER BY create_time DESC"));
        IPage<PostVO> voPage = p.convert(this::toVO);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    // ==================== 草稿箱 ====================

    @Override
    @Transactional
    public Long saveDraft(DraftSaveDTO dto) {
        String me = AuthContext.userId();
        if (!StringUtils.hasText(me)) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        String title = dto.getTitle() == null ? "" : dto.getTitle().trim();
        String content = dto.getContent() == null ? "" : dto.getContent().trim();
        if (title.isEmpty() && content.isEmpty()) {
            throw new BizException(ResponseCode.PARAM_ERROR, "标题和正文至少填一项");
        }
        validateCategory(dto.getCategoryId());

        if (dto.getId() == null) {
            Post p = new Post();
            p.setUserId(me);
            p.setCategoryId(dto.getCategoryId());
            p.setTitle(title.isEmpty() ? "" : xssSanitizer.clean(title));
            p.setContent(content.isEmpty() ? "" : xssSanitizer.clean(content));
            p.setContentType(dto.getContentType() == null ? 1 : dto.getContentType());
            p.setReadPerm(1);
            p.setStatus(STATUS_DRAFT);
            p.setIsTop(0);
            p.setIsEssence(0);
            p.setViewCount(0);
            p.setLikeCount(0);
            p.setCommentCount(0);
            p.setCollectCount(0);
            p.setCreateBy(me);
            p.setCreateTime(LocalDateTime.now());
            baseMapper.insert(p);
            saveTags(p.getId(), dto.getTagIds());
            return p.getId();
        }

        Post exist = requireOwnDraft(dto.getId(), me);
        Post p = new Post();
        p.setId(exist.getId());
        p.setTitle(title.isEmpty() ? "" : xssSanitizer.clean(title));
        p.setContent(content.isEmpty() ? "" : xssSanitizer.clean(content));
        p.setContentType(dto.getContentType());
        p.setCategoryId(dto.getCategoryId());
        p.setUpdateBy(me);
        p.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(p);
        saveTags(exist.getId(), dto.getTagIds());
        return exist.getId();
    }

    @Override
    public IPage<PostVO> myDrafts(int pageNum, int pageSize) {
        String me = AuthContext.userId();
        if (!StringUtils.hasText(me)) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        int num = pageNum < 1 ? 1 : pageNum;
        int size = pageSize < 1 ? 10 : pageSize;

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getUserId, me).eq(Post::getStatus, STATUS_DRAFT);

        Long total = baseMapper.selectCount(wrapper);
        Page<Post> page = new Page<>(num, size, false);
        page.setTotal(total == null ? 0 : total);
        IPage<Post> p = (total == null || total == 0)
                ? page
                : baseMapper.selectPage(page, wrapper.orderByDesc(Post::getUpdateTime));
        IPage<PostVO> voPage = p.convert(this::toVO);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    @Override
    public PostVO draftDetail(Long id) {
        String me = AuthContext.userId();
        if (!StringUtils.hasText(me)) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        Post p = id == null ? null : baseMapper.selectById(id);
        // 不是自己的草稿一律按不存在处理, 不暴露他人草稿是否存在
        if (p == null || p.getStatus() == null || p.getStatus() != STATUS_DRAFT
                || !me.equals(p.getUserId())) {
            throw new BizException(ResponseCode.NOT_FOUND, "草稿不存在");
        }
        PostVO vo = toVO(p);
        fillAssociations(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public Integer publishDraft(Long id) {
        String me = AuthContext.userId();
        if (!StringUtils.hasText(me)) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        Post exist = requireOwnDraft(id, me);
        String title = exist.getTitle() == null ? "" : exist.getTitle().trim();
        String content = exist.getContent() == null ? "" : exist.getContent().trim();
        if (title.isEmpty()) {
            throw new BizException(ResponseCode.PARAM_ERROR, "请先填写标题");
        }
        if (content.isEmpty()) {
            throw new BizException(ResponseCode.PARAM_ERROR, "请先填写正文");
        }
        if (exist.getCategoryId() == null) {
            throw new BizException(ResponseCode.PARAM_ERROR, "请先选择板块");
        }
        validateCategory(exist.getCategoryId());

        // 发布走与正常发帖一致的敏感词判定: 命中转审核中
        int status = (sensitiveWordFilter.contains(title) || sensitiveWordFilter.contains(content)) ? 2 : 1;
        Post p = new Post();
        p.setId(id);
        p.setStatus(status);
        p.setUpdateBy(me);
        p.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(p);
        return status;
    }

    /** 取草稿并校验归属 */
    private Post requireOwnDraft(Long id, String me) {
        Post exist = id == null ? null : baseMapper.selectById(id);
        if (exist == null || exist.getStatus() == null || exist.getStatus() != STATUS_DRAFT) {
            throw new BizException(ResponseCode.NOT_FOUND, "草稿不存在");
        }
        if (!me.equals(exist.getUserId())) {
            throw new BizException(ResponseCode.FORBIDDEN, "无权操作他人草稿");
        }
        return exist;
    }
}