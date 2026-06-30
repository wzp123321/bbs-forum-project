<template>
  <el-drawer custom-class="um-add-edit-drawer" :close-on-press-escape="false" :close-on-click-modal="false"
    v-model="visible" :title="isEdit ? '编辑字典项' : '新增字典项'" direction="rtl" :before-close="close">
    <el-form label-position="top" label-width="auto" :model="form" style="max-width: 600px">
      <el-form-item label="名称：">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch v-model="form.status" active-text="激活" inactive-text="未激活" />
      </el-form-item>
      <el-form-item label="类型：">
        <el-select v-model="form.type" placeholder="类型">
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述：">
        <el-input v-model="form.describe" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="close">
          确定
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue';
import { RowFrom } from '../constant/model'
defineOptions({
  name: 'UmAddEditDrawer',
});

const form = reactive<RowFrom>({
  name: '',
  type: '',
  status: false,
  describe: ''
})

const typeOptions = [
  { value: '1', label: '1' },
  { value: '2', label: '2' },
]

const visible = ref(false);
const isEdit = computed(() => !!form.name);

const open = (row: RowFrom) => {
  visible.value = true;
  form.name = row.name
  form.type = row.type
  form.status = row.status
  form.describe = row.describe
};

const close = () => {
  visible.value = false;
};

defineExpose({
  open,
});
</script>
<style lang="less" scoped>
.um-add-edit-drawer {}
</style>
  