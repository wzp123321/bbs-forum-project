<template>
  <div class="admin-home">
    <!-- 欢迎横幅 -->
    <section class="ah-hero">
      <div class="ah-hero-bg">
        <div class="ah-hero-blob ah-hero-blob-1" />
        <div class="ah-hero-blob ah-hero-blob-2" />
      </div>
      <div class="ah-hero-content">
        <div class="ah-hero-greet">
          <span class="ah-hero-emoji">{{ greetEmoji }}</span>
          {{ greeting }}, {{ userName || 'admin' }} 👋
        </div>
        <h1 class="ah-hero-title">欢迎来到 9527 管理后台</h1>
        <p class="ah-hero-sub">今天是 {{ today }}, 祝你工作顺利, 社区繁荣 ✨</p>
        <div class="ah-hero-stats">
          <div class="ah-hero-stat">
            <div class="ah-hero-stat-num">{{ stats.userCount }}</div>
            <div class="ah-hero-stat-label">注册用户</div>
          </div>
          <div class="ah-hero-stat">
            <div class="ah-hero-stat-num">{{ stats.postCount }}</div>
            <div class="ah-hero-stat-label">帖子总数</div>
          </div>
          <div class="ah-hero-stat">
            <div class="ah-hero-stat-num">{{ stats.todayNew }}</div>
            <div class="ah-hero-stat-label">今日新增</div>
          </div>
          <div class="ah-hero-stat">
            <div class="ah-hero-stat-num">{{ stats.pendingReport }}</div>
            <div class="ah-hero-stat-label">待处理举报</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 快捷入口 -->
    <section class="ah-section">
      <div class="ah-section-head">
        <h3 class="ah-section-title">快捷入口</h3>
        <span class="ah-section-sub">常用操作, 一键直达</span>
      </div>
      <div class="ah-quick">
        <div
          v-for="item in quickEntries"
          :key="item.path"
          class="ah-quick-item"
          :style="{ '--quick-color': item.color }"
          @click="goTo(item.path)"
        >
          <div class="ah-quick-icon" :style="{ background: item.gradient }">
            <el-icon :size="22"><component :is="item.icon" /></el-icon>
          </div>
          <div class="ah-quick-info">
            <div class="ah-quick-name">{{ item.name }}</div>
            <div class="ah-quick-desc">{{ item.desc }}</div>
          </div>
          <el-icon class="ah-quick-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- 双列: 待办 + 数据 -->
    <el-row :gutter="20" class="ah-row">
      <el-col :span="14">
        <section class="ah-section">
          <div class="ah-section-head">
            <h3 class="ah-section-title">待办事项</h3>
            <span class="ah-section-badge">{{ todoList.length }} 项</span>
          </div>
          <div class="ah-todo">
            <div
              v-for="item in todoList"
              :key="item.id"
              class="ah-todo-item"
              :class="`ah-todo-${item.type}`"
            >
              <div class="ah-todo-icon">
                <el-icon :size="18"><component :is="item.icon" /></el-icon>
              </div>
              <div class="ah-todo-main">
                <div class="ah-todo-title">{{ item.title }}</div>
                <div class="ah-todo-desc">{{ item.desc }}</div>
              </div>
              <el-button type="primary" size="small" round @click="goTo(item.path)">
                去处理
              </el-button>
            </div>
            <div v-if="!todoList.length" class="ah-todo-empty">
              <el-icon :size="48"><CircleCheckFilled /></el-icon>
              <p>太棒了, 全部处理完毕 🎉</p>
            </div>
          </div>
        </section>
      </el-col>

      <el-col :span="10">
        <section class="ah-section">
          <div class="ah-section-head">
            <h3 class="ah-section-title">最近活动</h3>
          </div>
          <div class="ah-activity">
            <div v-for="item in activityList" :key="item.id" class="ah-activity-item">
              <div class="ah-activity-dot" :style="{ background: item.color }" />
              <div class="ah-activity-main">
                <div class="ah-activity-text">{{ item.text }}</div>
                <div class="ah-activity-time">{{ item.time }}</div>
              </div>
            </div>
          </div>
        </section>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import {
  User, Document, ChatDotRound, Star, FolderOpened, PriceTag,
  EditPen, QuestionFilled, Warning, Bell, Setting, ArrowRight, CircleCheckFilled,
} from '@element-plus/icons-vue';
import { pagePostsApi } from '@/apis/post';
import { pageUsersApi } from '@/apis/user';
import { pageReportsApi } from '@/apis/report';

defineOptions({ name: 'HomePage' });

const router = useRouter();
const userName = ref(sessionStorage.getItem('bbs_user_name') || 'admin');
const today = new Date().toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' });

const stats = ref({
  userCount: 0,
  postCount: 0,
  todayNew: 0,
  pendingReport: 0,
});

const todoList = ref<Array<{ id: number; type: string; title: string; desc: string; icon: any; path: string }>>([]);
const activityList = ref<Array<{ id: number; text: string; time: string; color: string }>>([]);

const greeting = (() => {
  const h = new Date().getHours();
  if (h < 6) return '夜深了';
  if (h < 12) return '早上好';
  if (h < 14) return '中午好';
  if (h < 18) return '下午好';
  return '晚上好';
})();

const greetEmoji = (() => {
  const h = new Date().getHours();
  if (h < 6) return '🌙';
  if (h < 12) return '☀️';
  if (h < 18) return '🌤️';
  return '🌆';
})();

const quickEntries = [
  { name: '帖子管理', desc: '查看与编辑所有帖子', path: '/post', icon: Document, color: '#FF6B35', gradient: 'linear-gradient(135deg, #FF6B35, #FF8559)' },
  { name: '用户管理', desc: '管理注册用户', path: '/user', icon: User, color: '#3B82F6', gradient: 'linear-gradient(135deg, #3B82F6, #60A5FA)' },
  { name: '评论管理', desc: '审核与删除评论', path: '/comment', icon: ChatDotRound, color: '#22C55E', gradient: 'linear-gradient(135deg, #22C55E, #4ADE80)' },
  { name: '分类管理', desc: '维护板块分类', path: '/category', icon: FolderOpened, color: '#A855F7', gradient: 'linear-gradient(135deg, #A855F7, #C084FC)' },
  { name: '标签管理', desc: '管理内容标签', path: '/tag', icon: PriceTag, color: '#EC4899', gradient: 'linear-gradient(135deg, #EC4899, #F472B6)' },
  { name: '反馈处理', desc: '处理用户反馈', path: '/feedback', icon: QuestionFilled, color: '#F59E0B', gradient: 'linear-gradient(135deg, #F59E0B, #FCD34D)' },
];

const goTo = (path: string) => router.push(path);

const loadStats = async () => {
  try {
    const [postRes, userRes, reportRes] = await Promise.allSettled([
      pagePostsApi({ pageNum: 1, pageSize: 1, status: undefined as any }),
      pageUsersApi({ pageNum: 1, pageSize: 1 }),
      pageReportsApi({ pageNum: 1, pageSize: 1, status: 0 }),
    ]);
    stats.value.postCount = postRes.status === 'fulfilled' ? postRes.value.total : 0;
    stats.value.userCount = userRes.status === 'fulfilled' ? userRes.value.total : 0;
    stats.value.pendingReport = reportRes.status === 'fulfilled' ? reportRes.value.total : 0;
  } catch {
    ElMessage.warning('部分数据加载失败, 请稍后重试');
  }
};

const buildTodos = () => {
  todoList.value = [
    { id: 1, type: 'danger', title: '处理举报', desc: `${stats.value.pendingReport} 条举报待处理`, icon: Warning, path: '/report' },
    { id: 2, type: 'warning', title: '回复反馈', desc: '用户反馈等待回复', icon: Bell, path: '/feedback' },
    { id: 3, type: 'info', title: '内容审核', desc: '审核新发布内容', icon: EditPen, path: '/post' },
  ].filter(item => item.type !== 'danger' || stats.value.pendingReport > 0);
};

const buildActivities = () => {
  activityList.value = [
    { id: 1, text: '系统初始化完成', time: '刚刚', color: '#22C55E' },
    { id: 2, text: '数据库已就绪', time: '1 分钟前', color: '#3B82F6' },
    { id: 3, text: '管理员账号已创建', time: '5 分钟前', color: '#FF6B35' },
    { id: 4, text: '示例数据已灌入', time: '10 分钟前', color: '#A855F7' },
  ];
};

onMounted(async () => {
  await loadStats();
  buildTodos();
  buildActivities();
});
</script>

<style lang="less" scoped>
.admin-home {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ============ Hero 横幅 ============ */
.ah-hero {
  position: relative;
  border-radius: var(--bbs-radius-2xl);
  background: linear-gradient(135deg, #FF6B35 0%, #FF8559 50%, #FFB627 100%);
  padding: 32px 36px;
  color: #fff;
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(255, 107, 53, 0.2);
}

.ah-hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.ah-hero-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
}

.ah-hero-blob-1 {
  width: 200px; height: 200px;
  top: -40px; right: 10%;
  background: rgba(255, 214, 153, 0.4);
}

.ah-hero-blob-2 {
  width: 160px; height: 160px;
  bottom: -40px; left: 30%;
  background: rgba(255, 182, 39, 0.4);
}

.ah-hero-content {
  position: relative;
  z-index: 1;
}

.ah-hero-greet {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: var(--bbs-radius-full);
  font-size: var(--bbs-font-size-sm);
  font-weight: var(--bbs-font-weight-medium);
  margin-bottom: 12px;
}

.ah-hero-emoji {
  font-size: 16px;
}

.ah-hero-title {
  font-size: 28px;
  font-weight: var(--bbs-font-weight-bold);
  margin: 0 0 6px;
  letter-spacing: -0.5px;
}

.ah-hero-sub {
  font-size: var(--bbs-font-size-base);
  opacity: 0.9;
  margin: 0 0 24px;
}

.ah-hero-stats {
  display: flex;
  gap: 32px;
  flex-wrap: wrap;
}

.ah-hero-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ah-hero-stat-num {
  font-size: 28px;
  font-weight: var(--bbs-font-weight-bold);
  letter-spacing: -0.5px;
  line-height: 1.1;
}

.ah-hero-stat-label {
  font-size: var(--bbs-font-size-sm);
  opacity: 0.85;
}

/* ============ 通用 Section ============ */
.ah-section {
  background: #fff;
  border-radius: var(--bbs-radius-lg);
  padding: 24px;
  box-shadow: var(--bbs-shadow-xs);
}

.ah-row {
  margin: 0 !important;
}

.ah-section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.ah-section-title {
  font-size: var(--bbs-font-size-lg);
  font-weight: var(--bbs-font-weight-semibold);
  color: var(--bbs-text-color-primary);
  margin: 0;
}

.ah-section-sub {
  font-size: var(--bbs-font-size-sm);
  color: var(--bbs-text-color-secondary);
}

.ah-section-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  background: var(--bbs-color-primary-light);
  color: var(--bbs-color-primary-active);
  border-radius: var(--bbs-radius-full);
  font-size: var(--bbs-font-size-xs);
  font-weight: var(--bbs-font-weight-medium);
}

/* ============ 快捷入口 ============ */
.ah-quick {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.ah-quick-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--bbs-bg-color-secondary);
  border-radius: var(--bbs-radius-md);
  cursor: pointer;
  transition: all var(--bbs-transition-base);
  position: relative;

  &:hover {
    background: #fff;
    transform: translateY(-2px);
    box-shadow: var(--bbs-shadow-md);

    .ah-quick-arrow {
      transform: translateX(4px);
      color: var(--quick-color);
    }
  }
}

.ah-quick-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--bbs-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.ah-quick-info {
  flex: 1;
  min-width: 0;
}

.ah-quick-name {
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-semibold);
  color: var(--bbs-text-color-primary);
  margin-bottom: 2px;
}

.ah-quick-desc {
  font-size: var(--bbs-font-size-xs);
  color: var(--bbs-text-color-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ah-quick-arrow {
  color: var(--bbs-text-color-placeholder);
  transition: all var(--bbs-transition-base);
  flex-shrink: 0;
}

/* ============ 待办 ============ */
.ah-todo {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ah-todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--bbs-radius-md);
  border: 1px solid var(--bbs-border-color-light);
  background: var(--bbs-bg-color-page-soft);
  transition: all var(--bbs-transition-fast);

  &:hover {
    border-color: var(--bbs-color-primary-border);
    background: var(--bbs-color-primary-lighter);
  }
}

.ah-todo-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--bbs-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #fff;
}

.ah-todo-danger .ah-todo-icon {
  background: linear-gradient(135deg, #EF4444, #F87171);
}

.ah-todo-warning .ah-todo-icon {
  background: linear-gradient(135deg, #F59E0B, #FCD34D);
}

.ah-todo-info .ah-todo-icon {
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
}

.ah-todo-main {
  flex: 1;
  min-width: 0;
}

.ah-todo-title {
  font-size: var(--bbs-font-size-base);
  font-weight: var(--bbs-font-weight-semibold);
  color: var(--bbs-text-color-primary);
  margin-bottom: 2px;
}

.ah-todo-desc {
  font-size: var(--bbs-font-size-sm);
  color: var(--bbs-text-color-secondary);
}

.ah-todo-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 0;
  color: var(--bbs-color-success);
  text-align: center;

  p {
    margin: 0;
    font-size: var(--bbs-font-size-base);
    color: var(--bbs-text-color-secondary);
  }
}

/* ============ 活动 ============ */
.ah-activity {
  display: flex;
  flex-direction: column;
  gap: 0;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 5px;
    top: 8px;
    bottom: 8px;
    width: 2px;
    background: var(--bbs-border-color);
  }
}

.ah-activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 0;
  position: relative;
}

.ah-activity-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-top: 4px;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px var(--bbs-border-color);
  z-index: 1;
}

.ah-activity-main {
  flex: 1;
}

.ah-activity-text {
  font-size: var(--bbs-font-size-base);
  color: var(--bbs-text-color-primary);
  font-weight: var(--bbs-font-weight-medium);
  margin-bottom: 2px;
}

.ah-activity-time {
  font-size: var(--bbs-font-size-xs);
  color: var(--bbs-text-color-placeholder);
}
</style>
