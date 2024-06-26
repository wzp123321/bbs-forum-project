<template>
  <el-drawer
    class="um-view-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    title="详情"
    direction="rtl"
    :before-close="handleClose"
  >
    <section class="uvd-detail">
      <div class="uvd-detail-item">
        <el-avatar shape="circle" :size="100" fit="fill" :src="userDetail.avatar" />
      </div>
      <div class="uvd-detail-item">
        <label>姓名:</label>
        <span>{{ userDetail.username ?? '-' }}</span>
      </div>
    </section>
  </el-drawer>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { UserInfo } from '../../model';

defineOptions({
  name: 'UmViewDrawer',
});
// 开关
const visible = ref(false);
// 用户信息
const userDetail = reactive<UserInfo>({
  id: -1,
  username: '',
  sex: '',
  avatar: '',
});
/**
 * 打开
 * @param {UserInfo} userInfo
 */
const handleOpen = (userInfo: UserInfo) => {
  visible.value = true;
  userDetail.id = userInfo?.id;
  userDetail.username = userInfo?.username;
  userDetail.sex = userInfo?.sex;
  userDetail.avatar = userInfo?.avatar;
};
/**
 * 关闭
 */
const handleClose = () => {
  visible.value = false;
};

defineExpose({
  handleOpen,
});
</script>
<style lang="less" scoped>
.um-view-drawer {
  .el-drawer__body {
    .uvd-detail {
      display: flex;
      flex-direction: column;
      gap: var(--bbs-space-16);

      .uvd-detail-item {
        display: flex;
        align-items: center;
        gap: var(--bbs-space-8);
      }
    }
  }
}
</style>
