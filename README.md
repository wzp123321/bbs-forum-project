# 9527 论坛

> 参考：https://bbs.nanshengbbs.top/

## 一、项目介绍

本项目是一个仿社区论坛系统，参考「男生 BBS」实现论坛核心业务。整个项目采用 monorepo 结构，包含：

| 模块                     | 说明                                    | 路径                                               |
| ------------------------ | --------------------------------------- | -------------------------------------------------- |
| `server/bbs-admin`       | 后端服务（Spring Boot）                 | [server/bbs-admin](./server/bbs-admin)             |
| `web/packages/bbs-admin` | 管理后台前端（Vue3）                    | [web/packages/bbs-admin](./web/packages/bbs-admin) |
| `web/packages/bbs-web`   | 用户端前端（Vue3）                      | [web/packages/bbs-web](./web/packages/bbs-web)     |
| `web/public`             | 公共代码库（`@bbs/core`、`@bbs/utils`） | [web/public](./web/public)                         |
| `app/bbs-mini`           | 移动端小程序（Taro 4 + React）          | [app/bbs-mini](./app/bbs-mini)                     |

## 二、技术栈

### 后端

- Spring Boot 3.3.1 / Java 17
- MyBatis Plus 3.5.5（分页拦截器、`@TableLogic` 逻辑删除）
- Druid 连接池
- Knife4j 接口文档
- JWT 鉴权（`@RequireAuth` + `AuthInterceptor`，`adminOnly=true` 限定管理员），密码 BCrypt 哈希，DTO 参数校验（`spring-boot-starter-validation`）
- Caffeine 本地缓存（`@Cacheable`）

### 前端

- Vue 3 + Composition API + `<script setup>`
- TypeScript
- Vite
- Element Plus
- Vue Router / Pinia
- pnpm workspace

## 三、当前已实现

接口统一前缀 `admin/*`、统一 `R<T>` 响应，除公开接口外均需登录态。

### 后端

- [x] Spring Boot + MyBatis Plus + Druid 基础框架
- [x] Knife4j 接口文档（<http://127.0.0.1:9999/doc.html>）
- [x] 统一响应 `R<T>`、全局异常处理、DTO 参数校验（`code=400` + 字段中文提示）、分页拦截器
- [x] 登录鉴权：注册 / 登录 / 修改密码，JWT + BCrypt；管理端接口仅配置的管理员可调用，普通用户越权返回 403
- [x] 控制器：`auth` `user` `userInfo` `post` `comment` `category` `tag` `like` `collect` `follow` `feedback` `report` `attachment` `dictionary` `dictionaryType` `stat` `notification`
- [x] 帖子列表排序 `sort=latest|hot|essence`；热门按 `like*3 + comment*2 + view` 加权
- [x] 浏览数、点赞数走 SQL 原子自增，避免并发覆盖
- [x] 用户资料扩展 `nickname` / `avatar` / `signature`，展示名统一回退「昵称 → 用户名」
- [x] 字典 / 字典类型全链路，删除类型前校验其下子项
- [x] 站点统计 `POST /admin/stat/summary`（公开，用户端首页）+ `POST /admin/stat/overview`（仅管理员，仪表盘）
- [x] 站内通知 `POST /admin/notification/*`：点赞 / 评论 / 关注三类事件写入，未读数、分页、单条已读、全部已读
- [x] 通知去重：唯一键 `(user_id, actor_id, type, post_id, comment_id)` + `ON DUPLICATE KEY UPDATE`
- [x] Caffeine 本地缓存（板块、标签下列表 60s 过期）
- [x] 文件上传（本地存储 + 扩展名 / MIME 校验），上传接口要求登录，`/upload/**` 静态资源匿名可读
- [x] 关注动态流 `POST /admin/post/followFeed`：只取被关注作者、排除自己、时间倒序；未关注任何人返回空页
- [x] 草稿箱：`status = 4` 表示草稿，`saveDraft` / `myDrafts` / `draftDetail` / `publishDraft` 四个接口；草稿不进任何公开列表，他人读取返回 404
- [x] 找回密码：`forgotPassword`（用户名 + 绑定邮箱/手机换一次性令牌，账号不存在与信息不匹配返回同一文案）+ `resetPassword`（令牌 10 分钟、单次有效）

### 管理后台（`bbs-admin`）

- [x] 整体布局（顶栏 + 左侧菜单 + 主内容区）
- [x] 登录页（表单校验）+ 路由守卫 + 退出登录
- [x] 仪表盘：接 `stat/overview`，用户数 / 帖子数 / 评论数 / 活跃度图表
- [x] 用户管理（表格 + 分页 + 详情抽屉，列表含头像与昵称）
- [x] 字典管理、字典类型管理（接真实接口）
- [x] 反馈管理（含处理弹窗）
- [x] 帖子管理（含富文本编辑器）
- [x] 404 页面

### 用户端（`bbs-web`）

- [x] 顶部导航布局（菜单 + 搜索 + 创作中心入口 + 消息铃铛 + 头像菜单）
- [x] 首页 feed 流：关注 / 最新 / 热门 / 精华 tab（「关注」仅登录可见），侧栏站点统计走真实接口
- [x] 帖子详情（正文 + 评论 + 楼中楼回复 + 点赞 / 收藏 / 分享）
- [x] 发帖 / 编辑帖（富文本工具栏 + 标签选择 + 板块分类）
- [x] 话题页、标签页、搜索结果页
- [x] 用户中心：我的帖子 / 回复 / 收藏 / 关注、修改密码、编辑资料
- [x] 消息中心（`/user-center/messages`）：互动通知列表、未读筛选、单条 / 全部已读；铃铛未读数 60s 轮询
- [x] 草稿箱（`/user-center/drafts`）：发帖页「存草稿」，草稿列表支持继续编辑 / 发布 / 删除
- [x] 找回密码：登录页「忘记密码」两步弹窗（校验用户名 + 绑定邮箱或手机 → 设置新密码）
- [x] 其他用户主页（公开访问，资料 / 帖子 / 关注 / 粉丝 / 关注按钮）
- [x] 移动端适配

### 公共库

- [x] `usePagination` / `useDialogOpen` 等通用 hooks
- [x] axios 拦截器（401 登录态失效处理）
- [x] `v-inputFilter` / `v-drag` 自定义指令
- [x] sessionStorage 工具
- [x] 公共 CSS 变量与字体（DINPro）

## 四、功能清单（按阶段）

### 阶段 1：基础打通

- [x] **后端 1.1**：补充建表 SQL（帖子、评论、标签、反馈、字典、点赞、收藏、关注等）
- [x] **后端 1.2**：登录鉴权（JWT）+ 统一异常处理 + 参数校验
- [x] **后端 1.3**：用户模块 CRUD + 注册 + 修改密码 / 资料
- [x] **前端 1.4**：前端 axios 封装 + 真实接口请求（替换所有 mock 数据）
- [x] **前端 1.5**：管理后台登录对接后端 + 路由守卫 + 退出登录
- [x] **前端 1.6**：管理后台用户管理接入真实接口

### 阶段 2：论坛核心

- [x] **后端 2.1**：标签 / 板块模块
- [x] **后端 2.2**：帖子模块（发布、编辑、删除、列表、详情、置顶、加精）
- [x] **后端 2.3**：评论模块（楼层、楼中楼、点赞）
- [x] **后端 2.4**：点赞 / 收藏 / 关注
- [x] **前端 2.5**：用户端首页（feed 流 + 热门 + 最新 + 精华）
- [x] **前端 2.6**：用户端帖子详情页（正文 + 评论 + 回复输入框）
- [x] **前端 2.7**：用户端发帖 / 编辑帖（富文本工具栏 + 标签选择 + 分类）
- [x] **前端 2.8**：用户端标签页、话题页

### 阶段 3：管理后台完善

- [x] **前端 3.1**：帖子管理接入真实接口
- [x] **前端 3.2**：反馈管理接入真实接口
- [x] **前端 3.3**：字典 / 字典类型管理接入真实接口（含增删改查、启停用、选项下发）
- [x] **前端 3.4**：Tag 管理页补全
- [x] **前端 3.5**：仪表盘统计（用户数、帖子数、评论数、活跃度图表，数据源 `stat/overview`）

### 阶段 4：体验增强

- [x] **后端 4.1**：文件上传 / 图片处理（本地存储 + 扩展名 / MIME 校验 + 附件与帖子自动绑定）
- [x] **后端 4.2**：搜索（帖子 / 用户）
- [x] **后端 4.3**：消息通知（站内信：点赞 / 评论 / 关注三类，唯一键去重）
- [x] **前端 4.4**：用户中心（我的帖子、回复、收藏、关注、修改密码、编辑资料）
- [x] **前端 4.5**：注册 / 找回密码
- [x] **前端 4.6**：搜索结果页
- [x] **前端 4.7**：移动端适配
- [x] **前端 4.8**：其他用户主页（公开访问，TA 的资料 / 帖子 / 关注 / 粉丝 / 关注按钮）
- [x] **全栈 4.9**：关注动态流（首页「关注」tab，只含被关注者的正常帖子）
- [x] **全栈 4.10**：草稿箱（`status = 4`，保存 / 列出 / 编辑 / 发布 / 删除）
- [x] **全栈 4.11**：找回密码（用户名 + 绑定邮箱/手机换一次性令牌，10 分钟单次有效）

### 阶段 5：高级特性

- [x] 内容审核（图片 + 文本 — 本地敏感词过滤，命中后自动转「审核中」，由管理员复审）
- [x] 分享（Web 端 + 移动端 Web Share API，复制链接 / 微博 / QQ）
- [x] 实时聊天（WebSocket 公共聊天室，含敏感词过滤、最近 50 条历史消息、断线重连、未读红点）
- [x] 视频帖（B 站 / YouTube / Vimeo / .mp4 直链，16:9 自适应高度）
- [x] 阅读权限（公开 / 登录可见 / 粉丝可见 / 仅作者）
- [x] 举报功能（帖子 / 评论举报 + 管理员处理 + 被举报内容下架）
- [x] 流程引擎（论坛用不到，不实现）

### 阶段 6：性能与部署

- [x] **后端 6.1**：本地缓存（Caffeine + Spring Cache，板块 / 标签启用下列表 60s 过期）
- [x] **后端 6.2**：SQL 索引补充（`bbs_post` 加 `(status, create_time)` 复合索引优化 feed 排序）
- [x] **前端 6.3**：路由懒加载修复 + 富文本图片懒加载（`useLazyImages` + `v-lazy` 指令，IntersectionObserver 视口前 200px 触发）
- [x] **部署 6.4**：Docker 多阶段构建（后端 Maven → JRE / 前端 pnpm → nginx）+ docker-compose 一键编排（MySQL + 后端 + 前端）+ nginx 反代（`/api/*` → `/admin/*`、`/ws/*` WebSocket 透传、`/upload/*` 静态资源）+ GitHub Actions CI + GitLab CI 模板
- [x] **移动端 6.5**：Taro 4 + React + TypeScript 跨端小程序（`app/bbs-mini/`），支持微信 / 抖音 / 支付宝 / H5

## 五、数据库表

建表脚本：`server/bbs-admin/src/main/resources/sql/schema.sql`，使用 `CREATE TABLE IF NOT EXISTS`，可重复执行。

| 表名                 | 说明                                              |
| -------------------- | ------------------------------------------------- |
| `user_info`          | 用户（含 `nickname` / `avatar` / `signature`）     |
| `bbs_category`       | 板块 / 分类                                       |
| `bbs_tag`            | 标签                                              |
| `bbs_post`           | 帖子（`status`：1正常 0已删 2审核中 3审核不通过 4草稿） |
| `bbs_post_tag`       | 帖子-标签关联                                     |
| `bbs_comment`        | 评论（父评论实现楼中楼）                          |
| `bbs_like`           | 点赞（帖子 / 评论通用，取消时物理删除）           |
| `bbs_collect`        | 收藏                                              |
| `bbs_follow`         | 关注                                              |
| `bbs_feedback`       | 反馈                                              |
| `bbs_report`         | 举报                                              |
| `bbs_attachment`     | 附件                                              |
| `bbs_dictionary_type` | 字典类型                                         |
| `bbs_dictionary`      | 字典数据                                         |
| `bbs_notification`   | 互动通知（点赞 / 评论 / 关注）                    |
| `bbs_operation_log`  | 操作日志（已建表，接口待开发）                    |

## 六、本地启动

```bash
# 1. 初始化数据库（MySQL 8，库名 bbs）
mysql -uroot -p bbs < server/bbs-admin/src/main/resources/sql/schema.sql

# 2. 后端（默认 9999）
cd server/bbs-admin
./mvnw spring-boot:run

# 3. 前端（统一在 web 目录）
cd web
pnpm install
pnpm dev:bbs-admin   # 管理后台
pnpm dev:bbs-web     # 用户端
```

接口文档：<http://127.0.0.1:9999/doc.html>

## 七、Docker 一键部署

```bash
# 1. 复制环境变量样例
cp .env.example .env
# 2. 修改 .env 中 MYSQL_ROOT_PASSWORD / BBS_JWT_SECRET
# 3. 一键启动 (首次会构建镜像 + 初始化 MySQL)
docker compose up -d --build

# 常用命令
docker compose ps
docker compose logs -f bbs-admin
docker compose down                 # 停止
docker compose down -v              # 停止并清理数据卷
```

访问入口：

- 用户端：<http://localhost/>
- 管理后台：<http://localhost/admin> （需登录 `/login`）
- 接口文档：<http://localhost/api/doc.html>
- WebSocket 聊天：<ws://localhost/ws/chat>

> 前端 ajax 走 `/api/*`，nginx rewrite 到后端 `/admin/*`；开发模式直接同源请求 `/admin/*`，参见 `web/nginx.conf`。

## 八、移动端小程序（`app/bbs-mini/`）

基于 Taro 4 + React 18 + TypeScript，支持微信 / 抖音 / 支付宝 / H5 多端。

```bash
cd app/bbs-mini
pnpm install

# 微信小程序 (需要微信开发者工具导入 dist 目录)
pnpm dev:weapp

# H5 端预览
pnpm dev:h5

# 生产构建
pnpm build:weapp
```

**目录结构**

```
src/
├── app.config.ts      # 全局配置: 4 tabBar + 6 二级页
├── pages/             # 页面
│   ├── home/          # tabBar - 首页 feed 流
│   ├── topic/         # tabBar - 分类网格 + 热门标签
│   ├── message/       # tabBar - 互动消息
│   ├── mine/          # tabBar - 我的
│   ├── post-detail/   # 帖子详情 + 评论
│   ├── post-publish/  # 发布
│   ├── login/         # 登录
│   ├── user-home/     # 用户主页
│   ├── search/        # 搜索 (历史 + 热门)
│   └── topic-list/    # 分类/标签筛选列表
├── components/        # PostCard, Empty
├── services/api.ts    # API + mock 切换 (USE_MOCK)
├── store/user.ts      # Zustand 登录态
├── data/              # mock 数据
└── types/model.ts     # 类型定义 (与 web 端 bbs-web 对齐)
```

**切换真实接口**：编辑 `src/services/api.ts` 顶部 `const USE_MOCK = false`，axios 自动走 `/api/*` → nginx 反代到后端 `/admin/*`。

## 九、开发约定

- 提交规范：feat / fix / docs / refactor / style / test / chore
- 前端遵循：`<script setup>` + TElement + `usePagination` / `useDialogOpen` + `apis/<feature>/` 目录
- 后端遵循：Controller / Service / Mapper 三层 + `R<T>` 统一响应，接口一律 `POST` + `admin/*` 前缀
- 命名规范：Vue 文件用 `kebab-case`，类 / 接口用 `PascalCase`，方法 / 变量用 `camelCase`

## 十、需求与规格（OpenSpec）

需求与规格用 OpenSpec 管理，代码改动先对齐规格。

- 生效规格：`openspec/specs/`（`post-browsing`、`site-metrics`、`dictionary-management`、`site-navigation`、`user-profile`、`notification`、`attachment-auth`、`follow-feed`、`draft-box`、`password-reset`、`request-validation`、`admin-only-endpoint`）
- 进行中变更：`openspec/changes/`；已完成变更归档至 `openspec/changes/archive/`
- 常用命令：

```bash
openspec list                          # 查看进行中变更
openspec validate <change> --strict    # 校验变更规格
openspec archive <change> --yes        # 归档已完成变更
```

## 十一、工程化能力

| 方向 | 落地位置 | 运行方式 |
| --- | --- | --- |
| 自动化测试 | `server/bbs-admin/src/test/`（JUnit5 + Mockito）、`web/**/*.test.ts`（Vitest）、`web/e2e/`（Playwright） | `mvn -B test`；`pnpm test` / `pnpm test:coverage`；`pnpm test:e2e` |
| CI 门禁 | `.github/workflows/ci.yml` | 后端测试、前端单测+覆盖率、E2E 冒烟、AI 评测、镜像构建全部串入流水线 |
| 可观测性 | `RequestIdFilter` + Actuator `/actuator/{health,metrics,prometheus}` + 前端 `core/monitor.ts` | 请求头 `X-Request-Id` 贯穿日志；前端配置 `VITE_MONITOR_ENDPOINT` 后上报错误与性能 |
| 配置外置 | `application.yml` 与 `.env.example` | DB / JWT / Druid / Knife4j / Redis / ES / Actuator 均可由环境变量覆盖 |
| Redis 缓存 | `RedisCacheConfig`，`CACHE_TYPE=redis` 启用 | 默认仍为 Caffeine；切到 Redis 后 `bbs.cache.ttl-seconds` 控制 TTL |
| ES 搜索 | `EsPostSearchClient`，`SEARCH_ENGINE=elasticsearch` 启用 | 默认 SQL LIKE；启用后 ES 无结果或不可用时自动回退。首次需调 `POST /admin/search/reindex`（管理员）建索引 |
| AI 评测 | `eval/` | `cd eval && npm run eval`；真实模型用 `npm run eval:http` + `EVAL_TARGET_URL`。详见 `eval/README.md` |

可选中间件通过 compose profile 启动，不影响默认一键部署：

```bash
docker compose --profile cache up -d   # 附带 Redis
docker compose --profile search up -d  # 附带 Elasticsearch
```

需要本地 MySQL 的上下文冒烟测试默认跳过；设置 `RUN_DB_TESTS=true` 后执行 `mvn test` 才会运行。
