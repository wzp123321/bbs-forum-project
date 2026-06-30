<template>
  <div class="web-tags" v-loading="loading">
    <el-tag
      v-for="t in tags"
      :key="t.id"
      :type="activeId === t.id ? 'primary' : 'info'"
      :effect="activeId === t.id ? 'dark' : 'plain'"
      class="wt-tag"
      @click="toggleTagFilter(t.id)"
    >
      # {{ t.name }}
      <span v-if="t.postCount != null">·{{ t.postCount }}</span>
    </el-tag>
  </div>

  <div v-if="!loading && !dataSource.length" style="margin-top: 32px">
    <el-empty description="该标签暂无帖子" />
  </div>

  <div v-else class="wt-list">
    <article v-for="item in dataSource" :key="item.id" class="wt-item" @click="goDetail(item.id)">
      <div class="wt-item-title">{{ item.title }}</div>
      <div class="wt-item-meta">
        <span>{{ item.userName || item.userId || '匿名' }}</span>
        <span>评论 {{ item.commentCount ?? 0 }}</span>
        <span>{{ item.createTime }}</span>
      </div>
    </article>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { listTagsApi } from '@/apis/tag';
import { pagePostsApi } from '@/apis/post';
import type { TagVO } from '@/apis/tag';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'WebTags' });

const router = useRouter();
const tags = ref<TagVO[]>([]);
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);
const activeId = ref<number | undefined>(undefined);

const loadTags = async () => {
  try {
    tags.value = await listTagsApi();
  } catch {
    // 忽略
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pagePostsApi({
      pageNum: 1,
      pageSize: 50,
      tagId: activeId.value,
      status: 1,
    });
    dataSource.value = res.list;
  } finally {
    loading.value = false;
  }
};

const toggleTagFilter = (id: number) => {
  activeId.value = activeId.value === id ? undefined : id;
  fetchList();
};

const goDetail = (id: number) => {
  router.push(`/post/${id}`);
};

onMounted(async () => {
  await loadTags();
  await fetchList();
});
</script>

<style lang="less" scoped>
.web-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.wt-tag {
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
</style>
