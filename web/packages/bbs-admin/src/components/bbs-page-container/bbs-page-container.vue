<template>
  <div class="bbs-page-container">
    <header v-if="$slots.header" class="bpc-header">
      <slot name="header"></slot>
    </header>
    <div class="bpc-content">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'BbsPageContainer',
});
</script>

<style lang="less" scoped>
.bbs-page-container {
  width: 100%;
  background: var(--bbs-bg-color);
  border-radius: var(--bbs-radius-lg);
  box-shadow: var(--bbs-shadow-xs);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.bpc-header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--bbs-border-color-light);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.bpc-content {
  flex: 1;
  padding: 24px;
  min-width: 0;
}
</style>
