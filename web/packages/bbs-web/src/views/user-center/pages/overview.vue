<template>
  <div class="uc-overview">
    <h3 class="uc-title">个人概览</h3>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户名">{{ user?.userName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ user?.userId || userStore.getUserId() }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ user?.gender || '未设置' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ user?.email || '未设置' }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ user?.phone || '未设置' }}</el-descriptions-item>
      <el-descriptions-item label="注册时间">{{ user?.createTime || '-' }}</el-descriptions-item>
    </el-descriptions>

    <div class="uc-stats">
      <el-card shadow="never" class="uc-stat" @click="$router.push('/user-center/posts')">
        <div class="uc-stat-num">{{ totalPosts }}</div>
        <div class="uc-stat-label">我的帖子</div>
      </el-card>
      <el-card shadow="never" class="uc-stat" @click="$router.push('/user-center/collects')">
        <div class="uc-stat-num">{{ totalCollected }}</div>
        <div class="uc-stat-label">我的收藏</div>
      </el-card>
      <el-card shadow="never" class="uc-stat" @click="$router.push('/user-center/likes')">
        <div class="uc-stat-num">{{ totalLiked }}</div>
        <div class="uc-stat-label">我的赞</div>
      </el-card>
      <el-card shadow="never" class="uc-stat">
        <div class="uc-stat-num">{{ counts.followers }}</div>
        <div class="uc-stat-label">粉丝</div>
      </el-card>
      <el-card shadow="never" class="uc-stat">
        <div class="uc-stat-num">{{ counts.following }}</div>
        <div class="uc-stat-label">关注</div>
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import { myPostsApi, myCollectedApi, myLikedApi } from '@/apis/user';
import { userStore } from '@/utils';
import type { UserInfoVO, FollowCountVO } from '@/apis/user';

defineOptions({ name: 'UcOverview' });

const props = defineProps<{
  user?: UserInfoVO | null;
  counts: FollowCountVO;
}>();

const totalPosts = ref(0);
const totalCollected = ref(0);
const totalLiked = ref(0);

const loadStats = async () => {
  try {
    const [a, b, c] = await Promise.all([
      myPostsApi({ pageNum: 1, pageSize: 1 }),
      myCollectedApi({ pageNum: 1, pageSize: 1 }),
      myLikedApi({ pageNum: 1, pageSize: 1 }),
    ]);
    totalPosts.value = a.total;
    totalCollected.value = b.total;
    totalLiked.value = c.total;
  } catch {
    // ignore
  }
};

onMounted(loadStats);
</script>

<style lang="less" scoped>
.uc-overview {
  .uc-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 16px;
  }
}

.uc-stats {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.uc-stat {
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
  &:hover {
    transform: translateY(-2px);
  }
}

.uc-stat-num {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-color-primary);
}

.uc-stat-label {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
