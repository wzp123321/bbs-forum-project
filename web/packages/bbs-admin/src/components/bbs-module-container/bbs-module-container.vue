<template>
  <div class="bbs-module-container">
    <header class="bmc-header">
      <h5>{{ props.title }}</h5>
      <div v-if="$slots.header" class="bmc-extra">
        <slot name="header"></slot>
      </div>
    </header>
    <main class="bmc-main">
      <slot></slot>
    </main>
  </div>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'BbsModuleContainer',
});
interface Props {
  rowSpan?: number;
  title: string;
}
const props = withDefaults(defineProps<Props>(), {
  rowSpan: 12,
  title: '',
});
</script>

<style lang="less" scoped>
.bbs-module-container {
  background: var(--bbs-bg-color);
  border-radius: var(--bbs-radius-lg);
  box-shadow: var(--bbs-shadow-xs);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
}

.bmc-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--bbs-border-color-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;

  h5 {
    font-size: var(--bbs-font-size-base);
    color: var(--bbs-text-color-primary);
    font-weight: var(--bbs-font-weight-semibold);
    margin: 0;
  }
}

.bmc-extra {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bmc-main {
  flex: 1;
  padding: 16px 20px;
  overflow: auto;
  min-height: 0;
}
</style>
