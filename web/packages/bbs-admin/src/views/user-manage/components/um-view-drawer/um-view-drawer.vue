<template>
  <el-drawer
    class="um-view-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    title="详情"
    direction="rtl"
    :before-close="close"
  >
    <section class="uvd-detail" v-loading="loading">
      <div class="uvd-detail-item">
        <label>用户ID:</label>
        <span>{{ userDetail.userId ?? '-' }}</span>
      </div>
      <div class="uvd-detail-item">
        <label>用户名:</label>
        <span>{{ userDetail.userName ?? '-' }}</span>
      </div>
      <div class="uvd-detail-item">
        <label>性别:</label>
        <span>{{ getGenderText(userDetail.gender) }}</span>
      </div>
      <div class="uvd-detail-item">
        <label>邮箱:</label>
        <span>{{ userDetail.email ?? '-' }}</span>
      </div>
      <div class="uvd-detail-item">
        <label>手机:</label>
        <span>{{ userDetail.phone ?? '-' }}</span>
      </div>
      <div class="uvd-detail-item">
        <label>注册时间:</label>
        <span>{{ userDetail.createTime ?? '-' }}</span>
      </div>
    </section>
  </el-drawer>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { UserInfo } from '../../constant/model';
import { getUserApi } from '@/apis/user';

defineOptions({
  name: 'UmViewDrawer',
});
// 开关
const visible = ref(false);
const loading = ref(false);
// 用户信息
const userDetail = reactive<UserInfo>({
  userId: '',
  userName: '',
  gender: '',
  email: '',
  phone: '',
  createTime: '',
});

/** 0未知 1男 2女 */
const getGenderText = (g?: string) => (g === '1' ? '男' : g === '2' ? '女' : '未知');

/**
 * 打开
 * @param {UserInfo} userInfo
 */
const open = async (userInfo: UserInfo) => {
  visible.value = true;
  // 拉取最新详情
  loading.value = true;
  try {
    const detail = await getUserApi(userInfo.userId);
    Object.assign(userDetail, detail);
  } finally {
    loading.value = false;
  }
};
/**
 * 关闭
 */
const close = () => {
  visible.value = false;
};

defineExpose({
  open,
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
        label {
          width: 90px;
          color: var(--bbs-text-color-secondary);
        }
      }
    }
  }
}
</style>
