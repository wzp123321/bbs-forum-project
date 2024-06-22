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
          <el-input v-model="loginForm.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" placeholder="密码" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { LoginForm } from './model';
import { ElMessage } from 'element-plus';
import { encrypt } from 'zp-common-utils';

defineOptions({ name: 'BbsLogin' });

// 登录
const loginForm = reactive<LoginForm>({
  username: '',
  password: '',
});
/**
 * 登录
 */
const handleSubmit = () => {
  if (verifyForm()) {
    console.log(mapLoginParams());
  }
};
/**
 * 登录入参
 * @returns {LoginForm}
 */
const mapLoginParams = (): LoginForm => {
  const { username, password } = loginForm;
  return {
    username,
    password: encrypt(password),
  };
};
/**
 * 校验表单
 */
const verifyForm = () => {
  let isValid = true;
  let messages = [];
  if (!loginForm.username) {
    messages.push('请输入用户名');
  }
  if (!loginForm.password) {
    messages.push('请输入密码');
  }
  if (messages.length > 0) {
    ElMessage.error(messages[0]);
    isValid = false;
  }
  return isValid;
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
