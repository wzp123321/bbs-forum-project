package com.bbs.bbsadmin.controller;

import com.bbs.bbsadmin.entity.vo.AttachmentVO;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.R;
import com.bbs.bbsadmin.response.ResponseCode;
import com.bbs.bbsadmin.security.AuthContext;
import com.bbs.bbsadmin.security.annotation.RequireAuth;
import com.bbs.bbsadmin.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "AttachmentController", description = "附件上传")
@RestController
@RequestMapping("admin/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Operation(summary = "上传附件")
    @RequireAuth
    @PostMapping("/upload")
    public R<AttachmentVO> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(value = "bizType", required = false) String bizType) {
        String userId = AuthContext.userId();
        if (userId == null) {
            throw new BizException(ResponseCode.UNAUTHORIZED);
        }
        return R.data(attachmentService.upload(file, userId, bizType));
    }
}
