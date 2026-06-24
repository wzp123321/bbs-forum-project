package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.Attachment;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 附件 VO
 */
@Data
public class AttachmentVO {

    private Long id;
    private String userId;
    private String bizType;
    private Long bizId;
    private String originName;
    private String fileName;
    private String filePath;
    private String url;
    private String contentType;
    private Long size;
    private String storageType;
    private Integer status;
    private LocalDateTime createTime;

    public static AttachmentVO from(Attachment a) {
        if (a == null) return null;
        AttachmentVO v = new AttachmentVO();
        BeanUtils.copyProperties(a, v);
        return v;
    }
}
