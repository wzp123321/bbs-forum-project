<script setup lang="ts">
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
</script>

<template>
<el-config-provider :locale="zhCn">
  <router-view v-slot="{ Component }">
    <transition name="view" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </el-config-provider>
</template>

<style lang="less" scoped>
/*
  进入和离开动画可以使用不同
  持续时间和速度曲线。
*/
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
