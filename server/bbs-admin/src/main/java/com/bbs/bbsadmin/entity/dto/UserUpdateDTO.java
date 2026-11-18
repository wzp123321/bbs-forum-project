package com.bbs.bbsadmin.entity.dto;

import lombok.Data;

/**
 * 修改资料入参
 */
@Data
public class UserUpdateDTO {

    /** 昵称: 2-20 字符, 为空表示不修改 */
    private String nickname;

    /** 头像 URL: 必须是本站上传接口返回的地址, 为空表示清空 */
    private String avatar;

    /** 个性签名: 0-100 字符 */
    private String signature;

    private String email;

    private String phone;

    /** 0未知 1男 2女 */
    private String gender;
}
