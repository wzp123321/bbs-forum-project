package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Feedback;
import com.bbs.bbsadmin.entity.dto.FeedbackPageQuery;
import com.bbs.bbsadmin.entity.dto.FeedbackReplyDTO;
import com.bbs.bbsadmin.entity.dto.FeedbackSaveDTO;
import com.bbs.bbsadmin.entity.vo.FeedbackVO;

public interface FeedbackService extends IService<Feedback> {

    /** 分页查询(返回 VO) */
    IPage<FeedbackVO> pageQueryVO(FeedbackPageQuery query);

    /** 详情 */
    FeedbackVO detail(Long id);

    /** 用户提交反馈 */
    Long create(FeedbackSaveDTO dto);

    /** 管理员回复 */
    boolean reply(Long id, FeedbackReplyDTO dto);

    /** 删除 */
    boolean delete(Long id);
}
