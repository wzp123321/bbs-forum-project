<template>
  <el-header class="layout-header">
    <div class="lh-container">
      <section class="lh-left">
        <el-image style="width: 48px; height: 48px" src="/assets/images/common/icon.png" fit="fill" />
        <el-menu mode="horizontal" :ellipsis="false" @select="handleSelect">
          <template v-for="(item, index) in headerMenuList">
            <el-menu-item v-if="!item.children?.length" :index="`${index + 1}`">
              {{ item.title }}
            </el-menu-item>
            <el-sub-menu v-else :index="`${index + 1}`">
              <template #title>{{ item.title }}</template>
              <el-menu-item v-for="(cItem, cIndex) in item.children" :index="`${index + 1}-${cIndex + 1}`">
                {{ cItem.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </section>
      <section class="lh-right">1</section>
    </div>
  </el-header>
</template>
<script lang="ts" setup>
import { headerMenuList } from './model';

defineOptions({
  name: 'LayoutHeader',
});

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
};
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
      .el-image {
        border-radius: 50%;
      }
      .el-menu--horizontal.el-menu {
        flex: auto;
        border: none;
        .el-menu-item:hover {
          background: transparent;
        }
      }
    }
    > .lh-right {
      flex: auto;
    }
  }
  > .lh-container,
  > .lh-container > .lh-left,
  > .lh-container > .lh-right {
    display: flex;
    align-items: center;
  }
}
</style>
