<template>
  <div class="web-search-result">
    <div class="wsr-main">
      <div class="wsr-banner">
        <h2 class="wsr-title">搜索结果</h2>
        <p class="wsr-sub">关键词: <b>{{ keyword || '无' }}</b>，共 {{ total }} 条结果</p>
      </div>

      <div class="wsr-toolbar">
        <el-input
          v-model="inputKw"
          placeholder="搜索帖子标题/内容"
          clearable
          style="width: 360px"
          @keyup.enter="doSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="doSearch">搜索</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="5" animated />

      <template v-else>
        <el-empty v-if="!dataSource.length" description="没有匹配的结果" />
        <div v-else class="wsr-list">
          <article
            v-for="item in dataSource"
            :key="item.id"
            class="wsr-item"
            @click="goDetail(item.id)"
          >
            <h3 class="wsr-item-title">{{ item.title }}</h3>
            <p class="wsr-item-excerpt" v-html="highlightExcerpt(item.content)"></p>
            <div class="wsr-item-meta">
              <el-tag v-for="t in item.tagNames" :key="t" size="small" effect="plain">{{ t }}</el-tag>
              <span class="wsr-item-user">
                <el-icon><User /></el-icon> {{ item.userName || item.userId || '匿名' }}
              </span>
              <span><el-icon><View /></el-icon> {{ item.viewCount ?? 0 }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ item.commentCount ?? 0 }}</span>
              <span class="wsr-item-time">{{ item.createTime }}</span>
            </div>
          </article>
        </div>

        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="pageSizes"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="doSearch"
          @current-change="fetchList"
        />
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search, User, View, ChatDotRound } from '@element-plus/icons-vue';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { postApi } from '@/apis/post';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'WebSearchResult' });

const route = useRoute();
const router = useRouter();
const { pageNum, pageSize, total, setPageNum } = usePagination();

const inputKw = ref('');
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);

const keyword = ref('');

const fetchList = async () => {
  if (!keyword.value) {
    dataSource.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  try {
    const res = await postApi.page({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      status: 1,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } catch {
    dataSource.value = [];
  } finally {
    loading.value = false;
  }
};

const doSearch = () => {
  const kw = inputKw.value.trim();
  if (!kw) {
    keyword.value = '';
    fetchList();
    return;
  }
  if (kw !== keyword.value) {
    setPageNum(1);
    router.replace({ path: '/search', query: { keyword: kw } });
  } else {
    fetchList();
  }
};

const goDetail = (id: number) => router.push(`/post/${id}`);

const stripHtml = (html: string) => (html || '').replace(/<[^>]*>/g, ' ').replace(/\s+/g, ' ').trim();

const highlightExcerpt = (html?: string) => {
  const text = stripHtml(html ?? '');
  if (!text) return '';
  const max = 160;
  const snippet = text.length > max ? `${text.slice(0, max)}…` : text;
  if (!keyword.value) return escapeHtml(snippet);
  const re = new RegExp(escapeReg(keyword.value), 'gi');
  return escapeHtml(snippet).replace(re, (m) => `<mark>${m}</mark>`);
};

const escapeHtml = (s: string) =>
  s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');

const escapeReg = (s: string) => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');

watch(
  () => route.query.keyword,
  (kw) => {
    const next = typeof kw === 'string' ? kw : '';
    keyword.value = next;
    inputKw.value = next;
    setPageNum(1);
    fetchList();
  },
  { immediate: true },
);

onMounted(() => {
  if (!keyword.value) {
    const kw = typeof route.query.keyword === 'string' ? route.query.keyword : '';
    keyword.value = kw;
    inputKw.value = kw;
  }
});
</script>

<style lang="less" scoped>
.web-search-result {
  padding: 24px 0;
}

.wsr-main {
  max-width: 960px;
  margin: 0 auto;
}

.wsr-banner {
  padding: 24px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
}

.wsr-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.wsr-sub {
  margin: 8px 0 0;
  opacity: 0.9;
}

.wsr-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-lighter);
}

.wsr-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.wsr-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid var(--el-border-color-lighter);

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.wsr-item-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.wsr-item-excerpt {
  margin: 0 0 8px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
  font-size: 13px;

  :deep(mark) {
    background: #fff3a3;
    color: inherit;
    padding: 0 2px;
    border-radius: 2px;
  }
}

.wsr-item-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex-wrap: wrap;
}

.wsr-item-user,
.wsr-item-time {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.wsr-item-time {
  margin-left: auto;
}
</style>
