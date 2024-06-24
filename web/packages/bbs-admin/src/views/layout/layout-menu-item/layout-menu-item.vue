<template>
  <div v-for="(item, index) in props.menuList" :key="'menu_' + index">
    <el-menu-item
      v-if="!item.children || item.children.length === 0"
      :key="handleMenuKey(item.path)"
      :index="item.path"
      :title="item.name"
      @click="onMenuLinkTo(item.path)"
    >
      <component v-if="item.hasIcon" :is="mapMenuIcon(item.name)"></component>
      <span :title="item.meta.title">{{ item.meta.title }}</span>
    </el-menu-item>
    <!-- 子菜单偏移量 -->
    <el-sub-menu
      :key="'subMenu_' + item.path"
      popupClassName="custom-submenu"
      :popupOffset="[2, item.hasIcon ? 8 : 0]"
      :index="item.path"
      v-else
    >
      <template #title>
        <component v-if="item.hasIcon" :is="mapMenuIcon(item.name)"></component>
        <span :title="item.meta.title">{{ item.meta.title }}</span>
      </template>
      <LayoutMenuItem :menuList="item.children"></LayoutMenuItem>
    </el-sub-menu>
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from 'vue-router';
import { CommonMenu } from '@/service/model';
import { IconSystem } from '@arco-iconbox/vue-bbs-icon';
defineOptions({
  name: 'LayoutMenuItem',
  components: {
    IconSystem,
  },
});
// props
interface Props {
  menuList: CommonMenu[];
}
const props = withDefaults(defineProps<Props>(), {
  menuList: () => [],
});

const router = useRouter();
/**
 * 菜单图标
 * @param {string} name
 */
const mapMenuIcon = (name: string) => {
  let icon;
  switch (name) {
    case 'systemManage':
      icon = IconSystem;
      break;
  }
  return icon;
};
/**
 * 菜单点击
 * iframe unload事件 监听到了在触发后续
 * @param path
 */
const onMenuLinkTo = (path: string) => {
  router.push({
    path,
  });
};
// 处理菜单item key
const handleMenuKey = (url: string) => {
  return !url || url.indexOf('?') === -1 ? url : url.split('?')[0];
};
</script>
