<template>
  <div class="uc-list">
    <h3 class="uc-title">我的粉丝</h3>
    <el-empty v-if="!loading && !dataSource.length" description="还没有粉丝" />
    <div v-else v-loading="loading" class="uc-items">
      <div v-for="u in dataSource" :key="u.userId" class="uc-item">
        <el-avatar :size="40" icon="UserFilled" />
        <div class="uc-item-info">
          <div class="uc-item-name">{{ u.userName || u.userId }}</div>
          <div class="uc-item-id">ID: {{ u.userId }}</div>
        </div>
        <div class="uc-item-foot">
          <el-button
            v-if="!isSelf(u.userId)"
            :type="isFollowingMap[u.userId] ? 'default' : 'primary'"
            size="small"
            @click="onToggleFollow(u)"
          >
            {{ isFollowingMap[u.userId] ? '已关注' : '回关' }}
          </el-button>
        </div>
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
import { cancelFollowApi, followApi, followStatusApi, pageFollowersApi } from '@/apis';
import { userStore } from '@/utils';
import type { UserInfoVO } from '@/apis/user';

defineOptions({ name: 'UcFollowers' });

const { pageNum, pageSize, total } = usePagination();
const dataSource = ref<UserInfoVO[]>([]);
const isFollowingMap = ref<Record<string, boolean>>({});
const loading = ref(false);

const getMyId = () => userStore.getUserId();
const isSelf = (id: string) => id === getMyId();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageFollowersApi(getMyId(), pageNum.value, pageSize.value);
    dataSource.value = res.list;
    total.value = res.total;
    // 查询每个粉丝的互关状态
    await Promise.all(res.list.map((u: UserInfoVO) => fetchFollowStatus(u.userId)));
  } finally {
    loading.value = false;
  }
};

const fetchFollowStatus = async (uid: string) => {
  if (isSelf(uid)) return;
  try {
    const r = await followStatusApi(uid);
    isFollowingMap.value[uid] = r.following;
  } catch {
    isFollowingMap.value[uid] = false;
  }
};

const onToggleFollow = async (u: UserInfoVO) => {
  try {
    if (isFollowingMap.value[u.userId]) {
      await cancelFollowApi(u.userId);
      isFollowingMap.value[u.userId] = false;
      ElMessage.success('已取消关注');
    } else {
      await followApi(u.userId);
      isFollowingMap.value[u.userId] = true;
      ElMessage.success('关注成功');
    }
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
