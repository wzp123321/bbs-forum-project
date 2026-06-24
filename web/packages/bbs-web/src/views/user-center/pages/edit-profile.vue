<template>
  <div class="uc-edit">
    <h3 class="uc-title">编辑资料</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 480px">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" maxlength="20" show-word-limit />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio value="M">男</el-radio>
          <el-radio value="F">女</el-radio>
          <el-radio value="">保密</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" maxlength="64" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" maxlength="20" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { inject } from 'vue';
import { userApi } from '@/apis/user';
import { userStore } from '@/utils';

defineOptions({ name: 'UcEditProfile' });

const refresh = inject<() => Promise<void>>('refreshUserCenter');

const formRef = ref<FormInstance>();
const submitting = ref(false);
const form = reactive({
  userName: '',
  gender: '' as '' | 'M' | 'F',
  email: '',
  phone: '',
});

const rules: FormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度 3-20', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
};

const myId = () => userStore.getUserId();

const load = async () => {
  const id = myId();
  if (!id) return;
  try {
    const u = await userApi.getUser(id);
    form.userName = u.userName || '';
    form.gender = (u.gender as '' | 'M' | 'F') || '';
    form.email = u.email || '';
    form.phone = u.phone || '';
  } catch {
    // ignore
  }
};

const onSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    await userApi.updateProfile(myId(), {
      userName: form.userName.trim(),
      gender: form.gender || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
    });
    ElMessage.success('保存成功');
    // 同步 userName 到本地
    userStore.setUser(myId(), form.userName.trim() || myId());
    if (refresh) await refresh();
  } finally {
    submitting.value = false;
  }
};

const onReset = () => {
  formRef.value?.resetFields();
  load();
};

onMounted(load);
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
</style>
