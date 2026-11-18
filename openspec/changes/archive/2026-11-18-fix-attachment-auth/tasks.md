# Tasks

## 1. 修复

- [x] 1.1 `WebMvcConfig.excludePathPatterns` 移除 `/admin/attachment/**`，仅保留 `upload.url-prefix + "**"` 静态资源放行。

## 2. 验证

- [x] 2.1 重启后端，未带 token 上传返回 401，上传目录无新文件。
- [x] 2.2 带 token 上传返回 200，`bbs_attachment` 新增记录且 `user_id` 为当前登录用户。
- [x] 2.3 游客直接访问 `/upload/**` 下的图片返回 200。
