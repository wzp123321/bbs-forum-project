# Proposal

## Why

P1 排期里最后三项体验缺口仍未落地，都是「用户已经能感觉到不方便」的位置：

- **没有关注动态流**：关注了人却看不到 TA 的新帖，关注功能只停在「数字 +1」，用户没有回来的理由。
- **没有草稿箱**：发帖页一关就全丢，长文作者不敢写，等于把最重度的用户挡在门外。
- **没有找回密码**：忘记密码只能重新注册，账号与历史内容直接失联。
- 另外 `bbs_post.status` 只有「正常 / 已删 / 审核中 / 审核不通过」四态，草稿无处安放，需要先定状态语义再动代码。

## What Changes

- **关注动态流**：新增 `POST /admin/post/followFeed`，按当前用户关注关系取帖子，时间倒序分页；用户端首页加「关注」tab（未登录不显示）。
- **草稿箱**：`bbs_post.status = 4` 表示草稿；新增保存草稿 / 我的草稿 / 草稿详情 / 发布草稿四个接口；发帖页加「存草稿」，用户中心加「我的草稿」页（编辑、发布、删除）。
- **找回密码**：新增 `forgotPassword`（校验用户名 + 绑定邮箱/手机）与 `resetPassword`（一次性令牌 + 新密码）两步；登录页加「忘记密码」入口。
- 草稿状态语义与详情可见性一并收紧：非正常状态（`status != 1`）的帖子详情一律 404，仅作者本人可通过草稿详情接口读取自己的草稿。

## Capabilities

### New Capabilities

- `follow-feed`: 关注动态流的取数范围、排序与可见性。
- `draft-box`: 草稿的保存、列举、编辑、发布与删除规则。
- `password-reset`: 忘记密码的身份校验、令牌生命周期与重置约束。

### Modified Capabilities

（无。既有 6 份规格不受影响：草稿不在公开列表口径内，`site-metrics` 与 `post-browsing` 无需变更。）

## Impact

**后端**（`server/bbs-admin`）

- `PostController` / `PostService` / `PostServiceImpl`：`followFeed`、`saveDraft`、`myDrafts`、`draftDetail`、`publishDraft`。
- 新增 `dto/DraftSaveDTO`、`dto/PasswordResetDTO`、`dto/ForgotPasswordDTO`；视需要补 `vo/DraftVO`（可复用 `PostVO`）。
- `AuthController`：`forgotPassword` / `resetPassword`。
- 新增 `security/PasswordResetStore`（Caffeine，令牌 10 分钟、按账号限频）。
- `PostServiceImpl.assertReadable`：草稿态对非作者保持 404。

**数据库**

- `bbs_post.status` 注释扩展为「1正常 0已删 2审核中 3审核不通过 4草稿」，不新增字段。
- 索引 `idx_post_user` 已覆盖「我的草稿」按用户过滤；草稿列表再按 `update_time` 排序，量级小，不额外建索引。

**前端**（`web/packages/bbs-web`）

- `pages/home/index.vue`：新增「关注」tab（登录可见）。
- `views/post-publish`：新增「存草稿」按钮。
- 新增 `views/user-center/pages/drafts.vue` 与路由；`views/login/index.vue` 加忘记密码弹窗。
- `apis/post`、`apis/auth`：补对应封装。

**风险与取舍**

- 项目没有邮件 / 短信通道，`forgotPassword` 只能把一次性令牌直接返回给调用方，等价于「知道用户名 + 绑定邮箱即可重置密码」。这是弱于验证码的验证强度，靠限频（同账号 60 秒一次、同 IP 每小时次数上限）+ 令牌 10 分钟单次有效缓解；接入 SMTP 后应改为令牌只走邮件、接口不返回值。
- 关注动态流用 `EXISTS` 子查询而非先查关注列表再 `IN`，避免关注量大时拼超长 IN 列表；大 V 场景下的深分页仍是已知瓶颈，本期不处理。
