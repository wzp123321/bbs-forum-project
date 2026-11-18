package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.bbsadmin.entity.Category;
import com.bbs.bbsadmin.entity.Comment;
import com.bbs.bbsadmin.entity.Post;
import com.bbs.bbsadmin.entity.Report;
import com.bbs.bbsadmin.entity.vo.ActivityVO;
import com.bbs.bbsadmin.entity.vo.StatOverviewVO;
import com.bbs.bbsadmin.entity.vo.StatSummaryVO;
import com.bbs.bbsadmin.mapper.CategoryMapper;
import com.bbs.bbsadmin.mapper.CommentMapper;
import com.bbs.bbsadmin.mapper.PostMapper;
import com.bbs.bbsadmin.mapper.ReportMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 站点统计实现
 * 口径统一为: 正常状态 + 未逻辑删除 (deleted 由 @TableLogic 自动过滤)
 */
@Service
public class StatServiceImpl implements StatService {

    /** 单类事件取样条数, 归并后截断为 ACTIVITY_LIMIT */
    private static final int ACTIVITY_FETCH = 10;
    private static final int ACTIVITY_LIMIT = 10;
    /** 摘要截断长度 */
    private static final int SUMMARY_MAX = 30;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public StatSummaryVO summary() {
        StatSummaryVO vo = new StatSummaryVO();
        vo.setUserCount(userInfoMapper.selectCount(null));
        vo.setPostCount(countNormalPosts());
        vo.setCategoryCount(categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getStatus, 1)));
        return vo;
    }

    @Override
    public StatOverviewVO overview() {
        StatOverviewVO vo = new StatOverviewVO();
        StatSummaryVO base = summary();
        vo.setUserCount(base.getUserCount());
        vo.setPostCount(base.getPostCount());
        vo.setCategoryCount(base.getCategoryCount());

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        vo.setTodayPostCount(postMapper.selectCount(new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, 1)
                .ge(Post::getCreateTime, todayStart)));
        vo.setPendingReportCount(reportMapper.selectCount(
                new LambdaQueryWrapper<Report>().eq(Report::getStatus, 0)));
        vo.setRecentActivities(recentActivities());
        return vo;
    }

    private Long countNormalPosts() {
        return postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1));
    }

    /**
     * 最近动态: 帖子/评论/举报各取最近 N 条, 按时间倒序归并后截断
     * 规模小, 不做 SQL 级 UNION
     */
    private List<ActivityVO> recentActivities() {
        List<ActivityVO> all = new ArrayList<>();

        List<Post> posts = postMapper.selectList(new LambdaQueryWrapper<Post>()
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime)
                .last("LIMIT " + ACTIVITY_FETCH));
        for (Post p : posts) {
            if (p.getCreateTime() == null) continue;
            all.add(build("post", p.getId(), "发布帖子: " + p.getTitle(), p.getCreateTime()));
        }

        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime)
                .last("LIMIT " + ACTIVITY_FETCH));
        for (Comment c : comments) {
            if (c.getCreateTime() == null) continue;
            all.add(build("comment", c.getId(), "发表评论: " + c.getContent(), c.getCreateTime()));
        }

        List<Report> reports = reportMapper.selectList(new LambdaQueryWrapper<Report>()
                .orderByDesc(Report::getCreateTime)
                .last("LIMIT " + ACTIVITY_FETCH));
        for (Report r : reports) {
            if (r.getCreateTime() == null) continue;
            String target = r.getTargetType() != null && r.getTargetType() == 2 ? "评论" : "帖子";
            all.add(build("report", r.getId(), "举报" + target + ": " + r.getReasonType(), r.getCreateTime()));
        }

        all.sort(Comparator.comparing(ActivityVO::getTime).reversed());
        return all.size() > ACTIVITY_LIMIT ? new ArrayList<>(all.subList(0, ACTIVITY_LIMIT)) : all;
    }

    private ActivityVO build(String type, Long id, String summary, LocalDateTime time) {
        ActivityVO vo = new ActivityVO();
        vo.setType(type);
        vo.setId(id);
        vo.setSummary(truncate(summary));
        vo.setTime(time);
        return vo;
    }

    private String truncate(String text) {
        if (text == null) return "";
        String plain = text.replaceAll("<[^>]+>", "").trim();
        return plain.length() <= SUMMARY_MAX ? plain : plain.substring(0, SUMMARY_MAX) + "…";
    }
}
