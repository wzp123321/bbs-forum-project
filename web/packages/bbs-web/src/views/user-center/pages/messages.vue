<template>
  <div class="uc-messages">
    <div class="ucm-head">
      <h3 class="uc-title">我的消息</h3>
      <div class="ucm-actions">
        <el-radio-group v-model="onlyUnread" size="small" @change="onFilterChange">
          <el-radio-button :value="false">全部</el-radio-button>
          <el-radio-button :value="true">只看未读</el-radio-button>
        </el-radio-group>
        <el-button size="small" :disabled="unread === 0" :loading="readAllLoading" @click="onReadAll">
          全部已读
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="ucm-list">
      <el-empty v-if="!loading && list.length === 0" description="暂时没有消息" />

      <div
        v-for="item in list"
        :key="item.id"
        class="ucm-item"
        :class="{ 'is-unread': item.isRead === 0 }"
        @click="onOpen(item)"
      >
        <el-avatar :size="40" :src="resolveFileUrl(item.actorAvatar)" class="ucm-avatar">
          {{ (item.actorName || '?').slice(0, 1) }}
        </el-avatar>
        <div class="ucm-main">
          <div class="ucm-line">
            <span class="ucm-actor">{{ item.actorName || '系统' }}</span>
            <span class="ucm-type">{{ typeSentence(item) }}</span>
            <span v-if="item.postTitle" class="ucm-post">《{{ item.postTitle }}》</span>
          </div>
          <div v-if="item.content" class="ucm-content">{{ item.content }}</div>
          <div class="ucm-time">{{ formatTime(item.createTime) }}</div>
        </div>
        <span v-if="item.isRead === 0" class="ucm-dot" />
      </div>
    </div>

    <div v-if="total > pageSize" class="ucm-pager">
      <el-pagination
        layout="prev, pager, next"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  pageNotificationsApi,
  readAllNotificationsApi,
  readNotificationApi,
  unreadCountApi,
} from '@/apis/notification';
import type { NotificationVO } from '@/apis/notification';
import { resolveFileUrl } from '@/apis/attachment';

defineOptions({ name: 'UcMessages' });

const router = useRouter();

const loading = ref(false);
const readAllLoading = ref(false);
const onlyUnread = ref(false);
const list = ref<NotificationVO[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const unread = ref(0);
/** 通知归属: 评论/回复去帖子详情, 关注去对方主页 */
const TYPE = { LIKE: 1, COMMENT: 2, REPLY: 3, FOLLOW: 4, SYSTEM: 5 } as const;

const typeSentence = (item: NotificationVO) => {
  switch (item.type) {
    case TYPE.LIKE:
      return item.commentId ? '赞了你的评论' : '赞了你的帖子';
    case TYPE.COMMENT:
      return '评论了你的帖子';
    case TYPE.REPLY:
      return '回复了你的评论';
    case TYPE.FOLLOW:
      return '关注了你';
    default:
      return item.typeText || '发来一条消息';
  }
};

const formatTime = (t?: string) => {
  if (!t) return '';
  const d = new Date(t.replace(' ', 'T'));
  if (Number.isNaN(d.getTime())) return t;
  const diff = Date.now() - d.getTime();
  if (diff < 60_000) return '刚刚';
  if (diff < 3_600_000) return `${Math.floor(diff / 60_000)} 分钟前`;
  if (diff < 86_400_000) return `${Math.floor(diff / 3_600_000)} 小时前`;
  return t.slice(0, 16).replace('T', ' ');
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageNotificationsApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      onlyUnread: onlyUnread.value || undefined,
    });
    list.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const fetchUnread = async () => {
  try {
    const res = await unreadCountApi();
    unread.value = res.count;
  } catch {
    // ignore
  }
};

const onFilterChange = () => {
  pageNum.value = 1;
  fetchList();
};

const onPageChange = (p: number) => {
  pageNum.value = p;
  fetchList();
};

const onReadAll = async () => {
  readAllLoading.value = true;
  try {
    await readAllNotificationsApi();
    ElMessage.success('已全部标记为已读');
    await Promise.all([fetchList(), fetchUnread()]);
  } finally {
    readAllLoading.value = false;
  }
};

const onOpen = async (item: NotificationVO) => {
  if (item.isRead === 0) {
    try {
      await readNotificationApi(item.id);
      item.isRead = 1;
      unread.value = Math.max(0, unread.value - 1);
    } catch {
      // 已读失败不阻塞跳转
    }
  }
  if (item.type === TYPE.FOLLOW && item.actorId) {
    router.push(`/user/${item.actorId}`);
    return;
  }
  if (item.postId) {
    router.push(`/post/${item.postId}`);
  }
};

onMounted(async () => {
  await Promise.all([fetchList(), fetchUnread()]);
});
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.ucm-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.ucm-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ucm-list {
  min-height: 200px;
}

.ucm-item {
  position: relative;
  display: flex;
  gap: 12px;
  padding: 14px 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.is-unread .ucm-actor {
    font-weight: 600;
  }
}

.ucm-avatar {
  flex: none;
  background: var(--el-color-primary-light-5);
  color: #fff;
}

.ucm-main {
  flex: 1;
  min-width: 0;
}

.ucm-line {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.ucm-type {
  color: var(--el-text-color-regular);
}

.ucm-post {
  color: var(--el-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 320px;
}

.ucm-content {
  margin-top: 4px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ucm-time {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.ucm-dot {
  position: absolute;
  right: 8px;
  top: 18px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-danger);
}

.ucm-pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
