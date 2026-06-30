<template>
  <div class="uc-list">
    <h3 class="uc-title">我的关注</h3>
    <el-empty v-if="!loading && !dataSource.length" description="还没有关注任何人" />
    <div v-else v-loading="loading" class="uc-items">
      <div v-for="u in dataSource" :key="u.userId" class="uc-item">
        <el-avatar :size="40" icon="UserFilled" />
        <div class="uc-item-info">
          <div class="uc-item-name">{{ u.userName || u.userId }}</div>
          <div class="uc-item-id">ID: {{ u.userId }}</div>
        </div>
        <el-button type="default" size="small" @click="onUnfollow(u)">取消关注</el-button>
      </div>
    </div>
    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="pageNum"
      :page-size="pageSize"
      background
      layout="total, prev, pager, next"
      :total="total"
      @current-change="fetchList"
    />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { usePagination } from '@bbs/core';
import { pageFollowingApi, cancelFollowApi } from '@/apis';
import { userStore } from '@/utils';
import type { UserInfoVO } from '@/apis/user';

defineOptions({ name: 'UcFollowing' });

const { pageNum, pageSize, total } = usePagination();
const dataSource = ref<UserInfoVO[]>([]);
const loading = ref(false);

const getMyId = () => userStore.getUserId();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageFollowingApi(getMyId(), pageNum.value, pageSize.value);
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const onUnfollow = async (u: UserInfoVO) => {
  try {
    await cancelFollowApi(u.userId);
    ElMessage.success('已取消关注');
    await fetchList();
  } catch {
    // ignore
  }
};

onMounted(fetchList);
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.uc-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.uc-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);
}

.uc-item-info {
  flex: 1;
}

.uc-item-name {
  font-weight: 600;
}

.uc-item-id {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
