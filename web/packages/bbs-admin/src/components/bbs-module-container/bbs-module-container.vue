<template>
  <el-col class="bbs-module-container" :span="props.rowSpan">
    <header class="bmc-header">
      <h5>{{ props.title }}</h5>
      <slot name="header"></slot>
    </header>
    <main class="bmc-main"></main>
  </el-col>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'BbsModuleContainer',
});
interface Props {
  rowSpan: number;
  title: string;
}
const props = withDefaults(defineProps<Props>(), {
  rowSpan: 12,
  title: '',
});
</script>

<style lang="less" scoped>
.bbs-module-container {
  height: 360px;
  border-radius: var(--bbs-radius-s);
  display: flex;
  flex-direction: column;

  > .bmc-header {
    padding: var(--bbs-space-12) var(--bbs-space-20);
    background-color: var(--bbs-gray_brand-color-2);

    > h5 {
      font-size: var(--bbs-font-size-h20);
      color: var(--bbs-text-color-primary);
      font-family: var(--bbs-font-family-bold);
    }
  }

  > .bmc-main {
    flex: auto;
    background: var(--bbs-bg-color);
  }
}
</style>
