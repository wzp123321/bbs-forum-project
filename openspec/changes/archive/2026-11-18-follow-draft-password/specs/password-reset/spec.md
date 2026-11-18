# Spec Delta

## Purpose

定义忘记密码的两步流程：身份校验换一次性令牌、凭令牌重置密码，并明确令牌的生命周期与安全边界。

## ADDED Requirements

### Requirement: 身份校验与令牌签发

`POST /admin/auth/forgotPassword` SHALL 接收用户名与绑定联系方式（邮箱或手机），校验通过后签发一次性重置令牌。

- 用户名不存在、或联系方式与账号绑定的邮箱/手机不一致时，MUST 返回同一条通用错误文案（`账号或绑定信息不匹配`），MUST NOT 暴露账号是否存在。
- 校验成功 MUST 返回 `resetToken` 与有效期，有效期 MUST NOT 超过 10 分钟。
- 同一账号 60 秒内重复申请 MUST 复用同一令牌，MUST NOT 无限签发。
- 联系方式比对 MUST 忽略首尾空格；邮箱比对 MUST 忽略大小写。

#### Scenario: 用户名与邮箱匹配

- **WHEN** 提交存在的用户名 + 该账号绑定的邮箱
- **THEN** 返回 resetToken 与剩余有效期

#### Scenario: 邮箱不匹配

- **WHEN** 提交存在的用户名 + 别的邮箱
- **THEN** 返回「账号或绑定信息不匹配」，不签发令牌

#### Scenario: 用户不存在

- **WHEN** 提交不存在的用户名
- **THEN** 返回与上一条完全相同的文案，不泄露账号是否存在

#### Scenario: 短时间内重复申请

- **WHEN** 同一账号 30 秒内第二次申请
- **THEN** 返回同一 resetToken，有效期不叠加

### Requirement: 凭令牌重置密码

`POST /admin/auth/resetPassword` SHALL 校验令牌后更新密码。

- 令牌不存在、已使用或已过期时 MUST 返回「重置链接已失效，请重新申请」。
- 新密码 MUST 满足长度约束（6–32 位）。
- 重置成功 MUST 立即作废该令牌，并清除该账号的登录失败锁定计数。
- 重置后 MUST 能用新密码登录，旧密码 MUST 立即失效。

#### Scenario: 正常重置

- **WHEN** 用有效令牌提交符合长度要求的新密码
- **THEN** 返回成功，新密码可登录，旧密码登录失败

#### Scenario: 令牌重复使用

- **WHEN** 同一个 resetToken 第二次提交
- **THEN** 返回失效提示，密码不再变化

#### Scenario: 令牌过期

- **WHEN** 令牌签发超过 10 分钟后提交
- **THEN** 返回失效提示

### Requirement: 限频

两个接口 SHALL 受访问频率限制，防止令牌爆破与账号探测。

- 单个 IP 每小时申请次数 MUST 有上限，超限返回「操作过于频繁，请稍后再试」。
- `resetPassword` 的失败尝试 MUST 计入限频。

#### Scenario: 高频探测

- **WHEN** 同一 IP 一小时内反复提交不同的用户名 + 联系方式组合
- **THEN** 超过上限后请求被拒绝
