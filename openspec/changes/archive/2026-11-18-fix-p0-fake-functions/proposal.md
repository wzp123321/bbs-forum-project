# Proposal

## Why

当前站点有多处「显示出来就是错的」行为：帖子详情不累计阅读数，「热门/精华」是点不动的假标签，首页与后台仪表盘的数字写死，字典管理页没有任何接口（纯静态），顶部导航有多条指向不存在路由的死链。这些不是缺功能，是把 bug 当成品交付，会直接误导使用者和后续开发。

## What Changes

- 帖子详情访问时自增阅读数（按用户+时间窗口去重，避免刷新刷量）。
- 帖子分页查询新增排序维度：最新 / 热门 / 精华；首页三个标签接入排序并真实切换。
- 首页 hero 统计条（用户数、板块数、帖子数）改为真实数据。
- 管理后台仪表盘：今日新增按当日发帖量统计，动态列表由真实业务数据生成，去掉硬编码文案。
- 补齐字典与字典类型后端：实体、Mapper、Service、Controller；管理后台两个页面接入真实接口。
- 清理导航死链：移除指向空路径的菜单项；「创作中心」两个入口统一指向现有发帖页。

## Capabilities

### New Capabilities

- `post-browsing`: 帖子浏览侧行为——列表排序（最新/热门/精华）与详情访问的阅读计数规则。
- `site-metrics`: 站点统计数字的对外呈现——用户端首页统计条与后台仪表盘的统计口径。
- `dictionary-management`: 字典类型与字典项的后台维护能力（分页、增删改、启用停用）。
- `site-navigation`: 顶部导航与创作入口的可达性——所有展示给用户的菜单项必须落到存在的页面。

### Modified Capabilities

（无。项目此前没有 OpenSpec 规格，全部按新增能力处理。）

## Impact

**后端**（`server/bbs-admin`）
- `PostController` / `PostPageQuery` / `PostServiceImpl`：新增 `sort` 参数与排序分支；`detail()` 增加阅读数自增。
- 新增 `Dictionary` / `DictionaryType` 实体、Mapper、Service、`DictionaryController`。
- 新增统计查询能力（用户数、板块数、帖子数、今日新增、最近动态），供首页与仪表盘使用。

**数据库**
- `bbs_dictionary` / `bbs_dictionary_type` 已存在于 `schema.sql`，本次只补代码，不改表结构。
- 阅读去重如需持久化，使用内存窗口（Caffeine）即可，不新增表。

**前端**
- `web/packages/bbs-web`：`pages/home/index.vue`（排序 + 统计）、`apis/post`（新增 sort 参数）。
- `web/packages/bbs-admin`：新增 `apis/dictionary`、字典两页对接、`views/home/index.vue` 统计与动态。
- `web/packages/bbs-web/src/layouts/layout-header/model.ts`：菜单项修正。

**风险**
- 阅读数自增会给详情接口增加一次写操作，高频下需注意热点行更新；去重窗口放在应用层，多实例部署时按实例计数，可接受。
