<template>
  <el-drawer
    custom-class="cm-add-edit-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    :title="isEdit ? '编辑板块' : '新增板块'"
    direction="rtl"
    :before-close="handleClose"
    size="420px"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" v-loading="loading">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入板块名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入板块描述" />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" :max="9999" />
        <span class="cm-tip">升序,数字越小越靠前</span>
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { categoryApi } from '@/utils';
import type { CategoryVO } from '@/apis/category';
import { RowFrom } from '../constant/model';

defineOptions({ name: 'CmAddEditDrawer' });

const emit = defineEmits<{ (e: 'saved'): void }>();

const visible = ref(false);
const loading = ref(false);
const submitting = ref(false);
const formRef = ref<FormInstance>();

const form = reactive<RowFrom>({
  name: '',
  description: '',
  sort: 0,
  status: 1,
});

const isEdit = computed(() => !!form.id);

const rules: FormRules<RowFrom> = {
  name: [{ required: true, message: '请输入板块名称', trigger: 'blur' }],
};

const reset = () => {
  form.id = undefined;
  form.name = '';
  form.description = '';
  form.sort = 0;
  form.status = 1;
};

const handleOpen = (row?: CategoryVO) => {
  reset();
  if (row) {
    form.id = row.id;
    form.name = row.name;
    form.description = row.description || '';
    form.sort = row.sort || 0;
    form.status = row.status ?? 1;
  }
  visible.value = true;
};

const handleClose = () => {
  visible.value = false;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (isEdit.value) {
      await categoryApi.updateCategory(form.id!, {
        name: form.name,
        description: form.description,
        sort: form.sort,
        status: form.status,
      });
      ElMessage.success('修改成功');
    } else {
      await categoryApi.createCategory({
        name: form.name,
        description: form.description,
        sort: form.sort,
        status: form.status,
      });
      ElMessage.success('新增成功');
    }
    handleClose();
    emit('saved');
  } finally {
    submitting.value = false;
  }
};

defineExpose({ handleOpen });
</script>

<style lang="less" scoped>
.cm-add-edit-drawer {
  .cm-tip {
    margin-left: 8px;
    color: var(--el-text-color-secondary);
    font-size: 12px;
  }
}
</style>
