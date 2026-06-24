package com.bbs.bbsadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bbs.bbsadmin.entity.Feedback;
import com.bbs.bbsadmin.entity.UserInfo;
import com.bbs.bbsadmin.entity.dto.FeedbackPageQuery;
import com.bbs.bbsadmin.entity.dto.FeedbackReplyDTO;
import com.bbs.bbsadmin.entity.dto.FeedbackSaveDTO;
import com.bbs.bbsadmin.entity.vo.FeedbackVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.mapper.FeedbackMapper;
import com.bbs.bbsadmin.mapper.UserInfoMapper;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.Authz;
import com.bbs.bbsadmin.security.XssSanitizer;
import com.bbs.bbsadmin.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private XssSanitizer xssSanitizer;

    @Autowired
    private Authz authz;

    @Override
    public IPage<FeedbackVO> pageQueryVO(FeedbackPageQuery query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : query.getPageSize();
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Feedback::getContent, query.getKeyword());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(Feedback::getType, query.getType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Feedback::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getUserId())) {
            wrapper.eq(Feedback::getUserId, query.getUserId());
        }
        wrapper.orderByAsc(Feedback::getStatus)
               .orderByDesc(Feedback::getCreateTime);
        IPage<Feedback> p = baseMapper.selectPage(page, wrapper);

        IPage<FeedbackVO> voPage = p.convert(FeedbackVO::from);
        if (!CollectionUtils.isEmpty(voPage.getRecords())) {
            fillAssociations(voPage.getRecords());
        }
        return voPage;
    }

    @Override
    public FeedbackVO detail(Long id) {
        Feedback f = baseMapper.selectById(id);
        if (f == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "反馈不存在");
        }
        FeedbackVO vo = FeedbackVO.from(f);
        fillAssociations(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public Long create(FeedbackSaveDTO dto) {
        Feedback f = new Feedback();
        f.setUserId(AuthContext.userId());
        f.setType(dto.getType());
        f.setContent(xssSanitizer.clean(dto.getContent()));
        f.setStatus(0);
        f.setCreateBy(AuthContext.userId());
        f.setCreateTime(LocalDateTime.now());
        baseMapper.insert(f);
        return f.getId();
    }

    @Override
    public boolean reply(Long id, FeedbackReplyDTO dto) {
        // 越权: 仅管理员可回复反馈
        authz.assertAdmin();
        Feedback exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BizException(ResponseCode.NOT_FOUND, "反馈不存在");
        }
        Feedback f = new Feedback();
        f.setId(id);
        f.setReply(dto.getReply());
        f.setReplyUserId(AuthContext.userId());
        f.setReplyTime(LocalDateTime.now());
        f.setStatus(1);
        f.setUpdateBy(AuthContext.userId());
        f.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(f) > 0;
    }

    @Override
    public boolean delete(Long id) {
        Feedback exist = baseMapper.selectById(id);
        if (exist == null) {
            return true;
        }
        // 越权: 仅提交者或管理员可删除
        authz.assertOwnerOrAdmin(exist.getUserId());
        return baseMapper.deleteById(id) > 0;
    }

    private void fillAssociations(List<FeedbackVO> vos) {
        if (CollectionUtils.isEmpty(vos)) return;
        Set<String> userIds = new HashSet<>();
        for (FeedbackVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) userIds.add(v.getUserId());
            if (StringUtils.hasText(v.getReplyUserId())) userIds.add(v.getReplyUserId());
        }
        Map<String, String> nameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
            for (UserInfo u : users) {
                nameMap.put(u.getUserId(), u.getUserName());
            }
        }
        for (FeedbackVO v : vos) {
            if (StringUtils.hasText(v.getUserId())) {
                v.setUserName(nameMap.get(v.getUserId()));
            }
            if (StringUtils.hasText(v.getReplyUserId())) {
                v.setReplyUserName(nameMap.get(v.getReplyUserId()));
            }
        }
    }
}
