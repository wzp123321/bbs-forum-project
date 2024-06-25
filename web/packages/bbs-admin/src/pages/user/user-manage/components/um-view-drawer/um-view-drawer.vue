<template>
  <el-drawer
    custom-class="um-view-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    title="详情"
    direction="rtl"
    :before-close="handleClose"
  >
    <span>Hi, there!</span>
  </el-drawer>
</template>
<script lang="ts" setup>
import { ref } from 'vue';

defineOptions({
  name: 'UmViewDrawer',
});
// 开关
const visible = ref(false);
/**
 * 打开
 */
const handleOpen = () => {
  visible.value = true;
};
/**
 * 关闭
 */
const handleClose = () => {
  visible.value = false;
};

defineExpose({
  handleOpen,
});
</script>
<style lang="less" scoped>
.um-view-drawer {
}
</style>
