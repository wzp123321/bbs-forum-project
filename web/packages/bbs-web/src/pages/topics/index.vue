<template>
  <div class="web-topics">
    <div class="wt-categories" v-loading="loading">
      <el-tag
        :type="activeId === undefined ? 'primary' : 'info'"
        :effect="activeId === undefined ? 'dark' : 'plain'"
        class="wt-cat"
        @click="handleSelect(undefined)"
      >
        全部
      </el-tag>
      <el-tag
        v-for="c in categories"
        :key="c.id"
        :type="activeId === c.id ? 'primary' : 'info'"
        :effect="activeId === c.id ? 'dark' : 'plain'"
        class="wt-cat"
        @click="handleSelect(c.id)"
      >
        {{ c.name }}
      </el-tag>
    </div>

    <el-empty v-if="!loading && !dataSource.length" description="该板块暂无帖子" />

    <div v-else class="wt-list">
      <article v-for="item in dataSource" :key="item.id" class="wt-item" @click="goDetail(item.id)">
        <div class="wt-item-title">{{ item.title }}</div>
        <div class="wt-item-meta">
          <span>{{ item.userName || item.userId || '匿名' }}</span>
          <span>评论 {{ item.commentCount ?? 0 }}</span>
          <span>赞 {{ item.likeCount ?? 0 }}</span>
          <span>{{ item.createTime }}</span>
        </div>
      </article>
    </div>

    <el-pagination
      class="wt-pagination"
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="fetchList"
      @current-change="fetchList"
    />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { categoryApi } from '@/apis/category';
import { postApi } from '@/apis/post';
import type { CategoryVO } from '@/apis/category';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'WebTopics' });

const route = useRoute();
const router = useRouter();
const { pageNum, pageSize, total, setPageNum } = usePagination();
const categories = ref<CategoryVO[]>([]);
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);
const activeId = ref<number | undefined>(undefined);

const loadCategories = async () => {
  try {
    categories.value = await categoryApi.list();
  } catch {
    // 忽略
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await postApi.page({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: activeId.value,
      status: 1,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const handleSelect = (id: number | undefined) => {
  activeId.value = id;
  setPageNum(1);
  fetchList();
};

const goDetail = (id: number) => {
  router.push(`/post/${id}`);
};

watch(
  () => route.query.categoryId,
  (v) => {
    if (v != null) {
      const n = Number(v);
      if (!Number.isNaN(n)) handleSelect(n);
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await loadCategories();
  await fetchList();
});
</script>

<style lang="less" scoped>
.web-topics {
  padding: 24px 0;

  .wt-categories {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 16px;
  }

  .wt-cat {
    cursor: pointer;
  }

  .wt-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .wt-item {
    background: #fff;
    padding: 12px 16px;
    border-radius: 6px;
    cursor: pointer;
    border: 1px solid var(--el-border-color-lighter);

    &:hover {
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
  }

  .wt-item-title {
    font-weight: 600;
    margin-bottom: 4px;
  }

  .wt-item-meta {
    display: flex;
    gap: 16px;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  .wt-pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
