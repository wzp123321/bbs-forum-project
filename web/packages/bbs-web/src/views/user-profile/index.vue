<template>
  <div class="web-user-profile">
    <div class="wup-main">
      <el-skeleton v-if="loading && !user" :rows="4" animated />

      <el-empty v-else-if="!user" description="用户不存在" />

      <template v-else>
        <!-- 头部信息卡 -->
        <el-card shadow="never" class="wup-header">
          <div class="wup-header-left">
            <el-avatar :size="80" icon="UserFilled" />
            <div class="wup-header-info">
              <div class="wup-name">
                {{ user.userName || user.userId }}
                <el-tag v-if="user.gender === '1'" type="primary" size="small" effect="plain" class="wup-gender">
                  男
                </el-tag>
                <el-tag v-else-if="user.gender === '2'" type="danger" size="small" effect="plain" class="wup-gender">
                  女
                </el-tag>
              </div>
              <div class="wup-id">ID: {{ user.userId }}</div>
              <div class="wup-time">注册于 {{ user.createTime || '-' }}</div>
            </div>
          </div>
          <div class="wup-header-right">
            <el-button
              v-if="!isSelf"
              :type="following ? 'default' : 'primary'"
              :loading="followLoading"
              @click="toggleFollow"
            >
              {{ following ? '已关注' : '关注' }}
            </el-button>
          </div>
        </el-card>

        <!-- 统计 -->
        <el-card shadow="never" class="wup-stats">
          <el-statistic :value="totalPost" label="帖子" />
          <el-statistic :value="counts.followers" label="粉丝" />
          <el-statistic :value="counts.following" label="关注" />
        </el-card>

        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="wup-tabs">
          <el-tab-pane label="TA 的帖子" name="posts">
            <div v-if="loadingPosts" class="wup-loading">
              <el-skeleton :rows="4" animated />
            </div>
            <el-empty v-else-if="!posts.length" description="暂无帖子" />
            <div v-else class="wup-list">
              <article v-for="p in posts" :key="p.id" class="wup-post-item" @click="goPost(p.id)">
                <div class="wup-post-title">{{ p.title }}</div>
                <div class="wup-post-meta">
                  <el-tag v-for="t in p.tagNames" :key="t" size="small" effect="plain">{{ t }}</el-tag>
                  <span class="wup-post-time">{{ p.createTime }}</span>
                </div>
                <div class="wup-post-stat">
                  <span>
                    <el-icon><View /></el-icon>
                    {{ p.viewCount ?? 0 }}
                  </span>
                  <span>
                    <el-icon><ChatDotRound /></el-icon>
                    {{ p.commentCount ?? 0 }}
                  </span>
                  <span>
                    <el-icon><Star /></el-icon>
                    {{ p.likeCount ?? 0 }}
                  </span>
                </div>
              </article>
              <el-pagination
                v-model:current-page="pageNum"
                v-model:page-size="pageSize"
                :page-sizes="pageSizes"
                background
                layout="total, prev, pager, next, jumper"
                :total="total"
                @current-change="loadPosts"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { View, ChatDotRound, Star } from '@element-plus/icons-vue';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { getUserApi, followCountApi } from '@/apis/user';
import { cancelFollowApi, followApi, followStatusApi } from '@/apis';
import { pagePostsApi } from '@/apis/post';
import { isLoggedIn, userStore } from '@/utils';
import type { UserInfoVO, FollowCountVO } from '@/apis/user';
import type { PostVO } from '@/apis/post';

defineOptions({ name: 'WebUserProfile' });

const route = useRoute();
const router = useRouter();
const { pageNum, pageSize, total, setPageNum } = usePagination();
const totalPost = computed(() => total.value);

const user = ref<UserInfoVO | null>(null);
const counts = ref<FollowCountVO>({ followers: 0, following: 0 });
const following = ref(false);
const loading = ref(false);
const followLoading = ref(false);

const posts = ref<PostVO[]>([]);
const loadingPosts = ref(false);
const activeTab = ref('posts');

const myId = computed(() => userStore.getUserId());
const targetUserId = computed(() => String(route.params.userId || ''));
const isSelf = computed(() => !!myId.value && myId.value === targetUserId.value);

const loadUser = async () => {
  if (!targetUserId.value) return;
  loading.value = true;
  try {
    user.value = await getUserApi(targetUserId.value);
  } catch {
    user.value = null;
  } finally {
    loading.value = false;
  }
};

const loadCounts = async () => {
  if (!targetUserId.value) return;
  try {
    counts.value = await followCountApi(targetUserId.value);
  } catch {
    // ignore
  }
};

const loadFollowStatus = async () => {
  if (!targetUserId.value || isSelf.value || !isLoggedIn()) return;
  try {
    const res = await followStatusApi(targetUserId.value);
    following.value = res.following;
  } catch {
    // ignore
  }
};

const loadPosts = async () => {
  if (!targetUserId.value) return;
  loadingPosts.value = true;
  try {
    const res = await pagePostsApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      userId: targetUserId.value,
      status: 1,
    });
    posts.value = res.list;
    total.value = res.total;
  } catch {
    posts.value = [];
  } finally {
    loadingPosts.value = false;
  }
};

const toggleFollow = async () => {
  if (!isLoggedIn()) {
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }
  if (isSelf.value) return;
  followLoading.value = true;
  try {
    if (following.value) {
      await cancelFollowApi(targetUserId.value);
      following.value = false;
      counts.value.followers = Math.max(0, (counts.value.followers ?? 0) - 1);
      ElMessage.success('已取消关注');
    } else {
      await followApi(targetUserId.value);
      following.value = true;
      counts.value.followers = (counts.value.followers ?? 0) + 1;
      ElMessage.success('关注成功');
    }
  } catch {
    // ignore
  } finally {
    followLoading.value = false;
  }
};

const goPost = (id: number) => router.push(`/post/${id}`);

watch(
  () => route.params.userId,
  () => {
    setPageNum(1);
    posts.value = [];
    user.value = null;
    following.value = false;
    loadUser();
    loadCounts();
    loadFollowStatus();
    loadPosts();
  },
);

onMounted(() => {
  loadUser();
  loadCounts();
  loadFollowStatus();
  loadPosts();
});
</script>

<style lang="less" scoped>
.web-user-profile {
  padding: 24px 0;
}

.wup-main {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.wup-header {
  :deep(.el-card__body) {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.wup-header-left {
  display: flex;
  gap: 16px;
  align-items: center;
}

.wup-header-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.wup-name {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.wup-gender {
  font-weight: 400;
}

.wup-id {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.wup-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.wup-stats {
  :deep(.el-card__body) {
    display: flex;
    justify-content: space-around;
  }
}

.wup-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid var(--el-border-color-lighter);
}

.wup-loading {
  padding: 12px;
}

.wup-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.wup-post-item {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  padding: 12px;
  cursor: pointer;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
}

.wup-post-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--el-text-color-primary);
}

.wup-post-meta {
  display: flex;
  gap: 6px;
  align-items: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
}

.wup-post-time {
  margin-left: auto;
}

.wup-post-stat {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--el-text-color-secondary);

  span {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }
}
</style>
