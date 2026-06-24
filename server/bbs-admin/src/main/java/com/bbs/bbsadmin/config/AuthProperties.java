package com.bbs.bbsadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 鉴权相关配置
 * - adminUserIds: 拥有管理员权限的用户 ID 列表 (逗号分隔或列表)
 *   该列表中的用户可执行:
 *     - 删除/编辑任意用户的帖子
 *     - 处理举报
 *     - 回复反馈
 *     - 删除任意反馈
 *   普通用户只能操作自己创建的记录
 */
@Data
@Component
@ConfigurationProperties(prefix = "bbs.auth")
public class AuthProperties {

    /** 管理员用户 ID 列表 */
    private List<String> adminUserIds = Collections.emptyList();
}
