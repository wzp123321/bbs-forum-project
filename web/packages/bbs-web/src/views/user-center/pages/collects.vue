<template>
  <div class="uc-list">
    <h3 class="uc-title">我的收藏</h3>
    <el-empty v-if="!loading && !dataSource.length" description="还没有收藏" />
    <div v-else v-loading="loading" class="uc-items">
      <article v-for="p in dataSource" :key="p.id" class="uc-item" @click="goDetail(p.id)">
        <div class="uc-item-title">{{ p.title }}</div>
        <div class="uc-item-meta">
          <span>作者 · {{ p.userName || p.userId }}</span>
          <span>评论 {{ p.commentCount ?? 0 }}</span>
          <span>{{ p.createTime }}</span>
        </div>
      </article>
    </div>
    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="pageNum"
      :page-size="pageSize"
      background
      layout="total, prev, pager, next"
      :total="total"
      @current-change="fetchList"
    />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { usePagination } from '@bbs/core';
import { pageMyCollectedApi, PostVO } from '@/apis';

defineOptions({ name: 'UcCollects' });

const router = useRouter();
const { pageNum, pageSize, total } = usePagination();
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageMyCollectedApi({ pageNum: pageNum.value, pageSize: pageSize.value });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const goDetail = (id: number) => router.push(`/post/${id}`);

onMounted(fetchList);
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.uc-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.uc-item {
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

.uc-item-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.uc-item-meta {
  display: flex;
  gap: 16px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
