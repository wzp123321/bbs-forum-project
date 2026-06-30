<template>
  <el-drawer custom-class="um-add-edit-drawer" :close-on-press-escape="false" :close-on-click-modal="false"
    v-model="visible" :title="isEdit ? '编辑字典类型' : '新增字典类型'" direction="rtl" :before-close="close">
    <el-form label-position="top" label-width="auto" :model="form" style="max-width: 600px">
      <el-form-item label="字典类型：">
        <el-input v-model="form.name" />
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
import { TypeFrom } from '../constant/model'
defineOptions({
  name: 'UmAddEditDrawer',
});

const form = reactive<TypeFrom>({
  id: '',
  name: ''
})

const visible = ref(false);
const isEdit = computed(() => !!form.id);

const open = (row: TypeFrom) => {
  visible.value = true;
  form.id = row.id
  form.name = row.name
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
  