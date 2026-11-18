# Proposal

## Why

论坛的读写闭环已经通了，但两件事让它在真实使用中「不像一个社区」：

1. **用户没有画像**。`user_info` 只有用户名、密码、性别、邮箱、手机，没有头像、昵称、签名。所有列表里作者就是一个用户名，评论区无法建立对人的印象。
2. **互动没有回音**。被点赞、被评论、被回复、被关注，当事人完全不知道。用户发完帖不会回来，因为没有任何理由回来。

两者互为前提：通知列表没有头像昵称就没有信息量；头衔和画像没有通知就没有展示场景。所以合并成一个变更做。

## What Changes

- `user_info` 新增 `nickname`、`avatar`、`signature` 三个字段；注册时昵称默认取用户名；既有数据回填。
- 资料编辑支持昵称、头像（复用现有附件上传）、个性签名；昵称与签名做长度和内容校验。
- 帖子卡片、评论、用户主页统一展示「头像 + 昵称」，昵称为空回退用户名，头像为空显示首字母占位。
- 新增 `bbs_notification` 表与五类通知：点赞帖子、评论帖子、回复评论、关注、系统公告。
- 通知写入在既有互动逻辑内同步完成：自己触发的不产生通知，互动被撤销（取消点赞 / 取消关注）时对应通知一并删除。
- 新增通知接口：未读数、分页列表、单条已读、全部已读。
- 用户端顶部新增消息铃铛（未读角标），新增消息中心页；管理端用户列表补昵称与头像列。

## Capabilities

### New Capabilities

- `user-profile`：用户档案字段、编辑规则，以及头像昵称在各展示位的一致性口径。
- `notification`：互动通知的生成规则、查询与已读状态。

### Modified Capabilities

（无。P0 的四份规格尚未归档进 `specs/`，本次不修改它们。）

## Impact

**数据库**

- `user_info` 增加 `nickname`、`avatar`、`signature`，并对既有行回填 `nickname = user_name`。
- 新增 `bbs_notification` 表（接收者、触发者、类型、目标、摘要、已读、时间）。

**后端**（`server/bbs-admin`）

- `UserInfo` 实体、`UserUpdateDTO`、`UserVO` 扩展字段；`UserInfoServiceImpl.updateProfile` 增加校验。
- 新增 `Notification` 实体、Mapper、Service、`NotificationController`。
- `LikeRecordServiceImpl`、`CommentServiceImpl`、`FollowRecordServiceImpl` 增加通知写入 / 撤销。
- `PostVO`、`CommentVO` 及 `fillAssociations` 补头像与昵称，避免前端二次请求用户信息。
- `UserInfoController.selectUserInfo` 改为返回 `UserVO`（当前直接返回实体，把密码哈希暴露给任何登录用户）。

**前端**（`web/packages/bbs-web`、`web/packages/bbs-admin`）

- 用户端：资料编辑页补三个字段、头部消息铃铛、消息中心页、各展示位头像。
- 管理端：用户列表补昵称与头像列。
