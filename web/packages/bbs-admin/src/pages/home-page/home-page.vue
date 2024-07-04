<template>
  <el-row class="home-page" :gutter="20">
    <bbs-module-container :rowSpan="12" title="近期访客"></bbs-module-container>
    <bbs-module-container :rowSpan="12" title="反馈处理"></bbs-module-container>
  </el-row>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'HomePage',
});
</script>

<style lang="less" scoped>
.home-page {
  width: 100%;
  height: 100%;
  padding: var(--bbs-space-20);
}
</style>
