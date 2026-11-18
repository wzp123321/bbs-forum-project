# Spec Delta

## Purpose

定义附件上传与访问的鉴权边界：上传动作必须由登录用户发起且记录归属，上传产物保持匿名可读。

## ADDED Requirements

### Requirement: 上传接口要求登录态

`POST /admin/attachment/upload` SHALL 经过鉴权拦截器，未携带有效 `Authorization: Bearer <token>` 时 MUST 返回 401，不写入任何文件。

- 拦截器 MUST NOT 将 `/admin/attachment/**` 整体排除在鉴权之外。
- 上传成功后归属用户 MUST 取自 `AuthContext`，MUST NOT 由请求参数指定。

#### Scenario: 登录用户上传成功

- **WHEN** 携带有效 token 上传一张 PNG
- **THEN** 返回 200 与附件信息，记录的 `user_id` 为 token 对应的用户

#### Scenario: 未登录上传被拒绝

- **WHEN** 不携带 token 上传文件
- **THEN** 返回 401，上传目录下不新增文件

### Requirement: 上传结果公开可读

上传后的静态资源 SHALL 保持匿名可读，路径前缀由 `bbs.upload.url-prefix` 决定（默认 `/upload/`）。

- 静态资源放行 MUST 仅覆盖 `url-prefix` 前缀，MUST NOT 覆盖上传接口路径。

#### Scenario: 未登录访问已上传图片

- **WHEN** 游客直接请求 `/upload/avatar/xxx.png`
- **THEN** 返回 200 与图片内容
