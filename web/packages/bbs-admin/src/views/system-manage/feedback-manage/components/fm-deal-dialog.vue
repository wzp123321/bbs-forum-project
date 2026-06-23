<template>
  <el-dialog class="fm-deal-dialog" v-model="visible" title="处理反馈" width="500" :before-close="handleClose">
    <el-input v-model="content" :rows="3" type="textarea" placeholder="请输入"></el-input>
    <template #footer>
      <footer>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary">确认</el-button>
      </footer>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { useDialogOpen } from '@/hooks';
import { ref } from 'vue';
defineOptions({
  name: 'FmDealDialog',
});
const { visible, handleClose, handleOpen } = useDialogOpen();

const content = ref('');
/**
 * 打开
 */
const show = () => {
  content.value = '';
  handleOpen();
};

defineExpose({
  show,
});
</script>

<style lang="less" scoped>
.fm-deal-dialog {
}
</style>
