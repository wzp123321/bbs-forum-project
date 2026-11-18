<template>
  <el-drawer
    custom-class="um-add-edit-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    :title="isEdit ? '编辑字典项' : '新增字典项'"
    direction="rtl"
    :before-close="close"
    size="420px"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="所属类型" prop="typeId">
        <el-select v-model="form.typeId" placeholder="请选择字典类型" style="width: 100%">
          <el-option v-for="t in typeOptions" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="展示名" prop="label">
        <el-input v-model="form.label" placeholder="例: 启用" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="存储值" prop="value">
        <el-input v-model="form.value" placeholder="例: 1" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" :max="9999" />
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
import {
  createDictionaryApi,
  listDictionaryTypesApi,
  updateDictionaryApi,
} from '@/apis';
import type { DictionaryTypeVO, DictionaryVO } from '@/apis';
import { DictForm } from '../constant/model';

defineOptions({ name: 'UmAddEditDrawer' });

const emit = defineEmits<{ (e: 'saved'): void }>();

const visible = ref(false);
const submitting = ref(false);
const formRef = ref<FormInstance>();
const typeOptions = ref<DictionaryTypeVO[]>([]);

const form = reactive<DictForm>({
  id: undefined,
  typeId: undefined,
  label: '',
  value: '',
  sort: 0,
  status: 1,
});

const isEdit = computed(() => !!form.id);

const rules: FormRules<DictForm> = {
  typeId: [{ required: true, message: '请选择字典类型', trigger: 'change' }],
  label: [{ required: true, message: '请输入展示名', trigger: 'blur' }],
  value: [{ required: true, message: '请输入存储值', trigger: 'blur' }],
};

const loadTypes = async () => {
  try {
    typeOptions.value = await listDictionaryTypesApi();
  } catch {
    typeOptions.value = [];
  }
};

const resetForm = () => {
  form.id = undefined;
  form.typeId = undefined;
  form.label = '';
  form.value = '';
  form.sort = 0;
  form.status = 1;
};

const open = async (row?: DictionaryVO) => {
  resetForm();
  await loadTypes();
  if (row) {
    form.id = row.id;
    form.typeId = row.typeId;
    form.label = row.label;
    form.value = row.value;
    form.sort = row.sort ?? 0;
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
      typeId: form.typeId!,
      label: form.label,
      value: form.value,
      sort: form.sort,
      status: form.status,
    };
    if (isEdit.value) {
      await updateDictionaryApi(form.id!, payload);
      ElMessage.success('修改成功');
    } else {
      await createDictionaryApi(payload);
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
