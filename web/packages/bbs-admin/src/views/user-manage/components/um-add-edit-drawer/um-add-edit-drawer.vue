<template>
  <el-drawer
    custom-class="um-add-edit-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    direction="rtl"
    :before-close="handleClose"
    size="420px"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" v-loading="loading">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" :disabled="isEdit" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio value="1">男</el-radio>
          <el-radio value="2">女</el-radio>
          <el-radio value="0">未知</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
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
import { userApi } from '@/utils';
import { UserInfo } from '../../constant/model';

defineOptions({ name: 'UmAddEditDrawer' });

const emit = defineEmits<{ (e: 'saved'): void }>();

interface AddEditForm {
  userId: string;
  userName: string;
  password: string;
  gender: string;
  email: string;
  phone: string;
}

const visible = ref(false);
const loading = ref(false);
const submitting = ref(false);
const formRef = ref<FormInstance>();

const form = reactive<AddEditForm>({
  userId: '',
  userName: '',
  password: '',
  gender: '0',
  email: '',
  phone: '',
});

const isEdit = computed(() => !!form.userId);

const rules: FormRules<AddEditForm> = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const reset = () => {
  form.userId = '';
  form.userName = '';
  form.password = '';
  form.gender = '0';
  form.email = '';
  form.phone = '';
};

const handleOpen = (row?: UserInfo) => {
  reset();
  if (row) {
    form.userId = row.userId;
    form.userName = row.userName;
    form.gender = row.gender || '0';
    form.email = row.email || '';
    form.phone = row.phone || '';
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
      await userApi.updateProfile(form.userId, {
        email: form.email,
        phone: form.phone,
        gender: form.gender,
      });
      ElMessage.success('修改成功');
    } else {
      await userApi.register({
        userName: form.userName,
        password: form.password,
        email: form.email,
        phone: form.phone,
        gender: form.gender,
      });
      ElMessage.success('新增成功');
    }
    handleClose();
    visible.value = false;
    // 通知父组件刷新
    emit('saved');
  } finally {
    submitting.value = false;
  }
};

defineExpose({ handleOpen });
</script>
<style lang="less" scoped>
.um-add-edit-drawer {
}
</style>
