<template>
  <el-drawer custom-class="um-add-edit-drawer" :close-on-press-escape="false" :close-on-click-modal="false"
    v-model="dialogVisible" title="新增" direction="rtl" :before-close="handleClose">
    <el-form label-position="top" label-width="auto" :model="dialogForm" style="max-width: 600px">
      <el-form-item label="字典类型：">
        <el-input v-model="dialogForm.name" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          确定
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { TypeFrom } from '../model'
defineOptions({
  name: 'UmAddEditDrawer',
});


// 表单
const dialogForm = reactive<TypeFrom>({
  id: '',
  name: ''
})
// 开关
const dialogVisible = ref(false);
// 打开
const handleOpen = (row: TypeFrom) => {
  dialogVisible.value = true;
  dialogForm.id = row.id
  dialogForm.name = row.name
};
// 关闭
const handleClose = () => {
  dialogVisible.value = false;
};

defineExpose({
  handleOpen,
});
</script>
<style lang="less" scoped>
.um-add-edit-drawer {}
</style>
  