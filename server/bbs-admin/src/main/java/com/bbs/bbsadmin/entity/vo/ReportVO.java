package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Report;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 举报 VO
 */
@Data
public class ReportVO {

    private Long id;
    private String userId;
    private String userName;
    private Integer targetType;
    private String targetTypeText;
    private Long targetId;
    private String targetTitle;
    private String targetContent;
    private String targetUserId;
    private String targetUserName;
    private String reasonType;
    private String content;
    private Integer status;
    private String handleUserId;
    private String handleUserName;
    private String handleRemark;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;

    public static ReportVO from(Report r) {
        if (r == null) return null;
        ReportVO v = new ReportVO();
        BeanUtils.copyProperties(r, v);
        if (r.getTargetType() != null) {
            v.setTargetTypeText(r.getTargetType() == 1 ? "帖子" : "评论");
        }
        return v;
    }
}
