package com.bbs.bbsadmin.security;

import com.bbs.bbsadmin.config.AuthProperties;
import com.bbs.bbsadmin.exception.BizException;
import com.bbs.bbsadmin.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 越权校验工具
 * - 普通用户: 只能操作自己创建的记录
 * - 管理员 (adminUserIds 配置中的用户): 可操作任意记录
 *
 * 用法:
 *   Authz.assertOwnerOrAdmin(post.getUserId());         // 帖子/评论作者校验
 *   Authz.assertAdmin();                                 // 后台管理操作
 */
@Component
public class Authz {

    @Autowired
    private AuthProperties authProperties;

    private volatile Set<String> adminIdSet;

    private Set<String> adminSet() {
        Set<String> s = adminIdSet;
        if (s == null) {
            synchronized (this) {
                if (adminIdSet == null) {
                    s = new HashSet<>(authProperties.getAdminUserIds());
                    adminIdSet = s;
                }
            }
        }
        return s;
    }

    /** 当前用户是否是管理员 */
    public boolean isAdmin() {
        String uid = AuthContext.userId();
        return uid != null && adminSet().contains(uid);
    }

    /** 必须是管理员, 否则 403 */
    public void assertAdmin() {
        if (!isAdmin()) {
            throw new BizException(ResponseCode.FORBIDDEN, "需要管理员权限");
        }
    }

    /**
     * 资源拥有者 或 管理员 才能操作
     * @param ownerUserId 资源创建者 userId
     */
    public void assertOwnerOrAdmin(String ownerUserId) {
        if (isAdmin()) return;
        String cur = AuthContext.userId();
        if (cur == null || !cur.equals(ownerUserId)) {
            throw new BizException(ResponseCode.FORBIDDEN, "无权操作他人资源");
        }
    }
}
