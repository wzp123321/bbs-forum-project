<template>
  <div class="register-page">
    <el-card class="register-card" shadow="always">
      <div class="register-title">BBS 论坛 · 注册</div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleSubmit">
        <el-form-item prop="userName">
          <el-input v-model="form.userName" placeholder="用户名 (3-20字符)" clearable maxlength="20" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="密码 (6-32字符)"
            type="password"
            show-password
            maxlength="32"
          />
        </el-form-item>
        <el-form-item prop="confirm">
          <el-input
            v-model="form.confirm"
            placeholder="确认密码"
            type="password"
            show-password
            maxlength="32"
            @keyup.enter="handleSubmit"
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱 (选填)" clearable maxlength="64" />
        </el-form-item>
        <el-button class="register-btn" type="primary" :loading="loading" @click="handleSubmit">注册</el-button>
        <div class="register-footer">
          已有账号?
          <el-link type="primary" :underline="false" @click="navigateToLogin">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { registerApi } from '@/apis/user';

defineOptions({ name: 'Register' });

const router = useRouter();
const route = useRoute();

const formRef = ref<FormInstance>();
const loading = ref(false);
const form = reactive({ userName: '', password: '', confirm: '', email: '' });

const validateConfirm = (_: unknown, value: string, callback: (err?: Error) => void) => {
  if (value !== form.password) callback(new Error('两次密码不一致'));
  else callback();
};

const rules: FormRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度 6-32', trigger: 'blur' },
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  loading.value = true;
  try {
    await registerApi({
      userName: form.userName.trim(),
      password: form.password,
      email: form.email || undefined,
    });
    ElMessage.success('注册成功,请登录');
    router.replace({ path: '/login', query: route.query });
  } catch (e) {
    // 拦截器已提示
  } finally {
    loading.value = false;
  }
};

const navigateToLogin = () => router.push({ path: '/login', query: route.query });
</script>

<style lang="less" scoped>
.register-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-light);

  .register-card {
    width: 420px;
    border-radius: 8px;
  }

  .register-title {
    font-size: 20px;
    font-weight: 600;
    text-align: center;
    margin-bottom: 24px;
  }

  .register-btn {
    width: 100%;
  }

  .register-footer {
    margin-top: 16px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
}
</style>
