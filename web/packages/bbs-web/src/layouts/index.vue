<template>
  <div class="home">
    <LayoutHeader></LayoutHeader>
    <LayoutBody></LayoutBody>
    <ChatPanel></ChatPanel>
  </div>
</template>
<script lang="ts" setup>
import LayoutHeader from './layout-header/layout-header.vue';
import LayoutBody from './layout-body/layout-body.vue';
import ChatPanel from '@/components/chat-panel/chat-panel.vue';

defineOptions({
  name: 'Home',
});
</script>
<style lang="less" scoped>
.home {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
