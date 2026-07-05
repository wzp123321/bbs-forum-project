<template>
  <header class="layout-header">
    <div class="lh-container">
      <!-- 左侧: Logo + 导航 -->
      <div class="lh-left">
        <router-link to="/" class="lh-logo">
          <div class="lh-logo-icon">
            <span class="lh-logo-text">9527</span>
          </div>
          <div class="lh-logo-info">
            <div class="lh-logo-name">9527 论坛</div>
            <div class="lh-logo-slogan">社区广场</div>
          </div>
        </router-link>

        <nav class="lh-nav">
          <router-link
            v-for="item in headerLeftMenuList"
            :key="item.path"
            :to="item.path"
            class="lh-nav-item"
            active-class="is-active"
            :exact-active-class="item.path === '/' ? 'is-active' : ''"
          >
            {{ item.title }}
          </router-link>
        </nav>
      </div>

      <!-- 右侧: 搜索 + 操作 + 用户 -->
      <div class="lh-right">
        <div class="lh-search" :class="{ 'is-focused': searchFocused }">
          <el-icon class="lh-search-icon"><Search /></el-icon>
          <input
            v-model="searchLabel"
            type="text"
            placeholder="搜索帖子、用户、标签..."
            class="lh-search-input"
            maxlength="50"
            @focus="searchFocused = true"
            @blur="searchFocused = false"
            @keyup.enter="handleSearch"
          />
          <span v-if="searchLabel" class="lh-search-clear" @click="searchLabel = ''">
            <el-icon><CircleClose /></el-icon>
          </span>
        </div>

        <button class="lh-publish-btn" @click="goPublish">
          <el-icon><EditPen /></el-icon>
          <span>发帖</span>
        </button>

        <router-link to="/admin" target="_blank" class="lh-admin-link" title="进入管理后台">
          <el-icon><Setting /></el-icon>
        </router-link>

        <template v-if="!userName">
          <button class="lh-link-btn" @click="navigateToLogin">登录</button>
          <button class="lh-primary-btn" @click="navigateToRegister">注册</button>
        </template>

        <div v-else class="lh-user" @click="goUserCenter">
          <el-avatar :size="36" :icon="UserFilled" class="lh-user-avatar" />
          <div class="lh-user-info">
            <div class="lh-user-name">{{ userName }}</div>
            <div class="lh-user-level">LV.3</div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Search, UserFilled, EditPen, Setting, CircleClose } from '@element-plus/icons-vue';
import { headerLeftMenuList } from './model';
import { useUserInfo } from '@/hooks';
import { logoutApi } from '@/apis/auth';
import { tokenStore, userStore } from '@/utils';

defineOptions({
  name: 'LayoutHeader',
});
const route = useRoute();
const router = useRouter();

const searchLabel = ref('');
const searchFocused = ref(false);

const handleSearch = () => {
  const kw = searchLabel.value.trim();
  if (!kw) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  router.push({ path: '/search', query: { keyword: kw } });
  searchLabel.value = '';
};

const goPublish = () => {
  if (!userName.value) {
    ElMessage.warning('请先登录');
    navigateToLogin();
    return;
  }
  router.push('/post/publish');
};

const navigateToPage = (path: string) => router.push(path);
const navigateToLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } });
const navigateToRegister = () => router.push({ path: '/register', query: { redirect: route.fullPath } });
const goUserCenter = () => router.push('/user-center');

const { userName, initUserByStorage } = useUserInfo();

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确认退出登录?', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      customClass: 'bbs-message-box',
    });
  } catch {
    return;
  }
  try {
    await logoutApi();
  } catch {
    // 即使后端失败也继续清理本地
  }
  tokenStore.clear();
  userStore.clear();
  userName.value = '';
  ElMessage.success('已退出登录');
  router.replace('/');
};

onMounted(() => {
  searchLabel.value = '';
  initUserByStorage();
});
</script>

<style lang="less" scoped>
.layout-header {
  position: sticky;
  top: 0;
  z-index: var(--bbs-z-sticky);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 1px solid var(--bbs-border-color-light);
  transition: all var(--bbs-transition-base);
}

.lh-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

/* ============ 左侧 ============ */
.lh-left {
  display: flex;
  align-items: center;
  gap: 40px;
  flex-shrink: 0;
}

.lh-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  transition: transform var(--bbs-transition-base);

  &:hover {
    transform: translateY(-1px);
  }
}

.lh-logo-icon {
  width: 38px;
  height: 38px;
  background: var(--bbs-gradient-primary);
  border-radius: var(--bbs-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
  transition: box-shadow var(--bbs-transition-base);
}

.lh-logo:hover .lh-logo-icon {
  box-shadow: 0 6px 16px rgba(255, 107, 53, 0.45);
}

.lh-logo-text {
  color: #fff;
  font-size: 14px;
  font-weight: var(--bbs-font-weight-bold);
  letter-spacing: -0.5px;
}

.lh-logo-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.lh-logo-name {
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-bold);
  color: var(--bbs-text-color-primary);
  letter-spacing: -0.3px;
}

.lh-logo-slogan {
  font-size: 11px;
  color: var(--bbs-text-color-placeholder);
  margin-top: 1px;
}

.lh-nav {
  display: flex;
  gap: 4px;
}

.lh-nav-item {
  position: relative;
  padding: 8px 16px;
  border-radius: var(--bbs-radius-md);
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-medium);
  color: var(--bbs-text-color-regular);
  text-decoration: none;
  transition: all var(--bbs-transition-fast);

  &:hover {
    color: var(--bbs-color-primary);
    background: var(--bbs-color-primary-light);
  }

  &.is-active {
    color: var(--bbs-color-primary);
    background: var(--bbs-color-primary-light);
    font-weight: var(--bbs-font-weight-semibold);
  }
}

/* ============ 右侧 ============ */
.lh-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: flex-end;
}

/* 搜索框 */
.lh-search {
  position: relative;
  display: flex;
  align-items: center;
  width: 220px;
  height: 38px;
  padding: 0 12px;
  background: var(--bbs-bg-color-secondary);
  border: 1.5px solid transparent;
  border-radius: var(--bbs-radius-full);
  transition: all var(--bbs-transition-base);

  &:hover {
    background: var(--bbs-bg-color-tertiary);
  }

  &.is-focused {
    background: #fff;
    border-color: var(--bbs-color-primary);
    box-shadow: 0 0 0 4px var(--bbs-color-primary-light);
  }
}

.lh-search-icon {
  color: var(--bbs-text-color-placeholder);
  font-size: 16px;
  flex-shrink: 0;
  margin-right: 8px;
  transition: color var(--bbs-transition-fast);
}

.lh-search.is-focused .lh-search-icon {
  color: var(--bbs-color-primary);
}

.lh-search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  font-size: var(--bbs-font-size-base);
  color: var(--bbs-text-color-primary);

  &::placeholder {
    color: var(--bbs-text-color-placeholder);
  }
}

.lh-search-clear {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: var(--bbs-text-color-placeholder);
  transition: color var(--bbs-transition-fast);

  &:hover {
    color: var(--bbs-text-color-secondary);
  }
}

/* 发布按钮 */
.lh-publish-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 38px;
  padding: 0 16px;
  background: var(--bbs-gradient-primary);
  color: #fff;
  border: none;
  border-radius: var(--bbs-radius-full);
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-medium);
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
  transition: all var(--bbs-transition-base);

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(255, 107, 53, 0.4);
  }

  &:active {
    transform: translateY(0);
  }
}

/* 管理入口 */
.lh-admin-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: var(--bbs-radius-md);
  color: var(--bbs-text-color-secondary);
  font-size: 18px;
  text-decoration: none;
  transition: all var(--bbs-transition-fast);

  &:hover {
    color: var(--bbs-color-primary);
    background: var(--bbs-color-primary-light);
  }
}

/* 登录/注册按钮 */
.lh-link-btn {
  height: 38px;
  padding: 0 12px;
  background: transparent;
  border: none;
  color: var(--bbs-text-color-regular);
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-medium);
  cursor: pointer;
  border-radius: var(--bbs-radius-md);
  transition: color var(--bbs-transition-fast);

  &:hover {
    color: var(--bbs-color-primary);
  }
}

.lh-primary-btn {
  height: 38px;
  padding: 0 18px;
  background: var(--bbs-text-color-primary);
  color: #fff;
  border: none;
  border-radius: var(--bbs-radius-full);
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-medium);
  cursor: pointer;
  transition: all var(--bbs-transition-base);

  &:hover {
    background: var(--bbs-gray-color-8);
    transform: translateY(-1px);
  }
}

/* 用户卡片 */
.lh-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: var(--bbs-radius-full);
  cursor: pointer;
  transition: background var(--bbs-transition-fast);

  &:hover {
    background: var(--bbs-bg-color-secondary);
  }
}

.lh-user-avatar {
  background: var(--bbs-gradient-primary) !important;
  color: #fff !important;
  flex-shrink: 0;
}

.lh-user-info {
  line-height: 1.2;
}

.lh-user-name {
  font-size: var(--bbs-font-size-sm);
  font-weight: var(--bbs-font-weight-semibold);
  color: var(--bbs-text-color-primary);
}

.lh-user-level {
  font-size: 10px;
  color: var(--bbs-color-primary);
  font-weight: var(--bbs-font-weight-medium);
  margin-top: 1px;
}
</style>
