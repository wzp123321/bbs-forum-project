<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <div class="login-title">BBS 论坛 · 登录</div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleSubmit">
        <el-form-item prop="account">
          <el-input v-model="form.account" placeholder="用户名 / 邮箱 / 手机号" clearable>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="密码"
            type="password"
            show-password
            @keyup.enter="handleSubmit"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-button class="login-btn" type="primary" :loading="loading" @click="handleSubmit">登录</el-button>
        <div class="login-footer">
          还没有账号?
          <el-link type="primary" :underline="false" @click="navigateToRegister">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { loginApi } from '@/apis/auth';
import { tokenStore, userStore } from '@/utils';

defineOptions({ name: 'Login' });

const router = useRouter();
const route = useRoute();

const formRef = ref<FormInstance>();
const loading = ref(false);
const form = reactive({ account: '', password: '' });
const rules: FormRules = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};

const navigateToRedirect = () => {
  const back = (route.query.redirect as string) || '/';
  router.replace(back);
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
    const vo = await loginApi({ account: form.account.trim(), password: form.password });
    tokenStore.set(vo.token);
    userStore.setUser(vo.userId, vo.userName || vo.nickName || vo.userId);
    ElMessage.success(`欢迎回来,${vo.userName || vo.userId}`);
    navigateToRedirect();
  } catch (e) {
    // 拦截器已提示
  } finally {
    loading.value = false;
  }
};

const navigateToRegister = () => router.push({ path: '/register', query: route.query });
</script>

<style lang="less" scoped>
.login-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-light);

  .login-card {
    width: 380px;
    border-radius: 8px;
  }

  .login-title {
    font-size: 20px;
    font-weight: 600;
    text-align: center;
    margin-bottom: 24px;
  }

  .login-btn {
    width: 100%;
  }

  .login-footer {
    margin-top: 16px;
    text-align: center;
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
}
</style>
