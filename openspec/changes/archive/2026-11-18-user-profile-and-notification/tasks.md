# Tasks

## 1. 数据库

- [x] 1.1 `schema.sql`：`user_info` 增加 `nickname` / `avatar` / `signature`；新增 `bbs_notification` 表与索引（唯一键用 `post_id` / `comment_id` `NOT NULL DEFAULT 0` 占位）。
- [x] 1.2 本地库执行升级语句，回填 `nickname = user_name`。验证：`SELECT COUNT(*) FROM user_info WHERE nickname IS NULL` 为 0。

## 2. 后端·用户档案

- [x] 2.1 `UserInfo` 增加三个字段；`UserVO` 增加三字段并在 `from()` 里做昵称回退；`register` 写入默认昵称。
- [x] 2.2 `UserUpdateDTO` 增加三字段；`updateProfile` 增加昵称长度 2–20、签名 ≤100、敏感词过滤、头像前缀校验、邮箱/手机占用校验。
- [x] 2.3 `UserInfoController.selectUserInfo` 返回 `UserVO`（不再返回实体，去掉密码哈希）。验证：响应体无 `password`。
- [x] 2.4 `PostVO` / `CommentVO` 增加 `nickname`、`avatar`；`fillAssociations` 从已批量查出的用户填充。验证：列表接口返回头像昵称且 SQL 条数不变。

## 3. 后端·通知

- [x] 3.1 新增 `Notification` 实体、`NotificationMapper`（含 upsert 与按目标删除）、`NotificationService` / `NotificationServiceImpl`。
- [x] 3.2 在 `LikeRecordServiceImpl.like/cancel` 写入 / 撤销点赞通知；`CommentServiceImpl.create` 按「回复评论 → 通知被回复者，普通评论 → 通知帖子作者」写入。
- [x] 3.3 在 `FollowRecordServiceImpl.follow/unfollow` 写入 / 撤销关注通知。
- [x] 3.4 新增 `NotificationController`：`/unreadCount`、`/page`（支持 `onlyUnread`）、`/read`、`/readAll`，全部只作用于当前登录用户。
- [x] 3.5 编译通过并启动，桩数据验证通知生成与未读数。

## 4. 前端·用户端

- [x] 4.1 `apis/notification` 接口封装与类型。
- [x] 4.2 资料编辑页增加昵称、头像（复用附件上传）、个性签名，前端做长度校验与回显。
- [x] 4.3 新增消息中心页（列表 + 只看未读 + 全部已读 + 点击跳转并标记已读），路由挂到用户中心。
- [x] 4.4 顶部导航增加消息铃铛与未读角标（60 秒轮询 + 回到前台立即刷新）。
- [x] 4.5 帖子卡片、详情、评论、用户主页的头像昵称改为统一组件渲染，无头像显示首字符占位。
      说明：首字符占位与 `resolveFileUrl` 已统一，但渲染模板仍在各页面重复，未抽公共组件，记为技术债。

## 5. 前端·管理端

- [x] 5.1 用户列表补昵称与头像列。

## 6. 验收

- [x] 6.1 `mvn compile` + 两个前端包 `ts-check` 通过。
- [x] 6.2 端到端走查（本地库 + 9999 真实接口）：admin 点赞 alice 的帖子 → alice 未读数 0 → 1，通知列表出现「赞了你的帖子」；admin 取消点赞 → 未读回落 0，点赞通知随之删除；再点赞可恢复。
- [x] 6.3 反向验证：alice 给自己的帖子点赞不产生通知（未读数不变）；未登录访问 `/admin/notification/*` 返回 401。
