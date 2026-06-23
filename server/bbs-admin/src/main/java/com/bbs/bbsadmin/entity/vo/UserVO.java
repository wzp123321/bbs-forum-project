package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * 用户 VO (脱敏,不返回密码)
 */
@Data
public class UserVO {

    private String userId;
    private String userName;
    private String gender;
    private String email;
    private String phone;
    private LocalDateTime createTime;

    public static UserVO from(UserInfo u) {
        if (u == null) return null;
        UserVO v = new UserVO();
        BeanUtils.copyProperties(u, v);
        return v;
    }
}
