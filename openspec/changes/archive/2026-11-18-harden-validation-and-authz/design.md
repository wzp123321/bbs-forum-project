# Design

## 决策

| 决策 | 选择 | 理由 |
|---|---|---|
| 参数校验落地位置 | DTO 注解 + 既有 `GlobalExceptionHandler` | 处理器早已写好，只是缺少 validator 实现；补依赖即可，不新增重复逻辑 |
| 管理员校验位置 | 鉴权拦截器 + `@RequireAuth(adminOnly)` | 一处统一收口，避免 9 个 controller 各自注入 `Authz` 漏改 |
| 校验失败返回码 | HTTP 200 + `code=400` | 与项目 `R` 协议一致，前端 `unwrap` 已按业务码弹提示；若返回 HTTP 400，axios 会 reject 且丢失中文消息 |
| 管理接口判定 | 按调用方而非路径前缀 | 用户端也走 `/admin/**` 路由（如 `/admin/post/page` feed），不能按前缀一刀切 |

## 收紧的接口清单

| Controller | 接口 |
|---|---|
| PostController | 切换置顶、切换精华、修改状态 |
| CommentController | 编辑评论、修改评论状态 |
| UserController | 分页查询用户、删除用户 |
| CategoryController | 分页查询板块、板块详情、新增/编辑/删除板块 |
| TagController | 分页查询标签、标签详情、新增/编辑/删除标签 |
| DictionaryController | 分页查询字典项、新增/编辑/删除字典项、启用/停用 |
| DictionaryTypeController | 分页查询字典类型、新增/编辑/删除字典类型 |
| ReportController | 分页查询举报、举报详情、处理举报、删除举报 |
| FeedbackController | 分页查询反馈、反馈详情、回复反馈、删除反馈 |

保留原有归属校验、不加 `adminOnly` 的接口：`post/delete`、`comment/delete`（作者本人或管理员），以及发帖、评论、点赞、收藏、关注、草稿、动态流。

## 验证口径

- `mvn compile`、两个前端包 `vue-tsc --noEmit` 通过。
- 实测空参数、超长字段、短密码均返回 `code=400` + 中文提示；合法发帖/评论返回 200。
- 实测普通用户调用 8 类管理接口均 403，管理员与公开 feed、关注流均 200。
