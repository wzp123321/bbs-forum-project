<template>
  <el-drawer
    custom-class="um-add-edit-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    :title="isEdit ? '编辑字典类型' : '新增字典类型'"
    direction="rtl"
    :before-close="close"
    size="420px"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="例: 用户状态" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="编码" prop="code">
        <el-input v-model="form.code" placeholder="例: user_status, 字母开头" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="描述">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入描述"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="close">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { createDictionaryTypeApi, updateDictionaryTypeApi } from '@/apis';
import type { DictionaryTypeVO } from '@/apis';
import { TypeForm } from '../constant/model';

defineOptions({ name: 'UmAddEditDrawer' });

const emit = defineEmits<{ (e: 'saved'): void }>();

const visible = ref(false);
const submitting = ref(false);
const formRef = ref<FormInstance>();

const form = reactive<TypeForm>({
  id: undefined,
  name: '',
  code: '',
  description: '',
  status: 1,
});

const isEdit = computed(() => !!form.id);

const rules: FormRules<TypeForm> = {
  name: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入类型编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '需以字母开头, 只含字母/数字/下划线', trigger: 'blur' },
  ],
};

const resetForm = () => {
  form.id = undefined;
  form.name = '';
  form.code = '';
  form.description = '';
  form.status = 1;
};

const open = (row?: DictionaryTypeVO) => {
  resetForm();
  if (row) {
    form.id = row.id;
    form.name = row.name;
    form.code = row.code;
    form.description = row.description || '';
    form.status = row.status ?? 1;
  }
  visible.value = true;
};

const close = () => {
  visible.value = false;
};

const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitting.value = true;
  try {
    const payload = {
      name: form.name,
      code: form.code,
      description: form.description,
      status: form.status,
    };
    if (isEdit.value) {
      await updateDictionaryTypeApi(form.id!, payload);
      ElMessage.success('修改成功');
    } else {
      await createDictionaryTypeApi(payload);
      ElMessage.success('新增成功');
    }
    close();
    emit('saved');
  } finally {
    submitting.value = false;
  }
};

defineExpose({ open });
</script>

<style lang="less" scoped>
.um-add-edit-drawer {
}
</style>
