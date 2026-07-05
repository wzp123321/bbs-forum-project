<template>
  <div class="bbs-login">
    <!-- 装饰背景 -->
    <div class="bl-bg">
      <div class="bl-blob bl-blob-1" />
      <div class="bl-blob bl-blob-2" />
      <div class="bl-blob bl-blob-3" />
    </div>

    <!-- 左侧品牌 -->
    <div class="bl-brand">
      <div class="bl-brand-logo">
        <span class="bl-brand-text">9527</span>
      </div>
      <h1 class="bl-brand-title">9527 管理后台</h1>
      <p class="bl-brand-sub">高效 · 智能 · 安全的社区运营平台</p>
      <ul class="bl-brand-list">
        <li><el-icon><DataAnalysis /></el-icon> 数据看板, 一目了然</li>
        <li><el-icon><SetUp /></el-icon> 内容审核, 高效处理</li>
        <li><el-icon><Safe /></el-icon> 权限管控, 安全可靠</li>
      </ul>
    </div>

    <!-- 右侧表单 -->
    <div class="bl-card">
      <h2 class="bl-title">账号登录</h2>
      <p class="bl-tip">使用管理员账号登录后台</p>

      <el-form :model="loginForm" @submit.prevent="submitLoginForm">
        <el-form-item>
          <el-input
            v-model="loginForm.account"
            placeholder="用户名 / 邮箱 / 手机号"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            placeholder="密码"
            size="large"
            type="password"
            show-password
            @keyup.enter="submitLoginForm"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            class="bl-btn"
            type="primary"
            size="large"
            :loading="loading"
            @click="submitLoginForm"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="bl-footer">
        <a href="/" target="_blank">前往论坛 →</a>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, DataAnalysis, SetUp, Safe } from '@element-plus/icons-vue';
import { loginApi } from '@/apis/auth';
import { tokenStore, USER_ID_KEY, USER_NAME_KEY } from '@/utils';

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

const submitLoginForm = async () => {
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
    const data = await loginApi({ account: loginForm.account, password: loginForm.password });
    tokenStore.set(data.token);
    sessionStorage.setItem(USER_ID_KEY, data.userId);
    sessionStorage.setItem(USER_NAME_KEY, data.nickName || data.userName);
    ElMessage.success('登录成功 🎉');
    router.push('/');
  } catch {
    // 业务错误已由 axios 拦截器统一弹窗
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="less" scoped>
.bbs-login {
  position: relative;
  display: flex;
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8559 50%, #FFB627 100%);
  overflow: hidden;
}

/* ============ 装饰背景 ============ */
.bl-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bl-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  background: rgba(255, 255, 255, 0.15);
}

.bl-blob-1 {
  width: 400px; height: 400px;
  top: -100px; left: -100px;
}

.bl-blob-2 {
  width: 320px; height: 320px;
  bottom: -80px; right: 10%;
  background: rgba(255, 214, 153, 0.4);
}

.bl-blob-3 {
  width: 200px; height: 200px;
  top: 30%; right: 30%;
  background: rgba(255, 255, 255, 0.1);
}

/* ============ 左侧品牌 ============ */
.bl-brand {
  flex: 1;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 60px 80px;
  color: #fff;
}

.bl-brand-logo {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(12px);
  border-radius: var(--bbs-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.bl-brand-text {
  color: #fff;
  font-size: 24px;
  font-weight: var(--bbs-font-weight-bold);
  letter-spacing: -0.5px;
}

.bl-brand-title {
  font-size: 40px;
  font-weight: var(--bbs-font-weight-bold);
  margin: 0 0 12px;
  letter-spacing: -1px;
  line-height: 1.2;
}

.bl-brand-sub {
  font-size: var(--bbs-font-size-lg);
  opacity: 0.92;
  margin: 0 0 32px;
}

.bl-brand-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  list-style: none;
  padding: 0;
  margin: 0;

  li {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: var(--bbs-font-size-base);
    opacity: 0.95;
  }

  .el-icon {
    width: 32px;
    height: 32px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: var(--bbs-radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

/* ============ 右侧卡片 ============ */
.bl-card {
  position: relative;
  z-index: 1;
  flex: 0 0 480px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin: 40px 40px 40px 0;
  padding: 48px 40px;
  background: #fff;
  border-radius: var(--bbs-radius-2xl);
  box-shadow: 0 24px 56px rgba(0, 0, 0, 0.15);
}

.bl-title {
  font-size: 28px;
  font-weight: var(--bbs-font-weight-bold);
  color: var(--bbs-text-color-primary);
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.bl-tip {
  font-size: var(--bbs-font-size-base);
  color: var(--bbs-text-color-secondary);
  margin: 0 0 32px;
}

.bl-btn {
  width: 100%;
  height: 44px;
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-semibold);
  letter-spacing: 4px;
  border-radius: var(--bbs-radius-md);
  background: var(--bbs-gradient-primary);
  border: none;
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(255, 107, 53, 0.4);
  }
}

.bl-footer {
  margin-top: 8px;
  text-align: center;
  font-size: var(--bbs-font-size-sm);

  a {
    color: var(--bbs-text-color-secondary);
    text-decoration: none;
    transition: color var(--bbs-transition-fast);

    &:hover {
      color: var(--bbs-color-primary);
    }
  }
}

/* 响应式: 窄屏隐藏左侧 */
@media (max-width: 900px) {
  .bl-brand {
    display: none;
  }
  .bl-card {
    flex: 1;
    margin: 0;
    border-radius: 0;
  }
}
</style>
