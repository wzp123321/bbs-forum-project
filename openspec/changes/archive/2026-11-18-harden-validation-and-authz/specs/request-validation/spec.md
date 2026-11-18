# Spec Delta

## Purpose

请求 DTO 字段约束统一生效，非法参数返回可展示的结构化业务错误。

## ADDED Requirements

### Requirement: DTO 字段约束必须生效

所有标注 `@Valid @RequestBody` 的接口 SHALL 执行 DTO 上的 jakarta validation 注解。校验失败时 `GlobalExceptionHandler` MUST 返回 HTTP 200、`code=400`，消息包含字段名与注解定义的中文提示。

- 项目 MUST 引入 `spring-boot-starter-validation`。
- 校验异常的返回结构 MUST 保持 `R` 协议，前端拦截器无需针对 HTTP 400 做特殊处理。
- 业务代码中因依赖缺失而写的手动长度兜底 MUST 移除，避免同一规则两处定义。

#### Scenario: 空登录请求

- **WHEN** 向 `/admin/auth/login` 提交空 JSON
- **THEN** 返回 `code=400`，消息为 `account 账号不能为空;password 密码不能为空`

#### Scenario: 非法发帖参数

- **WHEN** 登录用户提交 `title` 为空的发帖请求
- **THEN** 返回 `code=400`，消息为 `title 标题不能为空`，不创建帖子

#### Scenario: 合法参数不受影响

- **WHEN** 提交字段齐全的发帖、评论请求
- **THEN** 返回 `code=200`，业务正常执行
