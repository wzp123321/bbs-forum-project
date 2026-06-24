# =============================================================
#  前端 Dockerfile (multi-stage)
#  - builder  : pnpm 装依赖 + 构建 bbs-admin / bbs-web 两个端
#  - runtime  : nginx 托管 dist, 反代 /admin /api /ws 到后端
# =============================================================

# ===== 构建阶段 =====
FROM node:20-alpine AS builder
WORKDIR /build

# 启用 pnpm
RUN corepack enable && corepack prepare pnpm@9.0.0 --activate

# 复制 workspace 描述文件, 先装依赖 (缓存)
COPY package.json pnpm-workspace.yaml* .npmrc* ./
COPY public/package.json ./public/
COPY packages/bbs-admin/package.json ./packages/bbs-admin/
COPY packages/bbs-web/package.json ./packages/bbs-web/
RUN pnpm install --frozen-lockfile || pnpm install

# 复制源码
COPY public ./public
COPY packages ./packages

# 构建
ENV VITE_NODE_ENV=production
RUN pnpm web:build && pnpm admin:build

# ===== 运行阶段 =====
FROM nginx:1.27-alpine
WORKDIR /app

# 复制构建产物
COPY --from=builder /build/packages/bbs-web/dist /app/web
COPY --from=builder /build/packages/bbs-admin/dist /app/admin

# 复制 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://127.0.0.1/ >/dev/null 2>&1 || exit 1

CMD ["nginx", "-g", "daemon off;"]
