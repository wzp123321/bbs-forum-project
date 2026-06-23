<template>
  <!-- https://www.bilibili.com/read/readlist/rl340448 -->
  <div class="bbs-login">
    <!-- 表单 -->
    <section class="bl-form">
      <el-form :model="loginForm">
        <el-form-item>
          <h5>BBS管理后台</h5>
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.account" placeholder="用户名 / 邮箱 / 手机号" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            placeholder="密码"
            type="password"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { authApi, tokenStore, USER_ID_KEY, USER_NAME_KEY } from '@/utils';

defineOptions({ name: 'BbsLogin' });

interface LoginForm {
  account: string;
  password: string;
}

const router = useRouter();
const loading = ref(false);

const loginForm = reactive<LoginForm>({
  account: '',
  password: '',
});

const handleSubmit = async () => {
  if (!loginForm.account) {
    ElMessage.error('请输入账号');
    return;
  }
  if (!loginForm.password) {
    ElMessage.error('请输入密码');
    return;
  }
  loading.value = true;
  try {
    const data = await authApi.login({ account: loginForm.account, password: loginForm.password });
    tokenStore.set(data.token);
    sessionStorage.setItem(USER_ID_KEY, data.userId);
    sessionStorage.setItem(USER_NAME_KEY, data.nickName || data.userName);
    ElMessage.success('登录成功');
    router.push('/');
  } catch (e) {
    // 业务错误已由 axios 拦截器统一弹窗,这里只负责结束 loading
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="less" scoped>
.bbs-login {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url(../../assets/images/login/login-bg.avif);
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center;
  overflow: hidden;
  background-color: rgba(0, 0, 0, 0.85);

  > .bl-form {
    position: absolute;
    top: 50%;
    right: 80px;
    transform: translateY(-50%);
    background-color: rgba(255, 255, 255, 0.25);
    width: 480px;
    height: 400px;
    border-radius: 4px;
    overflow: hidden;

    display: flex;
    align-items: center;
    justify-content: center;

    :deep(.el-form) {
      .el-form-item {
        margin-bottom: 32px;
      }

      .el-form-item__content {
        text-align: center;

        .el-input,
        .el-button {
          width: 300px;
          height: 40px;
        }

        h5 {
          width: 100%;
          font-size: 24px;
          text-align: center;
          font-family: DINPro Bold;
        }
      }
    }
  }
}
</style>
