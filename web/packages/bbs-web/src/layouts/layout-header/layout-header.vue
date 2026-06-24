<template>
  <el-header class="layout-header">
    <div class="lh-container">
      <section class="lh-left">
        <el-image style="width: 48px; height: 48px" src="/assets/images/common/icon.png" fit="fill" />
        <el-menu mode="horizontal" :ellipsis="false">
          <template v-for="(item, index) in headerLeftMenuList">
            <el-menu-item
              v-if="!item.children?.length"
              :index="`${index + 1}`"
              :route="item.path"
              @click="handleSelect(item.path)"
            >
              {{ item.title }}
            </el-menu-item>
            <el-sub-menu v-else :index="`${index + 1}`">
              <template #title>{{ item.title }}</template>
              <el-menu-item
                v-for="(cItem, cIndex) in item.children"
                :index="`${index + 1}-${cIndex + 1}`"
                @click="handleSelect(item.path)"
              >
                {{ cItem.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </section>
      <section class="lh-right">
        <el-input
          placeholder="搜索"
          v-inputFilter:text
          maxlength="20"
          v-model="searchLabel"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <el-menu mode="horizontal" :ellipsis="false">
          <template v-for="(item, index) in headerRightMenuList">
            <el-menu-item v-if="!item.children?.length" :index="`${index + 1}`" @click="handleSelect(item.path)">
              {{ item.title }}
            </el-menu-item>
            <el-sub-menu v-else :index="`${index + 1}`">
              <template #title>{{ item.title }}</template>
              <el-menu-item
                v-for="(cItem, cIndex) in item.children"
                :index="`${index + 1}-${cIndex + 1}`"
                @click="handleSelect(item.path)"
              >
                {{ cItem.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
          <el-menu-item>管理平台</el-menu-item>
        </el-menu>

        <!-- 用户 -->
        <template v-if="!userName">
          <el-button type="primary" link @click="goLogin">登录</el-button>
          <el-button type="primary" @click="goRegister">注册</el-button>
        </template>
        <el-dropdown v-else trigger="click" @command="handleUserCmd">
          <span class="lh-user">
            <el-avatar :size="28" icon="UserFilled" />
            <span class="lh-user-name">{{ userName }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="publish">发布帖子</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </section>
    </div>
  </el-header>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { headerLeftMenuList, headerRightMenuList } from './model';
import { useUserInfo } from '@/hooks';
import { authApi } from '@/apis/auth';
import { tokenStore, userStore } from '@/utils';

defineOptions({
  name: 'LayoutHeader',
});
const route = useRoute();
const router = useRouter();
/**
 * 左侧菜单
 */
const handleSelect = (path: string) => {
  router.push({
    path,
    query: route.query,
  });
};

const searchLabel = ref('');

const handleSearch = () => {
  const kw = searchLabel.value.trim();
  if (!kw) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }
  router.push({ path: '/search', query: { keyword: kw } });
  searchLabel.value = '';
};

const { userName, initUserByStorage } = useUserInfo();

const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } });
const goRegister = () => router.push({ path: '/register', query: { redirect: route.fullPath } });

const handleUserCmd = async (cmd: string) => {
  if (cmd === 'publish') {
    router.push('/post/publish');
    return;
  }
  if (cmd === 'profile') {
    router.push('/user-center');
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
      await authApi.logout();
    } catch {
      // 即使后端失败也继续清理本地
    }
    tokenStore.clear();
    userStore.clear();
    userName.value = '';
    ElMessage.success('已退出登录');
    router.replace('/');
  }
};

onMounted(() => {
  searchLabel.value = '';
  initUserByStorage();
});
</script>
<style lang="less" scoped>
.layout-header {
  > .lh-container {
    width: 1200px;
    margin: 0 auto;
    justify-content: space-between;
    gap: 100px;

    > .lh-left {
      gap: 32px;

      .el-menu--horizontal.el-menu {
        flex: auto;
      }

      .el-image {
        border-radius: 50%;
      }
    }

    > .lh-right {
      flex: auto;
      justify-content: flex-end;

      > .el-input {
        width: 200px;
      }
    }
  }

  > .lh-container,
  > .lh-container > .lh-left,
  > .lh-container > .lh-right {
    display: flex;
    align-items: center;

    .el-menu--horizontal.el-menu {
      border: none;

      .el-menu-item {
        border-bottom: none;
      }

      .el-menu-item:hover,
      .el-menu-item.is-active {
        background: transparent;
        border-bottom: none;
      }
    }
  }
}
</style>
