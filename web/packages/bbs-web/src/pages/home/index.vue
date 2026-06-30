<template>
  <div class="web-home">
    <div class="wh-main">
      <div class="wh-banner">
        <div class="wh-banner-title">9527 论坛</div>
        <div class="wh-banner-sub">分享技术、记录生活、交流想法</div>
      </div>

      <div class="wh-toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索帖子标题"
          clearable
          style="width: 280px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="6" animated />

      <template v-else>
        <div v-if="!dataSource.length" class="wh-empty">
          <el-empty description="暂无帖子" />
        </div>
        <div v-else class="wh-list">
          <article v-for="item in dataSource" :key="item.id" class="wh-item" @click="goDetail(item.id)">
            <div class="wh-item-head">
              <el-tag v-if="item.isTop === 1" type="danger" size="small" effect="dark">置顶</el-tag>
              <el-tag v-if="item.isEssence === 1" type="warning" size="small" effect="dark">精华</el-tag>
              <span class="wh-item-title">{{ item.title }}</span>
              <el-tag
                v-if="item.readPerm && item.readPerm !== 1"
                :type="readPermTagType(item.readPerm)"
                size="small"
                effect="plain"
              >
                <el-icon><Lock /></el-icon>
                {{ readPermLabel(item.readPerm) }}
              </el-tag>
            </div>
            <div class="wh-item-meta">
              <el-tag v-for="t in item.tagNames" :key="t" size="small" effect="plain" class="wh-tag">{{ t }}</el-tag>
              <span class="wh-item-cat">板块 · {{ item.categoryName || '-' }}</span>
            </div>
            <div class="wh-item-foot">
              <span class="wh-item-user" @click.stop="goUser(item.userId)">
                <el-icon><User /></el-icon>
                {{ item.userName || item.userId || '匿名' }}
              </span>
              <span>
                <el-icon><View /></el-icon>
                {{ item.viewCount ?? 0 }}
              </span>
              <span>
                <el-icon><ChatDotRound /></el-icon>
                {{ item.commentCount ?? 0 }}
              </span>
              <span>
                <el-icon><Star /></el-icon>
                {{ item.likeCount ?? 0 }}
              </span>
              <span class="wh-item-time">{{ item.createTime }}</span>
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
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Search, User, View, ChatDotRound, Star } from '@element-plus/icons-vue';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { pagePostsApi, readPermTagType, readPermLabel } from '@/apis/post';
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
      status: 1, // 只看正常的
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

const goDetail = (id: number) => {
  router.push(`/post/${id}`);
};

const goUser = (userId?: string) => {
  if (userId) router.push(`/user/${userId}`);
};

onMounted(fetchList);
</script>

<style lang="less" scoped>
.web-home {
  padding: 24px 0;

  .wh-main {
    width: 100%;
  }

  .wh-banner {
    background: linear-gradient(135deg, #409eff, #67c23a);
    color: #fff;
    padding: 32px 24px;
    border-radius: 8px;
    margin-bottom: 16px;
  }

  .wh-banner-title {
    font-size: 24px;
    font-weight: 700;
  }

  .wh-banner-sub {
    margin-top: 8px;
    opacity: 0.85;
  }

  .wh-toolbar {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
  }

  .wh-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .wh-item {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.2s;
    border: 1px solid var(--el-border-color-lighter);

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      transform: translateY(-1px);
    }
  }

  .wh-item-head {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .wh-item-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .wh-item-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;
  }

  .wh-item-cat {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  .wh-tag {
    margin-right: 4px;
  }

  .wh-item-foot {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-top: 12px;
    color: var(--el-text-color-secondary);
    font-size: 13px;

    span {
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
  }

  .wh-item-time {
    margin-left: auto;
  }

  .wh-pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
