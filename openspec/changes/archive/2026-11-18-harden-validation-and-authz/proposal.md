# Proposal

## Why

两个遗留项让权限与参数约束形同虚设：

1. `pom.xml` 缺 `spring-boot-starter-validation`，全站 20 余个 DTO 上的 `@NotBlank` / `@Size` / `@NotNull` 注解静默失效，`GlobalExceptionHandler` 里写好的校验异常处理从未被触发。
2. 管理端接口只标 `@RequireAuth`（要求登录），不校验管理员身份。任何登录用户都能删除任意账号、改帖子置顶/精华/状态、处理举报、回复反馈、改板块标签字典——比帖子删除缺归属校验更严重。

## What Changes

- 引入 `spring-boot-starter-validation`，DTO 注解统一生效，异常由 `GlobalExceptionHandler` 转成 `code=400` + 字段中文提示（HTTP 200，前端拦截器无需改动）。
- `@RequireAuth` 增加 `adminOnly` 开关，鉴权拦截器在解析 token 后校验管理员身份。
- 管理端专属接口统一标记 `adminOnly = true`；用户自己的资源操作（帖子/评论删除、点赞、收藏、关注、发帖、评论、草稿）保持仅登录即可。
- 移除 `resetPassword` 里因依赖缺失而写的手动长度兜底。

## Capabilities

### New Capabilities

- `request-validation`: 请求 DTO 字段约束统一生效，非法参数返回结构化业务错误。
- `admin-only-endpoint`: 管理端专属接口要求管理员身份，普通登录用户返回 403。

### Modified Capabilities

（无。）

## Impact

**后端**（`server/bbs-admin`）

- `pom.xml`、`security/annotation/RequireAuth.java`、`security/AuthInterceptor.java`
- 9 个 controller 的指定方法加 `adminOnly = true`；`controller/AuthController.java` 删手动兜底。

**前端**：无需改动。用户端未调用任何被收紧的接口。

**行为变化**

| 场景 | 改动前 | 改动后 |
|---|---|---|
| 参数非法 | 业务兜底或静默放过 | `code=400` + 字段名 + 中文提示 |
| 普通用户调管理接口 | 200，实际执行 | `code=403 需要管理员权限` |
| 普通用户操作自己的资源 | 200 | 不变 |

**风险**：`@Valid` 生效后，此前靠业务兜底的接口会提前返回 `code=400`。已逐一核对前端必填项与 DTO 注解一致，并回归正常发帖、评论、点赞、关注流。
