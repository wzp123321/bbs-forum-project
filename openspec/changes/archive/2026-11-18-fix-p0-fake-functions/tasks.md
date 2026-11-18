# Tasks

## 1. 后端：帖子排序与阅读计数

- [x] {d} `PostPageQuery` 增加 `sort` 字段（`latest` 默认 / `hot` / `essence`），非法值在服务层归一为 `latest`。验证：传 `sort=xxx` 请求正常返回且顺序与不传一致。
- [x] {d} `PostMapper` 增加自定义分页查询与计数方法，SQL 中显式 ORDER BY：置顶优先，`hot` 用 `like_count*3 + comment_count*2 + view_count` 降序、`essence` 追加精华过滤。验证：`mvnw -q compile` 通过，且 `sort=essence` 返回结果全部 `isEssence=1`。
- [x] {d} `PostServiceImpl.pageQueryVO()` 按 `sort` 走不同查询分支，保留原有分类/标签/关键字/作者过滤。验证：首页传 `sort=hot` 时同一批数据顺序与 `latest` 不同且点赞多的在前。
- [x] {d} `PostServiceImpl.detail()` 在权限校验通过后原子自增 `view_count`（`UPDATE ... SET view_count = view_count + 1`），并用 Caffeine 5 分钟窗口按 `postId + (userId|IP)` 去重。验证：连续三次请求同一帖子，阅读数只 +1；无权限请求后阅读数不变。

## 2. 后端：统计接口

- [x] {d} 新增 `StatController`，提供 `POST /admin/stat/overview`，返回 `userCount / postCount / categoryCount / todayPostCount / pendingReportCount / recentActivities`。验证：用 Knife4j 或 curl 调用，字段齐全且类型正确。
- [x] {d} `recentActivities` 由帖子、评论、举报各取最近 10 条按时间归并截断为 10 条，元素含类型、摘要、时间。验证：插入一条新帖后重新请求，该帖出现在列表首位。
- [x] {d} 统计口径固定为 `status=1` 且未删除；`todayPostCount` 按服务器本地当日 0 点起算。验证：造一条昨日帖子与一条今日帖子，前者不计入今日新增。

## 3. 后端：字典模块

- [x] {d} 新增 `Dictionary` / `DictionaryType` 实体与 Mapper（`@TableLogic` 映射 `deleted`），字段与现有表结构一致。验证：`mvnw -q compile` 通过。
- [x] {d} 新增 `DictionaryService` / `DictionaryTypeService`：类型分页、增删改（名称唯一校验）；字典项分页（按类型过滤 + 名称模糊）、增删改、启停。验证：新增同名类型返回业务错误；删除后列表不再出现。
- [x] {d} 新增 `DictionaryController`（`admin/dictionary`、`admin/dictionaryType`）：读接口需登录，写接口 `@RequireAuth` + `Authz.assertAdmin()`。验证：普通用户调用新增接口返回 403；管理员调用成功落库。
- [x] {d} 删除字典类型时校验其下是否仍有未删除字典项，有则拒绝并返回可读错误。验证：对有关联项的类型执行删除，返回错误且数据保留。

## 4. 前端：用户端首页与导航

- [x] 4.1 `apis/post` 的分页请求支持 `sort` 参数。验证：请求体中可见 `sort` 字段。
- [x] 4.2 首页三个标签接入 `sort`，切换时重新拉取并高亮当前项。验证：点击「热门 / 精华」列表内容发生对应变化，「最新」可切回。
- [x] 4.3 首页 hero 统计条改用统计接口真实数据，取数失败显示占位符。验证：数字与后台用户管理总数一致；断网时显示 `-` 而非 0。
- [x] 4.4 导航渲染时过滤空 `path` 项；「创作中心」两个入口指向 `/post/publish`（数据模型无文章/话题区分，不做类型预选）。验证：顶部不再出现「关于 / 资源」死链；点击「写文章」进入发布页。

## 5. 前端：管理后台字典与仪表盘

- [x] 5.1 新增 `apis/dictionary`，覆盖类型与字典项的全部接口。验证：网络面板可见真实请求，无 mock。
- [x] 5.2 字典管理页按真实表结构重写列（`label / value / sort / status / type_id`），新增/编辑抽屉对接接口。验证：新增一条字典项后刷新列表可见。
- [x] 5.3 字典类型管理页同样对接接口，删除有关联项的类型时展示后端错误文案。验证：删除被引用类型时出现「仍有 N 条字典项」提示。
- [x] 5.4 仪表盘统计改调 `stat/overview`，`todayPostCount` 替换写死的 0；动态列表渲染真实事件，无事件显示空态。验证：清空业务数据后动态区显示空态，不出现「系统初始化完成」。

## 6. 联调验证

- [x] 6.1 后端 `mvn compile` 通过；前端两个包 `ts-check` 与 `vite build` 通过。验证：构建退出码 0。顺带修掉两个既有打包错误（asmcrypto 默认导出、Safe 图标不存在），并删除仅被死代码引用的 utils/token.ts 与 service/interceptor。
- [x] 6.2 端到端走查（MySQL + 真实接口）。走查发现并修复 3 个真实缺陷：post/page 分页总数恒为 0、bbs_like 缺 deleted 列导致点赞 500、点赞计数读改写改原子 SQL。
