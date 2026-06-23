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

- [ ] **后端 1.1**：补充建表 SQL（帖子、评论、标签、反馈、字典、点赞、收藏、关注等）
- [ ] **后端 1.2**：登录鉴权（JWT / Sa-Token）+ 统一异常处理 + 参数校验
- [ ] **后端 1.3**：用户模块 CRUD + 注册 + 修改密码 / 资料
- [ ] **前端 1.4**：前端 axios 封装 + 真实接口请求（替换所有 mock 数据）
- [ ] **前端 1.5**：管理后台登录对接后端 + 路由守卫 + 退出登录
- [ ] **前端 1.6**：管理后台用户管理接入真实接口

### 阶段 2：论坛核心

- [ ] **后端 2.1**：标签 / 板块模块
- [ ] **后端 2.2**：帖子模块（发布、编辑、删除、列表、详情、置顶、加精）
- [ ] **后端 2.3**：评论模块（楼层、楼中楼、点赞）
- [ ] **后端 2.4**：点赞 / 收藏 / 关注
- [ ] **前端 2.5**：用户端首页（feed 流 + 热门 + 最新）
- [ ] **前端 2.6**：用户端帖子详情页（正文 + 评论 + 回复输入框）
- [ ] **前端 2.7**：用户端发帖 / 编辑帖（富文本 + 标签选择 + 分类）
- [ ] **前端 2.8**：用户端标签页、话题页

### 阶段 3：管理后台完善

- [ ] **前端 3.1**：帖子管理接入真实接口（修 `posts-manage.vue` 当前错乱的字典内容）
- [ ] **前端 3.2**：反馈管理接入真实接口
- [ ] **前端 3.3**：字典 / 字典类型管理接入真实接口
- [ ] **前端 3.4**：Tag 管理页补全
- [ ] **前端 3.5**：仪表盘统计（用户数、帖子数、评论数、活跃度图表）

### 阶段 4：体验增强

- [ ] **后端 4.1**：文件上传 / 图片处理（OSS / 本地存储 + 水印 + 压缩）
- [ ] **后端 4.2**：搜索（帖子 / 用户）
- [ ] **后端 4.3**：消息通知（站内信 / 系统消息）
- [ ] **前端 4.4**：用户中心（我的帖子、回复、收藏、关注）
- [ ] **前端 4.5**：注册 / 找回密码
- [ ] **前端 4.6**：搜索结果页
- [ ] **前端 4.7**：移动端适配

### 阶段 5：高级特性（可选）

- [ ] 内容审核（图片 + 文本）
- [ ] 分享（Web 端 + 移动端）
- [ ] 实时聊天（WebSocket）
- [ ] 流程引擎
- [ ] 视频帖（iframe / video 链接 + 自适应高度）
- [ ] 阅读权限
- [ ] 举报功能

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

## 七、开发约定

- 提交规范：feat / fix / docs / refactor / style / test / chore
- 前端遵循：`<script setup>` + TElement + `usePagination` / `useDialogOpen` + `apis/<feature>/` 目录
- 后端遵循：Controller / Service / Mapper 三层 + `R<T>` 统一响应
- 命名规范：Vue 文件用 `kebab-case`，类 / 接口用 `PascalCase`，方法 / 变量用 `camelCase`
