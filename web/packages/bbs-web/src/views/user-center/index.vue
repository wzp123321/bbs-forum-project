<template>
  <div class="web-user-center">
    <div class="wuc-layout">
      <aside class="wuc-side">
        <div class="wuc-user" @click="navigateToOverview">
          <el-avatar :size="64" :src="resolveFileUrl(user?.avatar)" class="wuc-avatar">
            {{ (user?.nickname || user?.userName || '?').slice(0, 1) }}
          </el-avatar>
          <div class="wuc-user-info">
            <div class="wuc-user-name">{{ user?.nickname || user?.userName || userStore.getUserName() }}</div>
            <div class="wuc-user-sign">{{ user?.signature || `ID: ${userStore.getUserId()}` }}</div>
          </div>
        </div>
        <el-menu :default-active="activeMenu" class="wuc-menu" @select="navigateToMenu">
          <el-menu-item index="overview">个人概览</el-menu-item>
          <el-menu-item index="posts">我的帖子</el-menu-item>
          <el-menu-item index="drafts">我的草稿</el-menu-item>
          <el-menu-item index="collects">我的收藏</el-menu-item>
          <el-menu-item index="likes">我赞过的</el-menu-item>
          <el-menu-item index="messages">
            我的消息
            <el-badge v-if="unread > 0" :value="unread" :max="99" class="wuc-badge" />
          </el-menu-item>
          <el-menu-item index="followers">我的粉丝 ({{ counts.followers }})</el-menu-item>
          <el-menu-item index="following">我的关注 ({{ counts.following }})</el-menu-item>
          <el-menu-item index="edit">编辑资料</el-menu-item>
          <el-menu-item index="password">修改密码</el-menu-item>
        </el-menu>
      </aside>
      <section class="wuc-content">
        <router-view v-slot="{ Component }">
          <component :is="Component" :user="user" :counts="counts" @refresh-counts="loadCounts" />
        </router-view>
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, provide, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getUserApi, followCountApi } from '@/apis/user';
import { unreadCountApi } from '@/apis/notification';
import { resolveFileUrl } from '@/apis/attachment';
import { userStore } from '@/utils';
import type { UserInfoVO, FollowCountVO } from '@/apis/user';

defineOptions({ name: 'WebUserCenter' });

const route = useRoute();
const router = useRouter();
const user = ref<UserInfoVO | null>(null);
const counts = ref<FollowCountVO>({ followers: 0, following: 0 });
const unread = ref(0);

const activeMenu = computed(() => {
  const p = route.path.replace('/user-center', '').replace(/^\//, '') || 'overview';
  return p || 'overview';
});

const loadUser = async () => {
  const myId = userStore.getUserId();
  if (!myId) return;
  try {
    user.value = await getUserApi(myId);
  } catch {
    ElMessage.error('获取用户信息失败');
  }
};

const loadCounts = async () => {
  const myId = userStore.getUserId();
  if (!myId) return;
  try {
    counts.value = await followCountApi(myId);
  } catch {
    // ignore
  }
};

const navigateToMenu = (key: string) => {
  const map: Record<string, string> = {
    overview: '/user-center',
    posts: '/user-center/posts',
    drafts: '/user-center/drafts',
    collects: '/user-center/collects',
    likes: '/user-center/likes',
    messages: '/user-center/messages',
    followers: '/user-center/followers',
    following: '/user-center/following',
    edit: '/user-center/edit',
    password: '/user-center/password',
  };
  router.push(map[key] || '/user-center');
};

const loadUnread = async () => {
  try {
    const res = await unreadCountApi();
    unread.value = res.count;
  } catch {
    // ignore
  }
};

const navigateToOverview = () => router.push('/user-center');

// 供子组件刷新
provide('refreshUserCenter', loadUser);
provide('refreshCounts', loadCounts);

onMounted(async () => {
  await Promise.all([loadUser(), loadCounts(), loadUnread()]);
});
</script>

<style lang="less" scoped>
.web-user-center {
  padding: 24px 0;
}

.wuc-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 16px;
}

.wuc-side {
  background: #fff;
  border-radius: 8px;
  padding: 20px 12px;
  border: 1px solid var(--el-border-color-lighter);
  height: fit-content;
}

.wuc-user {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 8px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 8px;
  cursor: pointer;
}

.wuc-user-name {
  font-size: 16px;
  font-weight: 600;
}

.wuc-user-sign {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 150px;
}

.wuc-avatar {
  flex: none;
  background: var(--el-color-primary);
  color: #fff;
  font-size: 24px;
}

.wuc-badge {
  margin-left: 8px;
  line-height: 1;
}

.wuc-menu {
  border-right: none;
}

.wuc-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid var(--el-border-color-lighter);
  min-height: 600px;
}
</style>
