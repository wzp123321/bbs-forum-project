<template>
  <div class="uc-pwd">
    <h3 class="uc-title">修改密码</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 480px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" show-password maxlength="32" show-word-limit />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirm">
        <el-input v-model="form.confirm" type="password" show-password maxlength="32" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { updatePasswordApi } from '@/apis/user';
import { userStore, tokenStore } from '@/utils';

defineOptions({ name: 'UcChangePassword' });

const router = useRouter();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const form = reactive({ oldPassword: '', newPassword: '', confirm: '' });

const validateConfirm = (_: unknown, v: string, cb: (e?: Error) => void) => {
  if (v !== form.newPassword) cb(new Error('两次密码不一致'));
  else cb();
};

const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32', trigger: 'blur' },
  ],
  confirm: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' },
  ],
};

const getMyId = () => userStore.getUserId();

const onSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    await updatePasswordApi(getMyId(), {
      oldPassword: form.oldPassword,
      newPassword: form.newPassword,
    });
    await ElMessageBox.confirm('密码修改成功,需要重新登录,确认退出?', '提示', {
      type: 'success',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).catch(() => null);
    tokenStore.clear();
    userStore.clear();
    router.replace('/login');
  } finally {
    submitting.value = false;
  }
};

const onReset = () => {
  formRef.value?.resetFields();
};
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}
</style>
