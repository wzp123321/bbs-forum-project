# Tasks

## 1. 后端·关注动态流

- [x] 1.1 `PostService` / `PostServiceImpl` 新增 `followFeed(pageNum, pageSize)`：`status = 1` + `EXISTS` 子查询 + 排除自己 + `create_time desc`，手动回填 total。
- [x] 1.2 `PostController` 新增 `POST /followFeed`，标 `@RequireAuth`。
- [x] 1.3 验证：A 关注 B 后 B 发帖出现在 A 的流里；A 未关注任何人返回空页；草稿与审核中帖子不出现。

## 2. 后端·草稿箱

- [x] 2.1 `schema.sql` 更新 `bbs_post.status` 注释为「1正常 0已删 2审核中 3审核不通过 4草稿」。
- [x] 2.2 新增 `DraftSaveDTO`（标题/正文不加必填，Service 断言至少一项非空）。
- [x] 2.3 `PostServiceImpl` 新增 `saveDraft` / `myDrafts` / `draftDetail` / `publishDraft`，均校验归属。
- [x] 2.4 `PostController` 暴露四个接口，全部 `@RequireAuth`。
- [x] 2.5 验证：只写标题可存草稿；空标题空正文被拒；他人草稿详情 404；发布后状态按敏感词落 1 或 2；发布后出现在首页与关注流。

## 3. 后端·找回密码

- [x] 3.1 新增 `ForgotPasswordDTO` / `PasswordResetDTO`（新密码 6–32 位）。
- [x] 3.2 新增 `security/PasswordResetStore`：Caffeine 10 分钟、token→userId、account→token 复用索引。
- [x] 3.3 `AuthController` 新增 `forgotPassword` / `resetPassword`，两接口均加 `@RateLimit`。
- [x] 3.4 校验：联系方式不匹配与账号不存在返回同一文案；重置后清理 `LoginLock`。
- [x] 3.5 验证：正确用户名 + 邮箱拿到令牌；错误邮箱被拒；令牌可用一次；过期/重复使用返回失效；新密码可登录。

## 4. 前端·关注流

- [x] 4.1 `apis/post` 增加 `followFeedApi`。
- [x] 4.2 首页 tab 前置「关注」（登录可见），切换时调 `followFeed` 并重置分页，补空态。

## 5. 前端·草稿箱

- [x] 5.1 `apis/post` 增加 `saveDraftApi` / `myDraftsApi` / `draftDetailApi` / `publishDraftApi`。
- [x] 5.2 发帖页：加「存草稿」按钮；带 `draftId` 时拉草稿回填并按更新提交。
- [x] 5.3 新增用户中心「我的草稿」页与路由，支持继续编辑、发布、删除。

## 6. 前端·找回密码

- [x] 6.1 `apis/auth` 增加 `forgotPasswordApi` / `resetPasswordApi`。
- [x] 6.2 登录页加「忘记密码」两步弹窗，成功后回登录。

## 7. 验收

- [x] 7.1 `mvn compile` 通过。
- [x] 7.2 两个前端包 `ts-check` 通过（顺带修掉 `edit-profile.vue` 里 `att.url` 可能为 undefined 的类型错误）。
- [x] 7.3 接口走查（本地库 + 9999 真实接口）：
      - 关注流：admin 关注 alice → `followFeed` 返回 alice 的帖子；alice 未关注任何人 → `total: 0`；无 token → 401。
      - 草稿：仅标题可存（id=2）；空标题空正文 → 400「标题和正文至少填一项」；alice 读 admin 草稿 → 404；正文为空发布 → 400「请先填写正文」；补全后发布 → `status: 1`；草稿箱随之清空；公开列表不含草稿。
      - 找回密码：错误邮箱与不存在用户 → 同一文案「账号或绑定信息不匹配」；正确邮箱（忽略大小写）拿到 600s 令牌；60s 内重复申请复用同一令牌；重置成功；令牌二次使用 → 400「重置链接已失效」；3 位新密码 → 400「密码长度 6-32」。
- [x] 7.4 回归：首页三个 tab、帖子详情、发帖、消息中心不受影响；`post/delete` 顺带补了归属校验（原实现任何登录用户都能删他人帖子），实测 alice 删 admin 的帖子返回 403。
