<template>
  <div class="uc-posts">
    <h3 class="uc-title">我的帖子</h3>
    <el-empty v-if="!loading && !dataSource.length" description="你还没有发过帖" />
    <div v-else v-loading="loading" class="uc-post-list">
      <article v-for="p in dataSource" :key="p.id" class="uc-post-item" @click="goDetail(p.id)">
        <div class="uc-post-title">{{ p.title }}</div>
        <div class="uc-post-meta">
          <span>评论 {{ p.commentCount ?? 0 }}</span>
          <span>赞 {{ p.likeCount ?? 0 }}</span>
          <span>收藏 {{ p.collectCount ?? 0 }}</span>
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
import { myPostsApi } from '@/apis/user';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'UcPosts' });

const router = useRouter();
const { pageNum, pageSize, total } = usePagination();
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await myPostsApi({ pageNum: pageNum.value, pageSize: pageSize.value });
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

.uc-post-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.uc-post-item {
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);
  cursor: pointer;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

.uc-post-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.uc-post-meta {
  display: flex;
  gap: 16px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
