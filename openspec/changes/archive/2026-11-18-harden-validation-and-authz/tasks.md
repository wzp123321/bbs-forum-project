# Tasks

## 1. 参数校验

- [x] 1.1 `pom.xml` 引入 `spring-boot-starter-validation`
- [x] 1.2 `AuthController.resetPassword` 删除手动长度兜底，改用 DTO `@Size`
- [x] 1.3 实测空登录体、空标题发帖、超长板块名、短密码重置均返回 `code=400` + 中文字段提示
- [x] 1.4 实测合法发帖、评论返回 200，前端两个包 `vue-tsc` 通过

## 2. 管理员权限

- [x] 2.1 `RequireAuth` 增加 `adminOnly` 属性，默认 false
- [x] 2.2 `AuthInterceptor` 解析 token 后按 `AdminUserIds` 校验，非管理员返回 403（未登录仍 401）
- [x] 2.3 9 个 controller 的管理端方法标记 `adminOnly = true`（清单见 design.md）
- [x] 2.4 实测普通用户调用删用户、改帖状态、改评论、查举报、查用户、建板块、建字典类型均 403
- [x] 2.5 实测管理员查用户 200，普通用户访问公开 feed 与关注流 200，作者删自己帖子/评论 200、删他人帖子 403

## 3. 收尾

- [x] 3.1 `mvn compile` 通过，后端重启后完成上述接口回归
- [x] 3.2 归档变更并同步 README
