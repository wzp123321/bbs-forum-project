package com.bbs.bbsadmin.entity.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录出参
 */
@Data
@Builder
public class LoginVO {

    /** JWT */
    private String token;

    /** 用户ID */
    private String userId;

    /** 用户名 */
    private String userName;

    /** 昵称 (暂用 userName) */
    private String nickName;

    /** 头像 */
    private String avatar;
}
