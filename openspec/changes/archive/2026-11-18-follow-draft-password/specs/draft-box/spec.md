# Spec Delta

## Purpose

定义草稿在帖子表里的状态语义，以及草稿的保存、列举、编辑、发布与删除规则，保证未完成的内容既不会丢失也不会提前曝光。

## ADDED Requirements

### Requirement: 草稿状态语义

`bbs_post.status` SHALL 增加取值 `4 = 草稿`。

- 草稿 MUST NOT 出现在任何公开列表（首页、话题、标签、搜索、用户主页、关注流）中。
- 草稿 MUST NOT 计入站点统计与用户帖子数。
- 草稿的点赞、评论、收藏、浏览计数 MUST 保持 0 且不增长。

#### Scenario: 草稿不出现在首页

- **WHEN** 某用户保存了一篇草稿
- **THEN** 首页任何 tab 都不出现该帖，站点统计帖子数不变

### Requirement: 保存草稿

`POST /admin/post/saveDraft` SHALL 支持新建与更新草稿，要求登录态。

- 新建时不要求标题与正文同时非空，但二者 MUST 至少有一项非空。
- 更新时 MUST 校验目标帖属于当前用户且状态为草稿。
- 保存草稿 MUST NOT 触发敏感词转审核逻辑（草稿尚未发布）。

#### Scenario: 只写标题也能存

- **WHEN** 用户只填标题、正文为空，点击「存草稿」
- **THEN** 草稿保存成功，返回草稿 ID

#### Scenario: 标题与正文都为空

- **WHEN** 用户标题与正文都没填就点「存草稿」
- **THEN** 返回参数错误，不创建草稿

#### Scenario: 不能改别人的草稿

- **WHEN** A 调用 `saveDraft` 传入 B 的草稿 ID
- **THEN** 返回 403 或 404，B 的草稿内容不变

### Requirement: 我的草稿列表

`POST /admin/post/myDrafts` SHALL 返回当前用户的草稿分页，按更新时间倒序。

- 列表项 MUST 至少包含：ID、标题、正文摘要、更新时间。
- 仅返回当前用户自己的草稿，MUST NOT 因参数而返回他人草稿。

#### Scenario: 列表按更新时间倒序

- **WHEN** 用户有 3 篇草稿，最近更新的是第 2 篇
- **THEN** 列表首条为该篇

#### Scenario: 没有草稿

- **WHEN** 用户没有草稿
- **THEN** 返回空列表与总数 0

### Requirement: 草稿详情与编辑

`POST /admin/post/draftDetail` SHALL 允许作者读取自己的草稿完整内容；非作者访问 MUST 返回 404。

- 公开详情接口 `POST /admin/post/detail` 对 `status != 1` 的帖子 MUST 继续返回 404（含草稿）。

#### Scenario: 作者打开草稿继续编辑

- **WHEN** 作者带上自己的 token 请求草稿详情
- **THEN** 返回完整标题、正文、板块、标签，供编辑页回填

#### Scenario: 他人拿到草稿 ID

- **WHEN** B 请求 A 的草稿详情
- **THEN** 返回 404

### Requirement: 发布草稿

`POST /admin/post/{id}/publishDraft` SHALL 把草稿转为已发布帖，要求作者本人操作。

- 发布 MUST 走与正常发帖一致的敏感词检查：命中则状态转为 `2`（审核中），未命中为 `1`（正常）。
- 发布前 MUST 满足发帖约束：标题非空、正文非空、板块有效。
- 发布成功后该帖 MUST 立即出现在公开列表与被关注者的关注流中。

#### Scenario: 正常发布

- **WHEN** 作者对一篇标题与正文齐全的草稿点击发布
- **THEN** 状态变为 1，首页可见

#### Scenario: 只有标题的草稿发布失败

- **WHEN** 正文为空的草稿被发布
- **THEN** 返回参数错误，状态仍为草稿

#### Scenario: 命中敏感词

- **WHEN** 草稿正文包含敏感词后发布
- **THEN** 状态变为 2，仅作者与管理员可见

### Requirement: 删除草稿

草稿 SHALL 可被作者删除，删除走与帖子一致的删除入口。

- 删除后 MUST NOT 出现在「我的草稿」列表。
- 非作者删除 MUST 被拒绝。

#### Scenario: 删除自己的草稿

- **WHEN** 作者删除一篇草稿
- **THEN** 草稿从列表中消失，其它草稿不受影响
