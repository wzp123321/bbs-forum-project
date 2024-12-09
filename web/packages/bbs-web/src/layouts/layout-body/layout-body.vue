<template>
  <el-main class="layout-body">
    <div class="lb-container">

      <router-view></router-view>
    </div>
  </el-main>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'LayoutBody',
});
</script>
<style lang="less" scoped>
.layout-body {
  >.lb-container {
    width: 1200px;
    margin: 0 auto;
    height: 100%;
  }
}
</style>
