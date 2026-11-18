<template>
  <div class="uc-drafts">
    <div class="uc-head">
      <h3 class="uc-title">我的草稿</h3>
      <el-button type="primary" round size="small" @click="goPublish()">写新帖</el-button>
    </div>

    <el-empty
      v-if="!loading && !dataSource.length"
      description="草稿箱是空的, 写一半的内容可以先存这里"
    />

    <div v-else v-loading="loading" class="uc-draft-list">
      <article v-for="d in dataSource" :key="d.id" class="uc-draft-item">
        <div class="uc-draft-main" @click="goPublish(d.id)">
          <div class="uc-draft-title">{{ d.title || '(无标题草稿)' }}</div>
          <div class="uc-draft-summary">{{ summaryOf(d) }}</div>
          <div class="uc-draft-meta">最近更新 {{ d.updateTime || d.createTime }}</div>
        </div>
        <div class="uc-draft-actions">
          <el-button size="small" @click="goPublish(d.id)">继续编辑</el-button>
          <el-button
            size="small"
            type="primary"
            :loading="publishingId === d.id"
            @click="handlePublish(d.id)"
          >
            发布
          </el-button>
          <el-button size="small" type="danger" plain @click="handleDelete(d)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination } from '@bbs/core';
import { deletePostApi, myDraftsApi, publishDraftApi } from '@/apis/post';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'UcDrafts' });

const router = useRouter();
const { pageNum, pageSize, total } = usePagination();
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);
const publishingId = ref<number>();

/** 正文是富文本, 列表里只展示纯文本摘要 */
const stripHtml = (html?: string) =>
  (html || '')
    .replace(/<[^>]+>/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();

const summaryOf = (d: PostVO) => {
  const text = stripHtml(d.content);
  if (!text) return '还没有正文';
  return text.length > 80 ? `${text.slice(0, 80)}...` : text;
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await myDraftsApi({ pageNum: pageNum.value, pageSize: pageSize.value });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const goPublish = (draftId?: number) => {
  router.push(draftId ? `/post/publish?draftId=${draftId}` : '/post/publish');
};

const handlePublish = async (id: number) => {
  publishingId.value = id;
  try {
    const res = await publishDraftApi(id);
    ElMessage.success(res.needAudit ? '已提交审核' : '发布成功');
    fetchList();
  } finally {
    publishingId.value = undefined;
  }
};

const handleDelete = async (d: PostVO) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复, 确定删除这篇草稿?', '删除草稿', { type: 'warning' });
  } catch {
    return;
  }
  await deletePostApi(d.id);
  ElMessage.success('已删除');
  fetchList();
};

onMounted(fetchList);
</script>

<style lang="less" scoped>
.uc-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.uc-draft-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.uc-draft-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
}

.uc-draft-main {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.uc-draft-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.uc-draft-summary {
  color: var(--el-text-color-regular);
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.uc-draft-meta {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.uc-draft-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
}
</style>
