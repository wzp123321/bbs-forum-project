<template>
  <el-drawer custom-class="um-add-edit-drawer" :close-on-press-escape="false" :close-on-click-modal="false"
    v-model="dialogVisible" title="新增" direction="rtl" :before-close="handleClose">
    <el-form label-position="top" label-width="auto" :model="dialogForm" style="max-width: 600px">
      <el-form-item label="名称：">
        <el-input v-model="dialogForm.name" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch v-model="dialogForm.status" active-text="激活" inactive-text="未激活" />
      </el-form-item>
      <el-form-item label="类型：">
        <el-select v-model="dialogForm.type" placeholder="类型">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述：">
        <el-input v-model="dialogForm.describe" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          确定
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { RowFrom } from '../model'
defineOptions({
  name: 'UmAddEditDrawer',
});


// 表单
const dialogForm = reactive<RowFrom>({
  name: '',
  type: '',
  status: false,
  describe: ''
})
// 字典类型
const options = [{
  value: '1',
  label: '1',
},
{
  value: '2',
  label: '2',
}
]
// 开关
const dialogVisible = ref(false);
// 打开
const handleOpen = (row: RowFrom) => {
  dialogVisible.value = true;
  dialogForm.name = row.name
  dialogForm.type = row.type
  dialogForm.status = row.status
  dialogForm.describe = row.describe
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
  