<template>
  <div class="bbs-page-container"></div>
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

  background: var(--bbs-bg-color-page);
}
</style>
