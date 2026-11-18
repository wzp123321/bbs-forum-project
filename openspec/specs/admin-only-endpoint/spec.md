# admin-only-endpoint Specification

## Purpose
管理端专属接口只能由配置的管理员执行，普通登录用户不得越权操作站点数据与账号。

## Requirements

### Requirement: 管理端接口仅管理员可访问

标注 `@RequireAuth(adminOnly = true)` 的接口 SHALL 在鉴权拦截器内校验当前用户是否在 `bbs.auth.admin-user-ids` 中。非管理员的登录用户 MUST 返回 `code=403`，请求不进入 controller。

- 未登录访问 MUST 仍返回 401，优先于管理员校验。
- 管理员身份 MUST 从 token 解析出的 `userId` 判定，MUST NOT 信任请求参数。
- 用户自己的资源操作（帖子/评论删除与编辑、点赞、收藏、关注、发帖、评论、草稿）MUST NOT 标记 `adminOnly`。

#### Scenario: 普通用户调用管理接口

- **WHEN** 非管理员用户调用 `/admin/user/delete`、`/admin/report/page`、`/admin/category/create`
- **THEN** 每个请求返回 `code=403 需要管理员权限`，数据不变

#### Scenario: 管理员调用管理接口

- **WHEN** 配置中的管理员调用 `/admin/user/page`
- **THEN** 返回 `code=200` 与用户分页数据

#### Scenario: 普通用户访问公开接口

- **WHEN** 非管理员用户访问帖子 feed 或关注流
- **THEN** 返回 `code=200`，行为与改动前一致
