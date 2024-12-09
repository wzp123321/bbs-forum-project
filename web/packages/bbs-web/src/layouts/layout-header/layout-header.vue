<template>
  <el-header class="layout-header">
    <div class="lh-container">
      <section class="lh-left">
        <el-image style="width: 48px; height: 48px" src="/assets/images/common/icon.png" fit="fill" />
        <el-menu mode="horizontal" :ellipsis="false">
          <template v-for="(item, index) in headerLeftMenuList">
            <el-menu-item v-if="!item.children?.length" :index="`${index + 1}`" :route="item.path"
              @click="handleSelect(item.path)">
              {{ item.title }}
            </el-menu-item>
            <el-sub-menu v-else :index="`${index + 1}`">
              <template #title>{{ item.title }}</template>
              <el-menu-item v-for="(cItem, cIndex) in item.children" :index="`${index + 1}-${cIndex + 1}`"
                @click="handleSelect(item.path)">
                {{ cItem.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </section>
      <section class="lh-right">
        <el-input placeholder="搜索" v-inputFilter:text maxlength="20" v-model="searchLabel"></el-input>
        <el-menu mode="horizontal" :ellipsis="false">
          <template v-for="(item, index) in headerRightMenuList">
            <el-menu-item v-if="!item.children?.length" :index="`${index + 1}`" @click="handleSelect(item.path)">
              {{ item.title }}
            </el-menu-item>
            <el-sub-menu v-else :index="`${index + 1}`">
              <template #title>{{ item.title }}</template>
              <el-menu-item v-for="(cItem, cIndex) in item.children" :index="`${index + 1}-${cIndex + 1}`"
                @click="handleSelect(item.path)">
                {{ cItem.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
          <el-menu-item>管理平台</el-menu-item>
        </el-menu>

        <!-- 用户 -->
        <el-button v-if="!userName">登录</el-button>
      </section>
    </div>
  </el-header>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { headerLeftMenuList, headerRightMenuList } from './model';
import { useUserInfo } from '@/hooks';

defineOptions({
  name: 'LayoutHeader',
});
const route = useRoute();
const router = useRouter();
/**
 * 左侧菜单
 * @param {string} path
 */
const handleSelect = (path: string) => {
  router.push({
    path,
    query: route.query,
  });
};

const searchLabel = ref('');

const { userName, initUserByStorage } = useUserInfo();

onMounted(() => {
  searchLabel.value = '';
  initUserByStorage();
});
</script>
<style lang="less" scoped>
.layout-header {
  >.lh-container {
    width: 1200px;
    margin: 0 auto;
    justify-content: space-between;
    gap: 100px;

    >.lh-left {
      gap: 32px;

      .el-menu--horizontal.el-menu {
        flex: auto;
      }

      .el-image {
        border-radius: 50%;
      }
    }

    >.lh-right {
      flex: auto;
      justify-content: flex-end;

      >.el-input{
        width: 200px;
      }
    }
  }

  >.lh-container,
  >.lh-container>.lh-left,
  >.lh-container>.lh-right {
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
