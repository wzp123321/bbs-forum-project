package com.bbs.bbsadmin.entity.vo;

import com.bbs.bbsadmin.entity.UserInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户 VO (脱敏, 不返回密码)
 */
@Data
public class UserVO {

    private String userId;
    private String userName;
    /** 展示用昵称: 昵称为空时已回退为 userName */
    private String nickname;
    private String avatar;
    private String signature;
    private String gender;
    private String email;
    private String phone;
    private LocalDateTime createTime;

    public static UserVO from(UserInfo u) {
        if (u == null) return null;
        UserVO v = new UserVO();
        BeanUtils.copyProperties(u, v);
        v.setNickname(displayName(u));
        return v;
    }

    /**
     * 展示名统一口径: 昵称优先, 为空回退用户名。
     * 所有需要展示用户名的 VO 都从这里取值, 避免各处各写一套回退逻辑。
     */
    public static String displayName(UserInfo u) {
        if (u == null) return "";
        return StringUtils.hasText(u.getNickname()) ? u.getNickname() : u.getUserName();
    }
}
