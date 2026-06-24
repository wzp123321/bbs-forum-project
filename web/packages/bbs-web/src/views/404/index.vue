<template>
  <div class="not-found">
    <el-result icon="warning" title="404" sub-title="页面不存在">
      <template #extra>
        <el-button type="primary" @click="$router.push('/')">回首页</el-button>
      </template>
    </el-result>
  </div>
</template>
<script lang="ts" setup>
defineOptions({ name: 'NotFound' });
</script>
<style lang="less" scoped>
.not-found {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
