package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Feedback;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 反馈 VO
 */
@Data
public class FeedbackVO {

    private Long id;
    private String userId;
    private String userName;
    private String type;
    private String content;
    private String reply;
    private String replyUserId;
    private String replyUserName;
    private LocalDateTime replyTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static FeedbackVO from(Feedback f) {
        if (f == null) return null;
        FeedbackVO v = new FeedbackVO();
        BeanUtils.copyProperties(f, v);
        return v;
    }
}
