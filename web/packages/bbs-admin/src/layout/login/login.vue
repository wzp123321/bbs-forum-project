<template>
  <!-- https://www.bilibili.com/read/readlist/rl340448 -->
  <div class="bbs-login">
    <el-carousel direction="vertical" indicator-position="none" :motion-blur="true" :interval="5000">
      <el-carousel-item v-for="item in mapLoginBgImages()" :key="item">
        <img :src="mapImageLoad(item)" alt="bg" />
      </el-carousel-item>
    </el-carousel>
    <!-- 表单 -->
    <section class="bl-form">
      <el-form :model="loginForm">
        <el-form-item>
          <el-input v-model="loginForm.username" />
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { LoginForm } from './model';

defineOptions({ name: 'BbsLogin' });
/**
 * 读取本地图片
 */
const mapLoginBgImages = () => {
  const images = import.meta.glob('../../assets/images/login/login-1/*.avif', { eager: true });
  return Object.keys(images);
};
/**
 * 加载图片
 * @param {string} path
 */
const mapImageLoad = (path: string) => {
  return new URL(path, import.meta.url).href;
};

// 登录
const loginForm = reactive<LoginForm>({
  username: '',
  password: '',
});
</script>

<style lang="less" scoped>
.bbs-login {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;

  :deep(.el-carousel) {
    width: 100%;
    height: 100%;

    > .el-carousel__container,
    > .el-carousel__container > .el-carousel__item > img {
      width: 100%;
      height: 100%;
    }

    > .el-carousel__container > .el-carousel__item > img {
      opacity: 0.85;
    }
  }

  .bl-form {
    width: 480px;
    height: 400px;

    position: absolute;
    top: 50%;
    right: 100px;
    transform: translateY(-50%);
  }
}
</style>
