package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Comment;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.Report;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.ReportHandleDTO;
import com.bbs.bbsadmin.entity.dto.ReportPageQuery;
import com.bbs.bbsadmin.entity.dto.ReportSaveDTO;
import com.bbs.bbsadmin.entity.vo.ReportVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.CommentMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.ReportMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.XssSanitizer;
import com.bbs.bbsadmin.service.ReportService;
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
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    /** 举报目标: 帖子 */
    private static final int TARGET_POST = 1;
    /** 举报目标: 评论 */
    private static final int TARGET_COMMENT = 2;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Autowired
    private Authz authz;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public IPage<ReportVO> pageQueryVO(ReportPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Report> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Report::getContent, query.getKeyword());
        }
        if (query.getTargetType() != null) {
            wrapper.eq(Report::getTargetType, query.getTargetType());
        }
        if (StringUtils.hasText(query.getReasonType())) {
            wrapper.eq(Report::getReasonType, query.getReasonType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Report::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(Report::getUserId, query.getUserId());
        }
        wrapper.orderByAsc(Report::getStatus)
                .orderByDesc(Report::getCreateTime);
        IPage<Report> p = baseMapper.selectPage(page, wrapper);

        IPage<ReportVO> voPage = p.convert(ReportVO::from);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    @Override
    public ReportVO detail(Long id) {
        Report r = baseMapper.selectById(id);
        if (r == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "举报记录不存在");
        }
        ReportVO vo = ReportVO.from(r);
        fillAssociations(Collections.singletonList(vo));
        return vo;
    }

    @Override
    @Transactional
    public Long create(ReportSaveDTO dto) {
        // 校验目标
        String targetUserId;
        if (TARGET_POST == dto.getTargetType()) {
            Post p = postMapper.selectById(dto.getTargetId());
            if (p == null) {
                throw new BizException(ResponseCode.NOT_FOUND, "举报的帖子不存在");
            }
            targetUserId = p.getUserId();
        } else if (TARGET_COMMENT == dto.getTargetType()) {
            Comment c = commentMapper.selectById(dto.getTargetId());
            if (c == null) {
                throw new BizException(ResponseCode.NOT_FOUND, "举报的评论不存在");
            }
            targetUserId = c.getUserId();
        } else {
            throw new BizException(ResponseCode.PARAM_ERROR, "不支持的举报目标类型");
        }

        // 不能举报自己的内容
        String currentUserId = AuthContext.userId();
        if (currentUserId.equals(targetUserId)) {
            throw new BizException(ResponseCode.PARAM_ERROR, "不能举报自己的内容");
        }

        // 同一人对同一目标不能重复举报
        Long exist = baseMapper.selectCount(new LambdaQueryWrapper<Report>()
                .eq(Report::getUserId, currentUserId)
                .eq(Report::getTargetType, dto.getTargetType())
                .eq(Report::getTargetId, dto.getTargetId())
                .eq(Report::getStatus, 0));
        if (exist != null && exist > 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "您已经举报过该内容,请等待处理");
        }

        Report r = new Report();
        r.setUserId(currentUserId);
        r.setTargetType(dto.getTargetType());
        r.setTargetId(dto.getTargetId());
        r.setReasonType(dto.getReasonType());
        r.setContent(xssSanitizer.clean(dto.getContent()));
        r.setStatus(0);
        r.setCreateBy(currentUserId);
        r.setCreateTime(LocalDateTime.now());
        baseMapper.insert(r);
        return r.getId();
    }

    @Override
    @Transactional
    public boolean handle(Long id, ReportHandleDTO dto) {
        Report exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "举报记录不存在");
        }
        if (exist.getStatus() != null && exist.getStatus() != 0) {
            throw new BizException(ResponseCode.PARAM_ERROR, "该举报已处理");
        }
        if (dto.getStatus() == null || (dto.getStatus() != 1 && dto.getStatus() != 2)) {
            throw new BizException(ResponseCode.PARAM_ERROR, "处理结果不正确");
        }

        String currentUserId = AuthContext.userId();

        Report r = new Report();
        r.setId(id);
        r.setStatus(dto.getStatus());
        r.setHandleRemark(dto.getHandleRemark());
        r.setHandleUserId(currentUserId);
        r.setHandleTime(LocalDateTime.now());
        r.setUpdateBy(currentUserId);
        r.setUpdateTime(LocalDateTime.now());
        boolean ok = baseMapper.updateById(r) > 0;

        // 如果是"已处理",则下架被举报的内容
        if (ok && dto.getStatus() == 1) {
            if (TARGET_POST == exist.getTargetType()) {
                Post p = new Post();
                p.setId(exist.getTargetId());
                p.setStatus(0);
                p.setUpdateBy(currentUserId);
                p.setUpdateTime(LocalDateTime.now());
                postMapper.updateById(p);
            } else if (TARGET_COMMENT == exist.getTargetType()) {
                Comment c = new Comment();
                c.setId(exist.getTargetId());
                c.setStatus(0);
                c.setUpdateBy(currentUserId);
                c.setUpdateTime(LocalDateTime.now());
                commentMapper.updateById(c);
            }
        }
        return ok;
    }

    @Override
    public boolean delete(Long id) {
        Report exist = baseMapper.selectById(id);
        if (exist == null) {
            return true;
        }
        // 越权: 仅提交者或管理员可删除
        authz.assertOwnerOrAdmin(exist.getUserId());
        return baseMapper.deleteById(id) > 0;
    }

    /**
     * 填充关联字段: 举报人/处理人/被举报内容作者姓名 + 被举报内容标题/摘要
     */
    private void fillAssociations(List<ReportVO> vos) {
        if (CollectionUtils.isEmpty(vos)) return;

        Set<String> userIds = new HashSet<>();
        Set<Long> postIds = new HashSet<>();
        Set<Long> commentIds = new HashSet<>();
        for (ReportVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) userIds.add(v.getUserId());
            if (StringUtils.hasText(v.getHandleUserId())) userIds.add(v.getHandleUserId());
            if (v.getTargetType() != null) {
                if (TARGET_POST == v.getTargetType()) {
                    postIds.add(v.getTargetId());
                } else if (TARGET_COMMENT == v.getTargetType()) {
                    commentIds.add(v.getTargetId());
                }
            }
        }

        // 用户名
        Map<String, String> nameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
            for (UserInfo u : users) {
                nameMap.put(u.getUserId(), u.getUserName());
            }
        }

        // 帖子标题
        Map<Long, Post> postMap = new HashMap<>();
        if (!postIds.isEmpty()) {
            List<Post> posts = postMapper.selectBatchIds(postIds);
            for (Post p : posts) {
                postMap.put(p.getId(), p);
            }
        }

        // 评论
        Map<Long, Comment> commentMap = new HashMap<>();
        if (!commentIds.isEmpty()) {
            List<Comment> comments = commentMapper.selectBatchIds(commentIds);
            for (Comment c : comments) {
                commentMap.put(c.getId(), c);
            }
        }

        for (ReportVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) {
                v.setUserName(nameMap.get(v.getUserId()));
            }
            if (StringUtils.hasText(v.getHandleUserId())) {
                v.setHandleUserName(nameMap.get(v.getHandleUserId()));
            }
            if (v.getTargetType() != null) {
                if (TARGET_POST == v.getTargetType()) {
                    Post p = postMap.get(v.getTargetId());
                    if (p != null) {
                        v.setTargetTitle(p.getTitle());
                        v.setTargetContent(extractSummary(p.getContent()));
                        v.setTargetUserId(p.getUserId());
                        v.setTargetUserName(nameMap.get(p.getUserId()));
                    }
                } else if (TARGET_COMMENT == v.getTargetType()) {
                    Comment c = commentMap.get(v.getTargetId());
                    if (c != null) {
                        v.setTargetContent(extractSummary(c.getContent()));
                        v.setTargetUserId(c.getUserId());
                        v.setTargetUserName(nameMap.get(c.getUserId()));
                    }
                }
            }
        }
    }

    /** 简单摘要: 去 HTML 标签 + 截断 */
    private String extractSummary(String text) {
        if (!StringUtils.hasText(text)) return "";
        String stripped = text.replaceAll("<[^>]+>", " ");
        if (stripped.length() > 120) {
            stripped = stripped.substring(0, 120) + "...";
        }
        return stripped.trim();
    }
}
