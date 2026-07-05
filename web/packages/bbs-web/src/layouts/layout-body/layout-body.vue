<template>
  <main class="layout-body">
    <div class="lb-container">
      <router-view v-slot="{ Component }">
        <transition name="bbs-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </main>
</template>
<script lang="ts" setup>
defineOptions({
  name: 'LayoutBody',
});
</script>
<style lang="less" scoped>
.layout-body {
  flex: 1;
  padding: 24px 0 48px;
  background: var(--bbs-bg-color-page);
  overflow-y: auto;
}

.lb-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: calc(100vh - 64px - 48px);
}
</style>

<style lang="less">
/* 全局路由切换动画 */
.bbs-fade-enter-active,
.bbs-fade-leave-active {
  transition: all 200ms cubic-bezier(0.16, 1, 0.3, 1);
}

.bbs-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.bbs-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
