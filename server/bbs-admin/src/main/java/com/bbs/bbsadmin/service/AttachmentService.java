package com.bbs.bbsadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bbs.bbsadmin.entity.Attachment;
import com.bbs.bbsadmin.entity.vo.AttachmentVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 附件 Service
 */
public interface AttachmentService extends IService<Attachment> {

    /**
     * 上传文件
     *
     * @param file    上传的文件
     * @param userId  上传人
     * @param bizType 业务类型
     * @return 附件信息
     */
    AttachmentVO upload(MultipartFile file, String userId, String bizType);

    /**
     * 关联附件到业务
     *
     * @param attachmentId 附件ID
     * @param bizType      业务类型
     * @param bizId        业务ID
     */
    void bindBiz(Long attachmentId, String bizType, Long bizId);

    /**
     * 从富文本内容中抽取 &lt;img&gt; 标签引用的图片附件,自动关联到业务.
     * 用于帖子保存/编辑后绑定正文图片.
     *
     * @param content 富文本内容
     * @param bizType 业务类型
     * @param bizId   业务ID
     */
    void bindContentImages(String content, String bizType, Long bizId);
}
