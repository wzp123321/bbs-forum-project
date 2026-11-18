# Design

## 1. 关注动态流

### 接口

| 项 | 值 |
| --- | --- |
| 路径 | `POST /admin/post/followFeed` |
| 鉴权 | `@RequireAuth` |
| 入参 | `{ pageNum, pageSize }` |
| 出参 | `PageResult<PostVO>`（与 `post/page` 同构） |

### 取数

在 `PostServiceImpl` 内新增 `followFeed(pageNum, pageSize)`：

```java
LambdaQueryWrapper<Post> w = new LambdaQueryWrapper<>();
w.eq(Post::getStatus, 1)
 .apply("EXISTS (SELECT 1 FROM bbs_follow f WHERE f.follow_user_id = bbs_post.user_id" +
        " AND f.user_id = {0} AND f.deleted = 0)", me)
 .ne(Post::getUserId, me)
 .orderByDesc(Post::getCreateTime);
```

- 用 `apply` 的 `{0}` 占位符走 MyBatis 参数绑定，不用字符串拼接，避免注入。
- 用 `EXISTS` 而不是「先查关注 ID 再 IN」，关注量大时避免超长 IN。
- 分页沿用现有「先 `selectCount` 再 `selectPage(searchCount=false)` 并手动 `setTotal`」的写法，避免 `Page` 的 total 恒 0 的老坑。
- VO 装配复用 `toVO` + `fillAssociations`，头像昵称回填与首页一致。

### 前端

- `pages/home/index.vue`：`sortTabs` 前置 `{ label: '关注', value: 'follow' }`，用 `userStore` 判断登录后渲染；`fetchList` 在 `activeSort === 'follow'` 时改调 `followFeedApi`。
- 分页状态按 tab 重置（切 tab 时 `pageNum = 1`），空态文案区分「还没有关注的人」。

## 2. 草稿箱

### 状态机

```
[saveDraft 新建] → 4 草稿
[4 草稿] --publishDraft--> 命中敏感词 ? 2 审核中 : 1 正常
[4 草稿] --delete--> 已删（deleted=1）
[1 正常] --编辑--> 保持 1（不因编辑回落草稿）
```

`status = 4` 不进任何 `status = 1` 的公开查询，天然隔离；额外约束「草稿不能出现在关注流」由关注流固定 `status = 1` 保证，不需要额外分支。

### 接口

| 路径 | 鉴权 | 说明 |
| --- | --- | --- |
| `POST /admin/post/saveDraft` | 是 | `{id?, title?, content?, contentType?, categoryId?, tagIds?}`，返回草稿 ID |
| `POST /admin/post/myDrafts` | 是 | `{pageNum, pageSize}`，返回 `PageResult<PostVO>`，按 `update_time desc` |
| `POST /admin/post/draftDetail` | 是 | `{id}`，仅作者可读，否则 404 |
| `POST /admin/post/{id}/publishDraft` | 是 | 校验归属与完整性后置 `1` / `2`，返回最终状态 |
| `POST /admin/post/delete` | 是 | 复用既有删除，草稿同样走这里 |

### 关键实现点

- `DraftSaveDTO`：`title` / `content` 不加 `@NotBlank`，在 Service 里断言「至少一项非空（trim 后）」。
- 更新草稿时先 `selectById`，校验 `userId` 相等且 `status == 4`，否则 403。
- `publishDraft` 复用 `create` 的敏感词判定顺序：先算状态，再写库，返回 `1` 或 `2` 给前端提示「已发布」/「已提交审核」。
- 板块与标签：`categoryId` 允许为空（草稿阶段可不选），发布时若为空则用默认板块（取 `bbs_category` 第一条启用项）；标签写 `bbs_post_tag`，更新时先删后插。
- `assertReadable` 不动：公开详情对 `status != 1` 一律 404 的既有语义保留，草稿阅读走 `draftDetail`，避免把「作者可读」塞进公开详情里带来旁路。

### 前端

- `views/post-publish/index.vue`：底部操作区加「存草稿」次要按钮；有 `draftId` 时改调 `saveDraft` 更新。
- 新增 `views/user-center/pages/drafts.vue`：表格列出标题 / 摘要 / 更新时间 / 操作（继续编辑、发布、删除），路由 `/user-center/drafts`，用户中心侧栏加入口。
- 路由带 `draftId` 跳到发布页：`/post/publish?draftId=123`，页面内 `onMounted` 拉 `draftDetail` 回填。

## 3. 找回密码

### 接口

| 路径 | 鉴权 | 入参 | 出参 |
| --- | --- | --- | --- |
| `POST /admin/auth/forgotPassword` | 否 | `{account, contact}` | `{resetToken, expireSeconds}` |
| `POST /admin/auth/resetPassword` | 否 | `{resetToken, newPassword}` | `{}` |

两个接口都加 `@RateLimit`：`key = auth:forgot`（capacity 5 / 60s）、`key = auth:reset`（capacity 10 / 60s）。

### 令牌存储

`security/PasswordResetStore`（`@Component`）：

- Caffeine `expireAfterWrite(10, MINUTES)`，`maximumSize(10_000)`。
- 结构：`token -> userId`；另有 `accountIndex: account -> token`（同样 10 分钟），用于同账号 60 秒内复用。
- 60 秒复用判定：`accountIndex` 命中且令牌仍在有效期内 → 直接返回旧令牌。

### 校验流程

1. `userInfoService.findByAccount(account)` 取用户；为空 → 通用错误。
2. `contact` 去空格，与 `user.getEmail()`（忽略大小写）或 `user.getPhone()` 比对；都不匹配 → 同一条通用错误。
3. 签发或复用令牌。
4. `resetPassword`：令牌查得到 → `BCrypt.hashpw` 更新密码 → 删除令牌与账号索引 → `loginLock.clear(account)`。

### 取舍

没有 SMTP / 短信通道，令牌只能直接返回给前端，强度等于「知道用户名 + 绑定邮箱」。缓解手段：短信/邮件通道缺失下靠限频、10 分钟单次有效、失败不区分原因。接入邮件服务后应改为「接口只返回成功，令牌走邮件」，前端只保留输入验证码的表单。

### 前端

- `views/login/index.vue`：密码框下加「忘记密码」链接，打开 `el-dialog` 两步表单（第 1 步用户名 + 邮箱/手机，第 2 步新密码 + 确认密码）；成功后提示并回填用户名。

## 4. 兼容与风险

| 点 | 处理 |
| --- | --- |
| 老数据 `status` 取值 | 表里只有 0/1/2/3，新增 4 不影响存量查询 |
| `PostSaveDTO.status` 透传 | 保持原样；发帖页正常路径不传 4，草稿只走 `saveDraft`，避免前端伪造 status |
| 管理后台帖子列表 | 未传 `status` 时仍能看到全部状态（含草稿），管理员可见草稿符合预期 |
| 通知 | 草稿不触发任何通知；发布草稿走正常发帖路径，关注者不收通知（发帖本就不产生通知） |

## 5. 遗留问题（本次不处理）

- **全站 `@Valid` 不生效**：`pom.xml` 未引入 `spring-boot-starter-validation`，实体 DTO 上的 `@NotBlank` / `@Size` 全部形同虚设（例如登录只传 `userName` 时不会报「account 不能为空」，而是走到业务层的「账号或密码错误」）。本次只在 `resetPassword` 内手动兜住密码长度；`PostSaveDTO` 的标题/正文必填仍靠业务逻辑兜底。补依赖会使多个接口的返回从业务错误变成 400 参数错误，属于独立变更，需单独评估。
- **`post/delete` 原本无归属校验**：任何登录用户可删他人帖子。本次实现草稿删除时顺带补了 `authz.assertOwnerOrAdmin`，实测越权删除返回 403。

