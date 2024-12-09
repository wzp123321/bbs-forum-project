<template>
  <el-main class="layout-body">
    <router-view></router-view>
  </el-main>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'LayoutBody',
});
</script>
<style lang="less" scoped>
.layout-body {
}
</style>
