<template>
  <div class="login-page">
    <!-- 左侧: 品牌展示 -->
    <div class="login-brand">
      <div class="login-brand-bg">
        <div class="login-brand-blob login-brand-blob-1" />
        <div class="login-brand-blob login-brand-blob-2" />
        <div class="login-brand-blob login-brand-blob-3" />
      </div>
      <div class="login-brand-content">
        <div class="login-brand-logo">
          <span class="login-brand-text">9527</span>
        </div>
        <h1 class="login-brand-title">欢迎回来 ✨</h1>
        <p class="login-brand-sub">
          分享技术、记录生活、交流想法<br />
          9527 论坛, 属于开发者的温暖社区
        </p>
        <ul class="login-brand-features">
          <li>
            <el-icon><ChatDotRound /></el-icon>
            <span>实时讨论, 精彩碰撞</span>
          </li>
          <li>
            <el-icon><Collection /></el-icon>
            <span>沉淀知识, 收藏灵感</span>
          </li>
          <li>
            <el-icon><User /></el-icon>
            <span>遇见有趣的人</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- 右侧: 表单 -->
    <div class="login-form-wrap">
      <div class="login-form-inner">
        <div class="login-header">
          <h2 class="login-title">账号登录</h2>
          <p class="login-tip">输入账号密码, 开始你的社区之旅</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleSubmit">
          <el-form-item prop="account">
            <el-input
              v-model="form.account"
              placeholder="用户名 / 邮箱 / 手机号"
              clearable
            >
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
          <el-button
            class="login-btn"
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="login-divider">
          <span>其他登录方式</span>
        </div>
        <div class="login-third">
          <button class="login-third-btn" title="微信登录">
            <el-icon :size="20"><ChatDotRound /></el-icon>
          </button>
          <button class="login-third-btn" title="GitHub 登录">
            <el-icon :size="20"><Link /></el-icon>
          </button>
          <button class="login-third-btn" title="手机号登录">
            <el-icon :size="20"><Iphone /></el-icon>
          </button>
        </div>

        <div class="login-footer">
          还没有账号?
          <el-link type="primary" :underline="false" @click="navigateToRegister">立即注册</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { User, Lock, ChatDotRound, Collection, Link, Iphone } from '@element-plus/icons-vue';
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
    ElMessage.success(`欢迎回来, ${vo.userName || vo.userId} ✨`);
    navigateToRedirect();
  } catch {
    // 拦截器已提示
  } finally {
    loading.value = false;
  }
};

const navigateToRegister = () => router.push({ path: '/register', query: route.query });
</script>

<style lang="less" scoped>
.login-page {
  display: flex;
  width: 100%;
  min-height: calc(100vh - 64px);
  background: var(--bbs-bg-color-page);
}

/* ============ 左侧品牌区 ============ */
.login-brand {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8559 50%, #FFB627 100%);
  color: #fff;
  overflow: hidden;
  padding: 60px 40px;
}

.login-brand-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.login-brand-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
}

.login-brand-blob-1 {
  width: 320px; height: 320px;
  top: -80px; right: -80px;
  background: rgba(255, 214, 153, 0.5);
}

.login-brand-blob-2 {
  width: 240px; height: 240px;
  bottom: -60px; left: -60px;
  background: rgba(255, 182, 39, 0.4);
}

.login-brand-blob-3 {
  width: 180px; height: 180px;
  top: 40%; right: 30%;
  background: rgba(255, 255, 255, 0.2);
}

.login-brand-content {
  position: relative;
  z-index: 1;
  max-width: 420px;
}

.login-brand-logo {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(12px);
  border-radius: var(--bbs-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.login-brand-text {
  color: #fff;
  font-size: 22px;
  font-weight: var(--bbs-font-weight-bold);
  letter-spacing: -0.5px;
}

.login-brand-title {
  font-size: 36px;
  font-weight: var(--bbs-font-weight-bold);
  margin: 0 0 12px;
  letter-spacing: -1px;
  line-height: 1.2;
}

.login-brand-sub {
  font-size: var(--bbs-font-size-base);
  opacity: 0.92;
  line-height: var(--bbs-line-height-relaxed);
  margin: 0 0 32px;
}

.login-brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;

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
    flex-shrink: 0;
  }
}

/* ============ 右侧表单区 ============ */
.login-form-wrap {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  background: #fff;
}

.login-form-inner {
  width: 100%;
  max-width: 360px;
}

.login-header {
  margin-bottom: 32px;
}

.login-title {
  font-size: 28px;
  font-weight: var(--bbs-font-weight-bold);
  color: var(--bbs-text-color-primary);
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.login-tip {
  font-size: var(--bbs-font-size-base);
  color: var(--bbs-text-color-secondary);
  margin: 0;
}

.login-btn {
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

.login-divider {
  display: flex;
  align-items: center;
  margin: 28px 0 16px;
  color: var(--bbs-text-color-placeholder);
  font-size: var(--bbs-font-size-xs);

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: var(--bbs-border-color);
  }

  span {
    padding: 0 16px;
  }
}

.login-third {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.login-third-btn {
  width: 44px;
  height: 44px;
  background: var(--bbs-bg-color-secondary);
  border: none;
  border-radius: var(--bbs-radius-md);
  color: var(--bbs-text-color-secondary);
  cursor: pointer;
  transition: all var(--bbs-transition-base);
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: var(--bbs-color-primary-light);
    color: var(--bbs-color-primary);
    transform: translateY(-2px);
  }
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  color: var(--bbs-text-color-secondary);
  font-size: var(--bbs-font-size-sm);
}

.login-footer :deep(.el-link) {
  font-weight: var(--bbs-font-weight-semibold);
}

/* 响应式: 窄屏隐藏左侧 */
@media (max-width: 900px) {
  .login-brand {
    display: none;
  }
  .login-form-wrap {
    flex: 1;
  }
}
</style>
