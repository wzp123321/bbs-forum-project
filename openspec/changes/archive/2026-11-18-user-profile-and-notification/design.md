# Design

## Context

现有互动逻辑集中在三个 Service：`LikeRecordServiceImpl`（点赞）、`CommentServiceImpl`（评论与回复）、`FollowRecordServiceImpl`（关注）。三者都已经在事务里维护计数，是最自然的通知写入点，不需要额外的事件总线。

用户信息在列表页通过 `fillAssociations` 批量回填（一次 `selectBatchIds`），扩展头像昵称不需要增加查询次数。

前端是纯 SPA + Element Plus，没有 WebSocket 基础设施；后端是单体 Spring Boot，没有 MQ。

## Decisions

### D1：通知表设计

新增 `bbs_notification`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | BIGINT PK | 自增 |
| `user_id` | VARCHAR(32) | 接收者 |
| `actor_id` | VARCHAR(32) NULL | 触发者，系统公告为 NULL |
| `type` | TINYINT | 1 赞帖子 2 评论帖子 3 回复评论 4 关注 5 系统 |
| `post_id` | BIGINT NULL | 关联帖子，便于跳转 |
| `comment_id` | BIGINT NULL | 关联评论，回复类通知用于定位楼层 |
| `content` | VARCHAR(200) | 摘要（评论正文截断、公告标题） |
| `is_read` | TINYINT | 0 未读 1 已读 |
| `create_time` | DATETIME | |

索引：`idx_notify_user (user_id, is_read, create_time)` 覆盖「某人的未读倒序列表」与未读计数两个查询；`uk_notify (user_id, actor_id, type, post_id, comment_id)` 用于 D2 的唯一性约束。

不建 `deleted` 列：通知是流水，删除即物理删除（撤销互动时），避免逻辑删除与唯一键冲突——`bbs_like` 已经踩过这个坑。

### D2：撤销与去重的实现方式

唯一键用 `INSERT ... ON DUPLICATE KEY UPDATE create_time = VALUES(create_time)`：重复点赞（toggle 撤销再点）不会产生两条，且会刷新时间排序。

撤销时按 `(user_id, actor_id, type, post_id, comment_id)` 精确删除。这比在通知上挂状态字段简单，也符合「取消点赞后那条通知就不该存在」的直觉。

代价：`post_id` / `comment_id` 为 NULL 时 MySQL 唯一键不生效（NULL 互不相等），所以关注通知（无 post）需要另用 `post_id = 0` 占位，不能留 NULL。表里这两个字段定为 `NOT NULL DEFAULT 0`。

### D3：通知写入放在互动的事务内

在 `like` / `cancel` / `createComment` / `follow` / `unfollow` 里同步写通知，与计数更新同事务。

理由：数据量是单人运营级别，同步写入的延迟可以忽略；引入异步会带来「点赞成功但通知丢了」的一致性问题，还要额外解释消息可靠性，不划算。

代价：单次点赞多一次 insert，事务变长一点——接受。

### D4：头像存储复用附件接口

头像走已有的 `POST /admin/attachment/upload`，返回 URL 后写进 `user_info.avatar`。不新增头像专用接口，也不在用户表存二进制。

校验只要求「以本站上传返回的前缀开头」，防止用户填外链导致图片挂掉或做外链追踪。

### D5：未读数用轮询，不引 WebSocket

顶部铃铛每 60 秒拉一次未读数，页面切回前台（`visibilitychange`）时立即拉一次。

理由：真实在线人数是个位数，长连接带来的运维成本（连接管理、心跳、重连）远高于它带来的实时性收益。单机单体架构下，也没必要为此引入 Redis 或 MQ——这与项目「不引入中间件」的既有约束一致。

代价：通知最多延迟 60 秒出现。可接受。

### D6：昵称回退放服务端

`UserVO` / `PostVO` / `CommentVO` 统一在转换时做 `nickname == null || blank ? userName : nickname`，前端只渲染一个字段。

理由：前端有多处展示位（列表、详情、评论、通知、主页），回退逻辑放前端会复制多份，必然漂移。

## Risks / Trade-offs

| 风险 | 处理 |
|---|---|
| 存量用户没有昵称，回退逻辑依赖 `nickname` 为 NULL | 升级脚本 `UPDATE user_info SET nickname = user_name WHERE nickname IS NULL`；同时服务端保留回退，双保险 |
| 通知表随互动线性增长，长期无人清理 | 本期不做归档；体量到十万级再加定时清理（届时也只需一条 delete） |
| 唯一键包含 `comment_id NOT NULL DEFAULT 0`，关注类通知全靠 0 占位 | 在实体注释与建表注释里写明，避免后来者改成 NULL |
| 评论删除后通知仍在，点击可能 404 | 通知跳转页对「内容已删除」做兜底提示，不静默失败 |

## Migration

`schema.sql` 里补列与建表语句（幂等 `IF NOT EXISTS`），另附升级语句：

```sql
ALTER TABLE user_info
  ADD COLUMN nickname VARCHAR(50) NULL COMMENT '昵称' AFTER user_name,
  ADD COLUMN avatar VARCHAR(255) NULL COMMENT '头像URL' AFTER nickname,
  ADD COLUMN signature VARCHAR(100) NULL COMMENT '个性签名' AFTER avatar;

UPDATE user_info SET nickname = user_name WHERE nickname IS NULL OR nickname = '';
```

H2/MySQL 均支持；本地开发库直接执行同一份语句。
