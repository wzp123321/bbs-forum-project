# Proposal

## Why

`WebMvcConfig` 把 `/admin/attachment/**` 整体排除在鉴权拦截器之外，本意是放行上传后的静态资源，实际把上传接口 `/admin/attachment/upload` 一并放开了。拦截器不执行 → `AuthContext` 不填充 → `AttachmentController` 里 `userId == null`，于是**登录用户上传也一律返回 401**，头像上传、富文本插图全部不可用；同时静态资源又已由 `upload.url-prefix`（`/upload/`）单独放行，这条排除纯属多余。

## What Changes

- 从拦截器 `excludePathPatterns` 中移除 `/admin/attachment/**`，上传接口回归拦截器 + `@RequireAuth` 管控。
- 保留 `/upload/**` 静态资源放行（头像、帖子图片需要匿名可读）。
- 上传接口的登录态校验以拦截器为准，`AttachmentController` 内的兜底判空保留。

## Capabilities

### New Capabilities

- `attachment-auth`: 附件上传与访问的鉴权边界——上传必须登录，读取上传结果保持公开。

### Modified Capabilities

（无。）

## Impact

**后端**（`server/bbs-admin`）

- `config/WebMvcConfig.java`：删除一行排除规则，补注释说明为什么不能整体放行。
- `controller/AttachmentController.java`：无需改动（已标 `@RequireAuth` 且有兜底判空）。

**前端**

- 无需改动。前端上传请求本就带 `Authorization` 头，此前因拦截器被排除而未生效。

**风险**

- 若存在依赖匿名上传的调用方（目前没有），改动后会 401。静态资源访问不受影响。
