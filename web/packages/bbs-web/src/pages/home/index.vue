<template>
  <div class="web-home">
    <!-- Hero 横幅: 暖珊瑚渐变 -->
    <section class="wh-hero">
      <div class="wh-hero-bg">
        <div class="wh-hero-blob wh-hero-blob-1" />
        <div class="wh-hero-blob wh-hero-blob-2" />
        <div class="wh-hero-blob wh-hero-blob-3" />
      </div>
      <div class="wh-hero-content">
        <div class="wh-hero-badge">
          <span class="wh-hero-dot" />  社区广场
        </div>
        <h1 class="wh-hero-title">9527 论坛</h1>
        <p class="wh-hero-sub">分享技术、记录生活、交流想法 · 发现有趣的人与事</p>
        <div class="wh-hero-search">
          <el-input
            v-model="keyword"
            placeholder="搜索感兴趣的帖子、话题或用户..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" round @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
        <div class="wh-hero-stats">
          <div class="wh-hero-stat">
            <div class="wh-hero-stat-num">{{ total }}</div>
            <div class="wh-hero-stat-label">帖子</div>
          </div>
          <div class="wh-hero-stat-divider" />
          <div class="wh-hero-stat">
            <div class="wh-hero-stat-num">8</div>
            <div class="wh-hero-stat-label">用户</div>
          </div>
          <div class="wh-hero-stat-divider" />
          <div class="wh-hero-stat">
            <div class="wh-hero-stat-num">6</div>
            <div class="wh-hero-stat-label">板块</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 工具栏 -->
    <div class="wh-toolbar">
      <div class="wh-toolbar-tabs">
        <span class="wh-toolbar-tab is-active">最新</span>
        <span class="wh-toolbar-tab">热门</span>
        <span class="wh-toolbar-tab">精华</span>
      </div>
      <el-button round @click="handleReset">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 列表 -->
    <el-skeleton v-if="loading" :rows="6" animated class="wh-skeleton" />

    <template v-else>
      <div v-if="!dataSource.length" class="wh-empty">
        <el-empty description="还没有帖子, 来发布第一个吧~">
          <el-button type="primary" round @click="$router.push('/post/publish')">发布帖子</el-button>
        </el-empty>
      </div>

      <div v-else class="wh-list">
        <article
          v-for="item in dataSource"
          :key="item.id"
          class="wh-item"
          :class="{ 'is-top': item.isTop === 1, 'is-essence': item.isEssence === 1 }"
          @click="goDetail(item.id)"
        >
          <!-- 头像 -->
          <div class="wh-item-avatar" @click.stop="goUser(item.userId)">
            <el-avatar :size="44" :icon="UserFilled" />
          </div>

          <!-- 主体 -->
          <div class="wh-item-main">
            <div class="wh-item-head">
              <div class="wh-item-head-tags">
                <span v-if="item.isTop === 1" class="wh-tag wh-tag--top">置顶</span>
                <span v-if="item.isEssence === 1" class="wh-tag wh-tag--essence">精华</span>
                <span
                  v-if="item.readPerm && item.readPerm !== 1"
                  class="wh-tag wh-tag--lock"
                >
                  <el-icon><Lock /></el-icon>
                  {{ readPermLabel(item.readPerm) }}
                </span>
              </div>
              <h3 class="wh-item-title">{{ item.title }}</h3>
              <div class="wh-item-meta">
                <span v-for="t in item.tagNames" :key="t" class="wh-tag wh-tag--plain">#{{ t }}</span>
              </div>
            </div>

            <div class="wh-item-foot">
              <div class="wh-item-user" @click.stop="goUser(item.userId)">
                <el-icon><User /></el-icon>
                <span>{{ item.userName || '匿名' }}</span>
                <span class="wh-item-cat">{{ item.categoryName || '未分类' }}</span>
              </div>
              <div class="wh-item-stats">
                <span class="wh-item-stat">
                  <el-icon><View /></el-icon>
                  {{ formatNum(item.viewCount) }}
                </span>
                <span class="wh-item-stat">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ formatNum(item.commentCount) }}
                </span>
                <span class="wh-item-stat wh-item-stat--like">
                  <el-icon><Star /></el-icon>
                  {{ formatNum(item.likeCount) }}
                </span>
                <span class="wh-item-time">{{ formatTime(item.createTime) }}</span>
              </div>
            </div>
          </div>
        </article>
      </div>

      <el-pagination
        class="wh-pagination"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </template>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Search, User, UserFilled, View, ChatDotRound, Star, Lock, Refresh } from '@element-plus/icons-vue';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { pagePostsApi, readPermLabel } from '@/apis/post';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'WebHome' });

const router = useRouter();
const { pageNum, pageSize, total, setPageNum } = usePagination();
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);
const keyword = ref('');

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pagePostsApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 1,
      keyword: keyword.value || undefined,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  setPageNum(1);
  fetchList();
};

const handleReset = () => {
  keyword.value = '';
  handleSearch();
};

const goDetail = (id: number) => router.push(`/post/${id}`);
const goUser = (userId?: string) => {
  if (userId) router.push(`/user/${userId}`);
};

const formatNum = (n?: number) => {
  if (!n) return '0';
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w';
  if (n >= 1000) return (n / 1000).toFixed(1) + 'k';
  return String(n);
};

const formatTime = (time?: string) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = (now.getTime() - date.getTime()) / 1000;
  if (diff < 60) return '刚刚';
  if (diff < 3600) return Math.floor(diff / 60) + ' 分钟前';
  if (diff < 86400) return Math.floor(diff / 3600) + ' 小时前';
  if (diff < 86400 * 7) return Math.floor(diff / 86400) + ' 天前';
  return date.toLocaleDateString('zh-CN');
};

onMounted(fetchList);
</script>

<style lang="less" scoped>
.web-home {
  padding: 0 0 48px;

  /* ============ Hero 横幅 ============ */
  .wh-hero {
    position: relative;
    margin-bottom: 32px;
    border-radius: var(--bbs-radius-2xl);
    background: linear-gradient(135deg, #FF6B35 0%, #FF8559 50%, #FFB627 100%);
    overflow: hidden;
    padding: 48px 40px;
    color: #fff;
    box-shadow: 0 16px 40px rgba(255, 107, 53, 0.25);
  }

  .wh-hero-bg {
    position: absolute;
    inset: 0;
    pointer-events: none;
  }

  .wh-hero-blob {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.15);
    filter: blur(40px);
  }

  .wh-hero-blob-1 {
    width: 240px; height: 240px;
    top: -60px; right: -40px;
    background: rgba(255, 214, 153, 0.4);
  }

  .wh-hero-blob-2 {
    width: 180px; height: 180px;
    bottom: -40px; left: 10%;
    background: rgba(255, 182, 39, 0.4);
  }

  .wh-hero-blob-3 {
    width: 120px; height: 120px;
    top: 30%; right: 30%;
    background: rgba(255, 255, 255, 0.2);
  }

  .wh-hero-content {
    position: relative;
    z-index: 1;
  }

  .wh-hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 6px 14px;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(8px);
    border-radius: var(--bbs-radius-full);
    font-size: var(--bbs-font-size-sm);
    font-weight: var(--bbs-font-weight-medium);
    margin-bottom: 16px;
  }

  .wh-hero-dot {
    width: 6px; height: 6px;
    border-radius: 50%;
    background: #fff;
    box-shadow: 0 0 0 4px rgba(255, 255, 255, 0.3);
    animation: pulse 2s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.4; }
  }

  .wh-hero-title {
    font-size: 40px;
    font-weight: var(--bbs-font-weight-bold);
    line-height: 1.2;
    margin: 0 0 12px;
    letter-spacing: -0.5px;
  }

  .wh-hero-sub {
    font-size: var(--bbs-font-size-lg);
    opacity: 0.92;
    margin: 0 0 28px;
  }

  .wh-hero-search {
    display: flex;
    gap: 12px;
    max-width: 560px;
    margin-bottom: 32px;

    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(8px);
      border-radius: var(--bbs-radius-full);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      padding: 4px 16px;
    }
  }

  .wh-hero-stats {
    display: flex;
    align-items: center;
    gap: 32px;
  }

  .wh-hero-stat {
    display: flex;
    align-items: baseline;
    gap: 8px;
  }

  .wh-hero-stat-num {
    font-size: 28px;
    font-weight: var(--bbs-font-weight-bold);
    letter-spacing: -0.5px;
  }

  .wh-hero-stat-label {
    font-size: var(--bbs-font-size-sm);
    opacity: 0.85;
  }

  .wh-hero-stat-divider {
    width: 1px;
    height: 24px;
    background: rgba(255, 255, 255, 0.3);
  }

  /* ============ 工具栏 ============ */
  .wh-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
  }

  .wh-toolbar-tabs {
    display: flex;
    gap: 4px;
    background: var(--bbs-bg-color);
    padding: 4px;
    border-radius: var(--bbs-radius-full);
    box-shadow: var(--bbs-shadow-xs);
  }

  .wh-toolbar-tab {
    padding: 8px 20px;
    border-radius: var(--bbs-radius-full);
    font-size: var(--bbs-font-size-base);
    font-weight: var(--bbs-font-weight-medium);
    color: var(--bbs-text-color-secondary);
    cursor: pointer;
    transition: all var(--bbs-transition-base);

    &:hover {
      color: var(--bbs-color-primary);
    }

    &.is-active {
      background: var(--bbs-gradient-primary);
      color: #fff;
      box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
    }
  }

  /* ============ 列表 ============ */
  .wh-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .wh-skeleton {
    background: var(--bbs-bg-color);
    border-radius: var(--bbs-radius-lg);
    padding: 20px;
  }

  .wh-item {
    display: flex;
    gap: 16px;
    background: var(--bbs-bg-color);
    border-radius: var(--bbs-radius-lg);
    padding: 20px;
    cursor: pointer;
    transition: all var(--bbs-transition-base);
    border: 1px solid var(--bbs-border-color-light);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      left: 0; top: 0; bottom: 0;
      width: 3px;
      background: var(--bbs-color-primary);
      transform: scaleY(0);
      transform-origin: center;
      transition: transform var(--bbs-transition-base);
    }

    &:hover {
      transform: translateY(-2px);
      box-shadow: var(--bbs-shadow-lg);
      border-color: transparent;

      &::before {
        transform: scaleY(1);
      }
    }

    &.is-top {
      background: linear-gradient(135deg, #FFF8F4 0%, #FFFFFF 100%);
      border-color: var(--bbs-color-primary-border);
    }
  }

  .wh-item-avatar {
    flex-shrink: 0;
    border-radius: 50%;
    transition: transform var(--bbs-transition-base);

    &:hover {
      transform: scale(1.05);
    }

    :deep(.el-avatar) {
      background: var(--bbs-gradient-primary);
      color: #fff;
    }
  }

  .wh-item-main {
    flex: 1;
    min-width: 0;
  }

  .wh-item-head {
    margin-bottom: 8px;
  }

  .wh-item-head-tags {
    display: inline-flex;
    gap: 6px;
    margin-bottom: 6px;
  }

  .wh-item-title {
    font-size: var(--bbs-font-size-lg);
    font-weight: var(--bbs-font-weight-semibold);
    color: var(--bbs-text-color-primary);
    line-height: var(--bbs-line-height-snug);
    margin: 0 0 8px;
    transition: color var(--bbs-transition-base);
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .wh-item:hover .wh-item-title {
    color: var(--bbs-color-primary);
  }

  .wh-item-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
  }

  /* ============ 自定义 Tag ============ */
  .wh-tag {
    display: inline-flex;
    align-items: center;
    gap: 3px;
    padding: 2px 10px;
    border-radius: var(--bbs-radius-full);
    font-size: var(--bbs-font-size-xs);
    font-weight: var(--bbs-font-weight-medium);
    line-height: 1.6;
    transition: all var(--bbs-transition-fast);
  }

  .wh-tag--top {
    background: linear-gradient(135deg, #FF6B35 0%, #FF4757 100%);
    color: #fff;
  }

  .wh-tag--essence {
    background: linear-gradient(135deg, #FFB627 0%, #FF8A00 100%);
    color: #fff;
  }

  .wh-tag--lock {
    background: var(--bbs-color-warning-light);
    color: var(--bbs-color-warning-dark);
  }

  .wh-tag--plain {
    background: var(--bbs-color-primary-light);
    color: var(--bbs-color-primary-active);

    &:hover {
      background: var(--bbs-color-primary-border);
    }
  }

  /* ============ 底部 ============ */
  .wh-item-foot {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 12px;
    color: var(--bbs-text-color-secondary);
    font-size: var(--bbs-font-size-sm);
  }

  .wh-item-user {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    transition: color var(--bbs-transition-fast);

    &:hover {
      color: var(--bbs-color-primary);
    }
  }

  .wh-item-cat {
    padding-left: 8px;
    margin-left: 4px;
    border-left: 1px solid var(--bbs-border-color);
    color: var(--bbs-text-color-placeholder);
  }

  .wh-item-stats {
    display: inline-flex;
    align-items: center;
    gap: 16px;
  }

  .wh-item-stat {
    display: inline-flex;
    align-items: center;
    gap: 4px;

    &--like {
      color: var(--bbs-color-primary);
    }
  }

  .wh-item-time {
    color: var(--bbs-text-color-placeholder);
    margin-left: 8px;
  }

  /* ============ 分页 ============ */
  .wh-pagination {
    margin-top: 32px;
    justify-content: center;
  }

  .wh-empty {
    background: var(--bbs-bg-color);
    border-radius: var(--bbs-radius-lg);
    padding: 60px 0;
  }
}
</style>
