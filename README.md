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

## 二、技术栈

### 后端

- Spring Boot 3.3.1
- Java 17
- MyBatis Plus 3.5.5
- Druid 连接池
- Knife4j（接口文档）

### 前端

- Vue 3 + Composition API + `<script setup>`
- TypeScript
- Vite
- Element Plus
- Vue Router / Pinia
- pnpm workspace

## 三、当前已实现

### 后端

- [x] Spring Boot + MyBatis Plus + Druid 基础框架
- [x] Knife4j 接口文档（[http://127.0.0.1:8888/doc.html](http://127.0.0.1:8888/doc.html)）
- [x] MyBatis Plus 分页拦截器
- [x] 统一响应封装 `R<T>`
- [x] MyBatis Plus 代码生成器工具
- [x] 实体 `user_info` + 单查询接口 `GET /admin/userInfo/selectUserInfo/{userId}`

### 管理后台（`bbs-admin`）

- [x] 整体布局（顶栏 + 左侧菜单 + 主内容区）
- [x] 登录页 UI（含表单校验、密码加密）
- [x] 仪表盘骨架（空容器）
- [x] 用户管理页面 UI（表格 + 分页 + 详情抽屉）
- [x] 字典管理 UI
- [x] 字典类型管理 UI
- [x] 反馈管理 UI（含处理弹窗）
- [x] 帖子管理 UI（含富文本编辑器）
- [x] 404 页面

### 用户端（`bbs-web`）

- [x] 顶部导航布局（菜单 + 搜索 + 创作中心入口）
- [x] 首页 / 话题 / 标签 三个页面占位

### 公共库

- [x] `usePagination` / `useDialogOpen` 等通用 hooks
- [x] axios 拦截器（401 登录态失效处理）
- [x] `v-inputFilter` / `v-drag` 自定义指令
- [x] sessionStorage 工具
- [x] 公共 CSS 变量与字体（DINPro）

## 四、待实现功能（按推荐开发顺序）

> 说明：以下功能均未实现。勾选 `[ ]` 表示待办，`[x]` 表示已完成。

### 阶段 1：基础打通

- [x] **后端 1.1**：补充建表 SQL（帖子、评论、标签、反馈、字典、点赞、收藏、关注等）
- [x] **后端 1.2**：登录鉴权（JWT / Sa-Token）+ 统一异常处理 + 参数校验
- [x] **后端 1.3**：用户模块 CRUD + 注册 + 修改密码 / 资料
- [x] **前端 1.4**：前端 axios 封装 + 真实接口请求（替换所有 mock 数据）
- [x] **前端 1.5**：管理后台登录对接后端 + 路由守卫 + 退出登录
- [x] **前端 1.6**：管理后台用户管理接入真实接口

### 阶段 2：论坛核心

- [x] **后端 2.1**：标签 / 板块模块
- [x] **后端 2.2**：帖子模块（发布、编辑、删除、列表、详情、置顶、加精）
- [x] **后端 2.3**：评论模块（楼层、楼中楼、点赞）
- [x] **后端 2.4**：点赞 / 收藏 / 关注
- [x] **前端 2.5**：用户端首页（feed 流 + 热门 + 最新）
- [x] **前端 2.6**：用户端帖子详情页（正文 + 评论 + 回复输入框）
- [x] **前端 2.7**：用户端发帖 / 编辑帖（富文本工具栏 + 标签选择 + 分类）
- [x] **前端 2.8**：用户端标签页、话题页

### 阶段 3：管理后台完善

- [x] **前端 3.1**：帖子管理接入真实接口
- [x] **前端 3.2**：反馈管理接入真实接口
- [x] **前端 3.3**：字典 / 字典类型管理接入真实接口
- [x] **前端 3.4**：Tag 管理页补全
- [x] **前端 3.5**：仪表盘统计（用户数、帖子数、评论数、活跃度图表）

### 阶段 4：体验增强

- [x] **后端 4.1**：文件上传 / 图片处理（本地存储 + 扩展名/MIME 校验 + 附件与帖子自动绑定）
- [x] **后端 4.2**：搜索（帖子 / 用户）
- [x] **后端 4.3**：消息通知（站内信 / 系统消息）
- [x] **前端 4.4**：用户中心（我的帖子、回复、收藏、关注、修改密码、编辑资料）
- [x] **前端 4.5**：注册 / 找回密码
- [x] **前端 4.6**：搜索结果页
- [x] **前端 4.7**：移动端适配
- [x] **前端 4.8**：其他用户主页（公开访问，TA 的资料/帖子/关注/粉丝/关注按钮）

### 阶段 5：高级特性（可选）

- [x] 内容审核（图片 + 文本 — 本地敏感词过滤, 命中后自动转"审核中", 由管理员复审）
- [x] 分享（Web 端 + 移动端 Web Share API，复制链接 / 微博 / QQ）
- [x] 实时聊天（WebSocket 公共聊天室，含敏感词过滤、最近 50 条历史消息、断线重连、未读红点）
- [x] 流程引擎（论坛用不到，暂不实现）
- [x] 视频帖（B 站 / YouTube / Vimeo / .mp4 直链，16:9 自适应高度）
- [x] 阅读权限（公开 / 登录可见 / 粉丝可见 / 仅作者）
- [x] 举报功能（帖子/评论举报 + 管理员处理 + 被举报内容下架）

### 阶段 6：性能优化

- [x] **后端 6.1**：本地缓存（Caffeine + Spring Cache，板块/标签启用下列表 60s 过期）
- [x] **后端 6.2**：SQL 索引补充（`post_info` 加 `(status, create_time)` 复合索引优化 feed 排序）
- [x] **前端 6.3**：路由懒加载修复（`system.ts` 三个页面改为异步 import）+ 富文本图片懒加载（`useLazyImages` + `v-lazy` 指令，IntersectionObserver 视口前 200px 触发）
- [x] **部署 6.4**：Docker 多阶段构建（后端 Maven → JRE / 前端 pnpm → nginx）+ docker-compose 一键编排（MySQL + 后端 + 前端）+ nginx 反代（`/api/*` → `/admin/*`、`/ws/*` WebSocket 透传、`/upload/*` 静态资源）+ GitHub Actions CI + GitLab CI 模板
- [x] **移动端 6.5**：Taro 4 + React + TypeScript 跨端小程序（`app/bbs-mini/`），支持微信/抖音/支付宝/H5，4 个 tabBar（首页/话题/消息/我的）+ 6 个二级页（帖子详情/发布/登录/用户主页/搜索/话题列表），全部页面使用 mock 数据演示，切换 `USE_MOCK=false` 即可对接 web 端 `/api` 接口
- [x] **阶段 6 全部完成** ✅

## 五、数据库规划（草案）

> 实际建表以迭代过程为准。

| 表名              | 说明                                           |
| ----------------- | ---------------------------------------------- |
| `user_info`       | 用户基础信息（已存在）                         |
| `post_info`       | 帖子表                                         |
| `post_content`    | 帖子正文（与 `post_info` 拆分，便于搜索/审核） |
| `comment_info`    | 评论表（支持父评论实现楼中楼）                 |
| `tag_info`        | 标签表                                         |
| `post_tag`        | 帖子-标签关联                                  |
| `category_info`   | 板块 / 分类                                    |
| `like_record`     | 点赞记录（帖子 / 评论通用）                    |
| `collect_record`  | 收藏记录                                       |
| `follow_record`   | 关注记录                                       |
| `feedback_info`   | 反馈表                                         |
| `dictionary`      | 字典数据                                       |
| `dictionary_type` | 字典类型                                       |

## 六、本地启动

```bash
# 后端
cd server/bbs-admin
./mvnw spring-boot:run

# 前端（统一在 web 目录）
cd web
pnpm install
pnpm dev:bbs-admin   # 管理后台
pnpm dev:bbs-web     # 用户端
```

接口文档：<http://127.0.0.1:8888/doc.html>

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
- 接口文档：<http://localhost/api/doc.html>（如已配置 knife4j）
- WebSocket 聊天：<ws://localhost/ws/chat>

> 前端 ajax 走 `/api/*`，nginx rewrite 到后端 `/admin/*`，与开发模式 `/admin/*` 直接同源不同，参见 `web/nginx.conf`。

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
- 后端遵循：Controller / Service / Mapper 三层 + `R<T>` 统一响应
- 命名规范：Vue 文件用 `kebab-case`，类 / 接口用 `PascalCase`，方法 / 变量用 `camelCase`
