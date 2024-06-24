<template>
  <div class="bbs-page-container">
    <header class="bpc-header">
      <slot name="header"></slot>
    </header>
    <header class="bpc-content">
      <slot name="content"></slot>
    </header>
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
  height: 100%;
  overflow: hidden;
  min-width: 1310px;
  padding: var(--bbs-space-20);
  box-sizing: border-box;

  background: var(--bbs-bg-color);
  display: flex;
  flex-direction: column;

  .bpc-header {
    padding: var(--bbs-space-20);
    border-bottom: 1px solid var(--bbs-border-color);
    flex-shrink: 0;
  }

  .bpc-content {
    flex: auto;
    flex-shrink: 0;
  }
}
</style>
