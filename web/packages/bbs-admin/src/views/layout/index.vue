<template>
  <div class="bbs-admin">
    <el-container>
      <el-header>
        <h5>9527&BBS</h5>
      </el-header>
      <el-container>
        <el-aside>
          <el-menu class="bbs-menu">
            <layout-menu-item :menuList="menuList"></layout-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <h5>{{ title }}</h5>
          <section class="bbs-content">
            <router-view></router-view>
          </section>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { menuList } from '@/config';
import LayoutMenuItem from './layout-menu-item//layout-menu-item';

defineOptions({
  name: 'BbsAdmin',
});
const route = useRoute();
const title = computed(() => {
  return route.meta.title;
});
</script>

<style lang="less" scoped>
.bbs-admin {
  width: 100%;
  height: 100%;

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

      h5 {
        font-family: DINPro Bold;
        color: var(--bbs-color-white);
      }
    }

    > .el-container {
      display: flex;

      > .el-main {
        background: var(--bbs-fill-color-dark);
        overflow: hidden;
        flex: 1;
        flex-shrink: 0;
        flex-basis: auto;
        overflow: auto;
        padding: var(--bbs-space-20);

        display: flex;
        flex-direction: column;

        > h5 {
          font-size: var(--bbs-font-size-h16);
          font-weight: var(--bbs-font-weight-primary);
          line-height: 32px;
          color: var(--bbs-text-color-primary);
        }

        .bbs-content {
          flex: auto;
          flex-shrink: 0;
          background: var(--bbs-bg-color);
          padding: var(--bbs-space-20);
          overflow: auto;
          box-sizing: border-box;
          overflow-x: hidden;
        }
      }
    }
  }
}
</style>
