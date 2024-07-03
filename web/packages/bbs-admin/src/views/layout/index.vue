<template>
  <div class="bbs-admin">
    <el-container>
      <el-header>
        <h5>9527&BBS</h5>
        <section class="ba-user">
          <el-dropdown>
            <div class="ba-user-info">
              <img src="../../assets/images/common/common-avatar.png" alt="avatar" />
              <h6>admin</h6>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>前往论坛</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </section>
      </el-header>
      <el-container>
        <el-aside>
          <el-menu :default-active="defaultActive" class="bbs-menu">
            <layout-menu-item :menuList="menuList"></layout-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <main class="bbs-main">
            <router-view></router-view>
          </main>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { menuList } from '@/config';
import LayoutMenuItem from './layout-menu-item/layout-menu-item.vue';

defineOptions({
  name: 'BbsAdmin',
});
const defaultActive = menuList?.[0]?.index;
</script>

<style lang="less" scoped>
.bbs-admin {
  width: 100%;
  height: 100%;
  --el-menu-text-color: var(--bbs-text-color-primary);
  --el-menu-hover-bg-color: var(--bbs-menu-hover-bg-color);
  --el-menu-active-color: var(--bbs-color-primary);

  .el-container {
    width: 100%;
    height: 100%;
    overflow: hidden;

    .el-header {
      padding: 20px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      background-color: var(--bbs-color-black);
      color: var(--bbs-color-white);

      h5 {
        font-family: DINPro Bold;
        color: var(--bbs-color-white);
        font-size: var(--bbs-font-size-h24);
      }

      .ba-user {
        .ba-user-info {
          display: flex;
          align-items: center;
          gap: var(--bbs-space-8);

          h6 {
            font-size: var(--bbs-font-size-h14);
            color: var(--bbs-color-white);
            font-weight: var(--bbs-font-weight-primary);
          }
        }
      }
    }

    > .el-container {
      display: flex;

      > .el-aside,
      > .el-aside :deep(.el-menu) {
        border-right: none;
        width: 248px;

        .el-menu-item.is-active {
          background-color: rgba(255, 255, 255, 0.1);
        }

        .el-sub-menu__title,
        .el-menu-item {
          display: flex;
          align-items: center;
          gap: var(--bbs-space-8);
        }
      }

      > .el-main {
        overflow: hidden;
        flex: auto;
        padding: var(--bbs-space-20);
        background: var(--bbs-bg-color-page);

        display: flex;
        flex-direction: column;

        .bbs-main {
          background: var(--bbs-bg-color);
          height: 100%;
        }
      }
    }
  }
}
</style>
