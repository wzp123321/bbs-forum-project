# follow-feed Specification

## Purpose
定义「关注动态流」的取数范围、排序和可见性，确保用户关注之后能在一个固定入口看到被关注者的新帖。

## Requirements

### Requirement: 关注动态流的取数范围

`POST /admin/post/followFeed` SHALL 返回当前登录用户所关注的人发布的帖子，按发布时间倒序分页。

- 结果集 MUST 只包含 `status = 1`（正常）且未删除的帖子。
- 结果集 MUST NOT 包含当前用户自己发的帖子。
- 当前用户未关注任何人时 MUST 返回空列表与总数 0，MUST NOT 报错。

#### Scenario: 关注后能看到对方新帖

- **WHEN** A 关注了 B，B 随后发布一篇正常帖子
- **THEN** A 的「关注」流首条为该帖

#### Scenario: 未关注任何人

- **WHEN** A 没有任何关注关系
- **THEN** 接口返回 `list: []`、`total: 0`

#### Scenario: 草稿与审核中的帖子不出现

- **WHEN** B 有一篇草稿和一篇审核中的帖子
- **THEN** A 的「关注」流中都不出现

### Requirement: 关注动态流要求登录

接口 SHALL 要求登录态，未登录访问 MUST 返回 401。

- 取数范围 MUST NOT 由请求参数指定他人，只按当前登录用户的关系计算。

#### Scenario: 游客访问

- **WHEN** 未携带 token 调用 `/admin/post/followFeed`
- **THEN** 返回 401

### Requirement: 关注动态流入口

用户端首页 SHALL 提供「关注」tab，与「最新 / 热门 / 精华」并列。

- 未登录用户 MUST NOT 看到该 tab。
- 已登录但无关注时，tab 内 MUST 展示空态提示并引导去发现内容，MUST NOT 展示静态示例数据。

#### Scenario: 游客不显示关注 tab

- **WHEN** 游客打开首页
- **THEN** tab 栏只有最新 / 热门 / 精华

#### Scenario: 登录后切到关注 tab

- **WHEN** 已登录用户点击「关注」
- **THEN** 列表切换为关注动态流，与其它 tab 的分页状态互不干扰
