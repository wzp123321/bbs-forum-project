<template>
  <div class="bbs-admin">
    <!-- 侧边栏 -->
    <aside class="ba-aside" :class="{ 'is-collapsed': collapsed }">
      <div class="ba-logo">
        <div class="ba-logo-icon">
          <span class="ba-logo-text">9527</span>
        </div>
        <div class="ba-logo-info">
          <div class="ba-logo-name">9527 管理后台</div>
          <div class="ba-logo-sub">Admin Console</div>
        </div>
      </div>

      <div class="ba-menu-wrap">
        <el-menu
          :default-active="defaultActive"
          class="ba-menu"
          :collapse="collapsed"
          :collapse-transition="false"
        >
          <layout-menu-item :menuList="menuList"></layout-menu-item>
        </el-menu>
      </div>

      <div class="ba-aside-foot">
        <div class="ba-version">v1.0.0</div>
      </div>
    </aside>

    <!-- 主体 -->
    <div class="ba-main">
      <!-- 顶栏 -->
      <header class="ba-header">
        <div class="ba-header-left">
          <button class="ba-toggle" @click="collapsed = !collapsed" :title="collapsed ? '展开' : '收起'">
            <el-icon><Expand v-if="collapsed" /><Fold v-else /></el-icon>
          </button>
          <el-breadcrumb separator="/" class="ba-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentMenuTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="ba-header-right">
          <button class="ba-header-icon" title="刷新" @click="refresh">
            <el-icon><Refresh /></el-icon>
          </button>
          <button class="ba-header-icon" title="全屏" @click="toggleFullscreen">
            <el-icon><FullScreen /></el-icon>
          </button>
          <a href="/" target="_blank" class="ba-header-icon" title="前往论坛">
            <el-icon><Link /></el-icon>
          </a>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="ba-user">
              <el-avatar :size="36" :icon="UserFilled" class="ba-user-avatar" />
              <div class="ba-user-info">
                <div class="ba-user-name">{{ userName || 'admin' }}</div>
                <div class="ba-user-role">超级管理员</div>
              </div>
              <el-icon class="ba-user-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="forum">
                  <el-icon><HomeFilled /></el-icon>
                  前往论坛
                </el-dropdown-item>
                <el-dropdown-item command="profile" divided>
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="ba-content">
        <router-view v-slot="{ Component }">
          <transition name="bbs-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh, FullScreen, Link, ArrowDown, Expand, Fold, HomeFilled, User, SwitchButton, UserFilled } from '@element-plus/icons-vue';
import { menuList } from '@/config';
import { logoutApi } from '@/apis/auth';
import LayoutMenuItem from './layout-menu-item/layout-menu-item.vue';
import { tokenStore, USER_NAME_KEY } from '@/utils/index';

defineOptions({ name: 'BbsAdmin' });
const router = useRouter();
const route = useRoute();
const defaultActive = menuList?.[0]?.index;
const userName = ref(sessionStorage.getItem(USER_NAME_KEY) || '');
const collapsed = ref(false);

const currentMenuTitle = computed(() => {
  return (route.meta?.title as string) || 'Dashboard';
});

const handleCommand = async (cmd: string) => {
  if (cmd === 'forum') {
    window.open('/', '_blank');
    return;
  }
  if (cmd === 'profile') {
    ElMessage.info('个人资料开发中');
    return;
  }
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确认退出登录?', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      });
    } catch {
      return;
    }
    try {
      await logoutApi();
    } catch {
      // 忽略
    }
    tokenStore.clear();
    ElMessage.success('已退出登录');
    router.push('/login');
  }
};

const refresh = () => {
  window.location.reload();
};

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};
</script>

<style lang="less" scoped>
.bbs-admin {
  display: flex;
  width: 100%;
  height: 100vh;
  background: var(--bbs-bg-color-page);
  overflow: hidden;
}

/* ============ 侧边栏 ============ */
.ba-aside {
  display: flex;
  flex-direction: column;
  width: 240px;
  background: #fff;
  border-right: 1px solid var(--bbs-border-color-light);
  transition: width var(--bbs-transition-base);
  flex-shrink: 0;
  position: relative;
  z-index: 10;

  &.is-collapsed {
    width: 64px;

    .ba-logo-info,
    .ba-version,
    .ba-menu :deep(.el-menu-item span),
    .ba-menu :deep(.el-sub-menu__title span) {
      display: none;
    }
  }
}

.ba-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 64px;
  padding: 0 20px;
  border-bottom: 1px solid var(--bbs-border-color-light);
  flex-shrink: 0;
}

.ba-logo-icon {
  width: 36px;
  height: 36px;
  background: var(--bbs-gradient-primary);
  border-radius: var(--bbs-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
  flex-shrink: 0;
}

.ba-logo-text {
  color: #fff;
  font-size: 13px;
  font-weight: var(--bbs-font-weight-bold);
  letter-spacing: -0.3px;
}

.ba-logo-info {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  overflow: hidden;
}

.ba-logo-name {
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-bold);
  color: var(--bbs-text-color-primary);
  white-space: nowrap;
  letter-spacing: -0.3px;
}

.ba-logo-sub {
  font-size: 10px;
  color: var(--bbs-text-color-placeholder);
  margin-top: 1px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.ba-menu-wrap {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.ba-menu {
  border-right: none;
  background: transparent;
}

.ba-menu :deep(.el-menu-item),
.ba-menu :deep(.el-sub-menu__title) {
  height: 44px;
  line-height: 44px;
  margin: 4px 12px;
  border-radius: var(--bbs-radius-md);
  font-weight: var(--bbs-font-weight-medium);
  transition: all var(--bbs-transition-fast);
}

.ba-menu :deep(.el-menu-item:hover),
.ba-menu :deep(.el-sub-menu__title:hover) {
  background: var(--bbs-color-primary-light) !important;
  color: var(--bbs-color-primary) !important;
}

.ba-menu :deep(.el-menu-item.is-active) {
  background: var(--bbs-gradient-primary) !important;
  color: #fff !important;
  box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
}

.ba-menu :deep(.el-sub-menu .el-menu-item) {
  margin: 4px 12px 4px 24px;
  font-size: 13px;
}

.ba-aside-foot {
  padding: 12px 20px;
  border-top: 1px solid var(--bbs-border-color-light);
  flex-shrink: 0;
}

.ba-version {
  font-size: 11px;
  color: var(--bbs-text-color-placeholder);
  text-align: center;
  letter-spacing: 0.5px;
}

/* ============ 主体 ============ */
.ba-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 顶栏 */
.ba-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid var(--bbs-border-color-light);
  flex-shrink: 0;
}

.ba-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ba-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: transparent;
  border: none;
  border-radius: var(--bbs-radius-md);
  color: var(--bbs-text-color-secondary);
  font-size: 18px;
  cursor: pointer;
  transition: all var(--bbs-transition-fast);

  &:hover {
    background: var(--bbs-color-primary-light);
    color: var(--bbs-color-primary);
  }
}

.ba-breadcrumb {
  font-size: var(--bbs-font-size-base);
}

.ba-breadcrumb :deep(.el-breadcrumb__inner) {
  color: var(--bbs-text-color-secondary);
  font-weight: var(--bbs-font-weight-medium);
}

.ba-breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--bbs-text-color-primary);
  font-weight: var(--bbs-font-weight-semibold);
}

.ba-header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ba-header-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: transparent;
  border: none;
  border-radius: var(--bbs-radius-md);
  color: var(--bbs-text-color-secondary);
  font-size: 18px;
  cursor: pointer;
  text-decoration: none;
  transition: all var(--bbs-transition-fast);

  &:hover {
    background: var(--bbs-color-primary-light);
    color: var(--bbs-color-primary);
  }
}

.ba-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px 4px 4px;
  border-radius: var(--bbs-radius-full);
  cursor: pointer;
  margin-left: 4px;
  transition: background var(--bbs-transition-fast);

  &:hover {
    background: var(--bbs-bg-color-secondary);
  }
}

.ba-user-avatar {
  background: var(--bbs-gradient-primary) !important;
  color: #fff !important;
  flex-shrink: 0;
}

.ba-user-info {
  line-height: 1.2;
}

.ba-user-name {
  font-size: var(--bbs-font-size-sm);
  font-weight: var(--bbs-font-weight-semibold);
  color: var(--bbs-text-color-primary);
}

.ba-user-role {
  font-size: 10px;
  color: var(--bbs-color-primary);
  font-weight: var(--bbs-font-weight-medium);
  margin-top: 1px;
}

.ba-user-arrow {
  font-size: 12px;
  color: var(--bbs-text-color-placeholder);
  transition: transform var(--bbs-transition-fast);
}

.ba-user:hover .ba-user-arrow {
  transform: translateY(2px);
}

/* 内容 */
.ba-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>

<style lang="less">
/* 全局路由切换动画 */
.bbs-fade-enter-active,
.bbs-fade-leave-active {
  transition: all 200ms cubic-bezier(0.16, 1, 0.3, 1);
}

.bbs-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.bbs-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
