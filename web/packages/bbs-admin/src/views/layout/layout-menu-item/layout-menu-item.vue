<template>
  <template v-for="item in props.menuList" :key="item.index">
    <el-menu-item
      v-if="!item.children || item?.children.length === 0"
      :key="item?.index"
      :index="item?.index"
      :title="item?.name"
      @click.native="onMenuLinkTo(item?.path)"
    >
      <component v-if="item?.hasIcon" :is="mapMenuIcon(item?.name)"></component>
      <span :title="item?.meta?.title">{{ item?.meta?.title }}</span>
    </el-menu-item>
    <!-- 子菜单偏移量 -->
    <el-sub-menu :key="item.index" :index="item.index" v-else>
      <template #title>
        <component v-if="item?.hasIcon" :is="mapMenuIcon(item?.name)"></component>
        <span :title="item?.meta?.title">{{ item?.meta?.title }}</span>
      </template>
      <LayoutMenuItem :menuList="item?.children"></LayoutMenuItem>
    </el-sub-menu>
  </template>
</template>
<script lang="ts" setup>
import { useRouter } from 'vue-router';
import { CommonMenu } from '@/service/model';
import { IconSystem, IconUser } from '@arco-iconbox/vue-bbs-icon';
defineOptions({
  name: 'LayoutMenuItem',
  components: {
    IconSystem,
    IconUser,
  },
});
// props
interface Props {
  menuList: CommonMenu[];
}
const props = withDefaults(defineProps<Props>(), {
  menuList: () => [],
});
// router
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
    case 'userManage':
      icon = IconUser;
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
</script>
