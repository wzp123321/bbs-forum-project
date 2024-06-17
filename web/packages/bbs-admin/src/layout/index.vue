<template>
  <div class="bbs-admin">
    <el-container>
      <el-header>Header</el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
defineOptions({
  name: 'BbsAdmin',
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
  }
}
</style>
