# Design

## Context

见 `proposal.md - Why`。当前实现的相关事实：

- `PostServiceImpl.detail()` 只读不写，阅读数恒为 0；`PostPageQuery` 只有 `categoryId / tagId / status / userId / keyword`，无排序参数。
- `PostServiceImpl.pageQueryVO()` 固定 `orderByDesc(isTop, topTime, createTime)`，即只有「最新」一种顺序。
- 首页与后台仪表盘都用「分页接口 pageSize=1 取 total」的方式凑统计数字；首页 hero 的两个数字（用户、板块）直接写死。
- `bbs_dictionary` / `bbs_dictionary_type` 表已存在，字段为 `label / value / type_id / sort / status`；前端两个字典页是按 `name / type / describe` 写的纯静态页，与表结构不匹配，无任何 API 调用。
- 管理员身份来自配置项 `AuthProperties.adminUserIds`，`Authz.assertAdmin()` 已有现成用法。

## Goals / Non-Goals

**Goals:**
- 让「热门 / 精华」成为真实可用的视图，阅读数可信。
- 统计数字只有一个来源，前后端口径一致。
- 字典具备可运维的服务端 CRUD，前端页面按真实表结构重写列。
- 导航不再有死链。

**Non-Goals:**
- 不引入 Redis、Elasticsearch 等中间件（保持单体 + 单库）。
- 不做阅读数的防爬/防刷（只做去重窗口）。
- 不改表结构；字典相关表沿用现有 schema。
- 不做角色权限体系（RBAC 属 P2）。

## Decisions

### D1：热度排序用自定义 SQL，不用 QueryWrapper 拼表达式

MyBatis-Plus 的 `orderByDesc` 只接受字段引用，无法表达 `like_count * 3 + comment_count * 2 + view_count`。用 `.last()` 注入 ORDER BY 有风险：分页自动 count 会走 SQL 解析，行为随版本变化。

**选择**：在 `PostMapper` 增加两个自定义方法——列表查询与计数查询各一个，SQL 里显式写明 ORDER BY 表达式，count 语句单独写（不用 MP 自动 count）。
**备选**：(a) `.last()` 注入 —— count 不可控，放弃；(b) 只按 `like_count, comment_count, view_count` 字典序 —— MP 直接支持，但「1 赞 100 评论」会排在「2 赞」之后，与 spec 的加权口径不符，放弃。

### D2：阅读数用原子自增 + 应用层去重窗口

- 计数写库用 `UPDATE bbs_post SET view_count = view_count + 1 WHERE id = ?`，不用「读出来 +1 再 updateById」——后者在并发下丢计数（点赞计数目前就是这个写法，本次不动它）。
- 去重放 Caffeine：key = `read:{postId}:{subject}`，`subject` 为登录 userId，未登录用来源 IP；TTL 5 分钟，最大条目数设上限并开启按大小淘汰。
- 只在「详情成功返回」之后才计数，权限校验失败提前抛异常，天然不计数。

**代价**：多实例部署时每个实例各自计数，去重变弱。当前单体部署，可接受；`design` 里记这一条风险即可。

### D3：统计接口拆成两个，按敏感度分权

新增 `POST /admin/stat/summary` 与 `POST /admin/stat/overview`：

- `summary`：`userCount`、`postCount`、`categoryCount`，**无需登录**——用户端首页要展示，游客也必须看得到。
- `overview`：在 `summary` 基础上增加 `todayPostCount`、`pendingReportCount`、`recentActivities`，**要求管理员**——待处理举报数属于后台运营数据，不能对游客暴露。

原本设想用一个接口服务两处，但两者的可见范围不同，合并就必然要么泄露后台数据、要么让首页游客看不到数字。拆两个方法、共用同一个 Service，比在返回体里按角色裁剪字段更清楚。

首页统计条与后台仪表盘都调它，替换现有的「pageSize=1 取 total」写法——那写法每多一个数字就多一次分页请求，且分页总数受调用方传参影响，口径不可控。

`recentActivities` 的合并策略：分别取帖子、评论、举报各最近 10 条，按时间倒序归并后截断 10 条。规模小，不做 SQL 级 UNION。

### D4：字典按现有表结构实现，前端重写列

后端按 `bbs_dictionary_type(name, code)` + `bbs_dictionary(type_id, label, value, sort, status)` 建 CRUD，逻辑删除沿用 `deleted` 字段（MP `@TableLogic`）。

前端两个页面现有列（`name / type / describe`）与表结构不匹配，直接改列定义，不做字段映射层——映射层只会让两套命名长期共存。

删除类型时的约束：若该类型下仍有未删除字典项，拒绝删除并返回可读错误（`仍有 N 条字典项`），避免产生孤儿数据。

### D5：导航在渲染层过滤，不改数据结构

`headerLeftMenuList` / `headerRightMenuList` 保留原结构，在 layout 渲染时过滤 `path` 为空的项与有 children 但全部 children 为空的项。这样以后补页面只需填 path，不用改渲染逻辑。

「创作中心」两个入口改为 `/post/publish?type=article` 与 `/post/publish?type=topic`，发布页读 `route.query.type` 预选内容类型。`/about`、`/resources` 当前无页面，按「空路径不渲染」规则自然消失，等有内容再加回。

## Risks / Trade-offs

- [热点行更新] 爆款帖子详情高频自增 `view_count` 会在单行上排队 → 当前量级无影响；若出现热点，改为按分钟批量累加（需要额外缓冲，本次不做）。
- [多实例去重失效] 去重窗口在单实例内存 → 多实例部署时同一用户可能被多个实例各计一次；可接受，记为已知限制。
- [字典前端重写] 现有字典页列与表不符，重写会改变后台页面观感 → 属修 bug，不是设计回退。
- [统计接口多跑几条 SQL] overview 每次合并 3 张表各 10 条 → 数据量小，且可加短 TTL 缓存；本次先不加缓存，保持实现简单。

## Migration Plan

- 无表结构变更、无数据迁移。
- 部署顺序：后端先上（新接口向后兼容，旧前端不受影响），再上前端。
- 回滚：前端回退即可；后端新接口独立，旧接口行为不变（`sort` 缺省等价于原行为，阅读计数是纯增量）。
