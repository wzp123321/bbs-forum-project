package com.bbs.bbsadmin.entity.dto;

import lombok.Data;

/**
 * 修改资料入参
 */
@Data
public class UserUpdateDTO {

    private String email;

    private String phone;

    /** 0未知 1男 2女 */
    private String gender;
}
