<template>
  <div v-for="(item, index) in menus" :key="'menu_' + index">
    <el-menu-item
      v-if="!item.children || item.children.length === 0"
      :key="handleMenuKey(item.path)"
      :index="index + ''"
      :title="item.name"
      @click="onMenuLinkTo(item.path)"
    >
      <template #icon v-if="item.hasIcon">
        <div class="menu-icon">
          <i :class="['ems-iconfont', item.icons]"></i>
        </div>
      </template>
      {{ item.meta.title }}
    </el-menu-item>
    <!-- 子菜单偏移量 -->
    <el-sub-menu
      :key="'subMenu_' + item.path"
      popupClassName="custom-submenu"
      :popupOffset="[2, item.hasIcon ? 8 : 0]"
      :index="index + ''"
      v-else
    >
      <template #icon v-if="item.hasIcon">
        <div class="menu-icon">
          <i :class="['ems-iconfont', item.meta.icon]"></i>
        </div>
      </template>
      <template #title>{{ item.meta.title }}</template>
      <LayoutMenuItem :menuList="item.children"></LayoutMenuItem>
    </el-sub-menu>
  </div>
</template>
<script lang="ts">
import { useRouter } from 'vue-router';
import { defineComponent, computed, PropType } from 'vue';

export default defineComponent({
  name: 'LayoutMenuItem',
  props: {
    menuList: {
      type: Array as PropType<CommonMenu[]>,
      default: [],
    },
  },
  setup(props) {
    const menus = computed(() => {
      return props.menuList;
    });
    const router = useRouter();
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
    return {
      menus,
      router,
      onMenuLinkTo,
      handleMenuKey,
    };
  },
});
</script>
