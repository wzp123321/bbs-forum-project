<template>
  <div class="web-post-detail" v-loading="loading">
    <template v-if="post">
      <article class="wpd-main">
        <h1 class="wpd-title">
          <el-tag v-if="post.isTop === 1" type="danger" size="small" effect="dark">置顶</el-tag>
          <el-tag v-if="post.isEssence === 1" type="warning" size="small" effect="dark">精华</el-tag>
          {{ post.title }}
        </h1>
        <div class="wpd-meta">
          <span>
            <el-icon><User /></el-icon>
            {{ post.userName || post.userId || '匿名' }}
          </span>
          <span>
            <el-icon><Clock /></el-icon>
            {{ post.createTime }}
          </span>
          <span>
            <el-icon><View /></el-icon>
            {{ post.viewCount ?? 0 }}
          </span>
          <span>
            <el-icon><ChatDotRound /></el-icon>
            {{ post.commentCount ?? 0 }}
          </span>
        </div>
        <div class="wpd-tags" v-if="post.tagNames?.length">
          <el-tag v-for="t in post.tagNames" :key="t" size="small" effect="plain"># {{ t }}</el-tag>
        </div>
        <div class="wpd-content">{{ post.content }}</div>

        <div class="wpd-actions">
          <el-button :type="liked ? 'primary' : 'default'" @click="onToggleLike" :loading="likeLoading">
            <el-icon><Star /></el-icon>
            {{ post.likeCount ?? 0 }}
          </el-button>
          <el-button :type="collected ? 'warning' : 'default'" @click="onToggleCollect" :loading="collectLoading">
            <el-icon><Collection /></el-icon>
            {{ post.collectCount ?? 0 }}
          </el-button>
        </div>
      </article>

      <section class="wpd-comments">
        <h3 class="wpd-comments-title">评论 ({{ total }})</h3>

        <div class="wpd-comments-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="说点什么吧..."
            maxlength="1000"
            show-word-limit
          />
          <div class="wpd-comments-input-foot">
            <el-button type="primary" :loading="commentSubmitting" @click="onSubmitComment">发表</el-button>
          </div>
        </div>

        <el-empty v-if="!loading && !dataSource.length" description="还没有评论,快来抢沙发" />

        <ul v-else class="wpd-comment-list">
          <li v-for="c in dataSource" :key="c.id" class="wpd-comment">
            <div class="wpd-comment-head">
              <el-avatar :size="32" icon="UserFilled" />
              <div class="wpd-comment-info">
                <div class="wpd-comment-user">{{ c.userName || c.userId || '匿名' }}</div>
                <div class="wpd-comment-time">{{ c.createTime }}</div>
              </div>
            </div>
            <div class="wpd-comment-body">
              <template v-if="c.replyToUserName">
                回复
                <span class="wpd-comment-reply">@{{ c.replyToUserName }}</span>
                :
              </template>
              {{ c.content }}
            </div>
            <div class="wpd-comment-foot">
              <el-button link size="small" @click="onReplyStart(c)">回复</el-button>
              <el-button v-if="c.userId === currentUserId" link size="small" type="danger" @click="onDeleteComment(c)">
                删除
              </el-button>
            </div>
            <div v-if="replyTo?.id === c.id" class="wpd-comment-reply-form">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 ${c.userName || c.userId}...`"
                maxlength="1000"
              />
              <div class="wpd-comment-reply-form-foot">
                <el-button size="small" @click="onReplyCancel">取消</el-button>
                <el-button size="small" type="primary" :loading="commentSubmitting" @click="onSubmitReply(c)">
                  提交
                </el-button>
              </div>
            </div>
          </li>
        </ul>

        <el-pagination
          v-if="total > pageSize"
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="pageSizes"
          background
          layout="total, prev, pager, next, jumper"
          :total="total"
          @current-change="fetchComments"
        />
      </section>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Clock, View, ChatDotRound, Star, Collection } from '@element-plus/icons-vue';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { postApi } from '@/apis/post';
import { commentApi } from '@/apis/comment';
import { likeApi } from '@/apis/like';
import { collectApi } from '@/apis/collect';
import type { PostVO, CommentVO } from '@/apis/post';
import { isLoggedIn, userStore } from '@/utils';

defineOptions({ name: 'WebPostDetail' });

const route = useRoute();
const postId = computed(() => Number(route.params.id));
const loading = ref(false);
const post = ref<PostVO | null>(null);

const { pageNum, pageSize, total, setPageNum } = usePagination();
const dataSource = ref<CommentVO[]>([]);
const newComment = ref('');
const commentSubmitting = ref(false);
const replyTo = ref<CommentVO | null>(null);
const replyContent = ref('');

const currentUserId = computed(() => userStore.getUserId());

const liked = ref(false);
const collected = ref(false);
const likeLoading = ref(false);
const collectLoading = ref(false);

const fetchPost = async () => {
  if (!postId.value) return;
  loading.value = true;
  try {
    post.value = await postApi.getPost(postId.value);
  } finally {
    loading.value = false;
  }
};

const fetchComments = async () => {
  if (!postId.value) return;
  try {
    const res = await commentApi.pageComments({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      postId: postId.value,
      status: 1,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } catch {
    // ignore
  }
};

const fetchStatus = async () => {
  if (!postId.value || !isLoggedIn()) return;
  try {
    const [l, c] = await Promise.all([likeApi.likeStatus(1, postId.value), collectApi.collectStatus(postId.value)]);
    liked.value = l.liked;
    collected.value = c.collected;
  } catch {
    // ignore
  }
};

const ensureLogin = () => {
  if (!isLoggedIn()) {
    ElMessage.warning('请先登录');
    return false;
  }
  return true;
};

const onToggleLike = async () => {
  if (!post.value || !ensureLogin()) return;
  likeLoading.value = true;
  try {
    if (liked.value) {
      await likeApi.cancelLike(1, post.value.id);
      liked.value = false;
      post.value.likeCount = Math.max(0, (post.value.likeCount ?? 0) - 1);
    } else {
      await likeApi.like(1, post.value.id);
      liked.value = true;
      post.value.likeCount = (post.value.likeCount ?? 0) + 1;
    }
  } finally {
    likeLoading.value = false;
  }
};

const onToggleCollect = async () => {
  if (!post.value || !ensureLogin()) return;
  collectLoading.value = true;
  try {
    if (collected.value) {
      await collectApi.cancelCollect(post.value.id);
      collected.value = false;
      post.value.collectCount = Math.max(0, (post.value.collectCount ?? 0) - 1);
    } else {
      await collectApi.collect(post.value.id);
      collected.value = true;
      post.value.collectCount = (post.value.collectCount ?? 0) + 1;
    }
  } finally {
    collectLoading.value = false;
  }
};

const onSubmitComment = async () => {
  if (!ensureLogin()) return;
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }
  commentSubmitting.value = true;
  try {
    await commentApi.createComment({
      postId: postId.value,
      content: newComment.value.trim(),
    });
    newComment.value = '';
    ElMessage.success('评论成功');
    setPageNum(1);
    await fetchComments();
    if (post.value) {
      post.value.commentCount = (post.value.commentCount ?? 0) + 1;
    }
  } finally {
    commentSubmitting.value = false;
  }
};

const onReplyStart = (c: CommentVO) => {
  if (!ensureLogin()) return;
  replyTo.value = c;
  replyContent.value = '';
};

const onReplyCancel = () => {
  replyTo.value = null;
  replyContent.value = '';
};

const onSubmitReply = async (c: CommentVO) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  commentSubmitting.value = true;
  try {
    await commentApi.createComment({
      postId: postId.value,
      parentId: c.parentId && c.parentId > 0 ? c.parentId : c.id,
      replyToUserId: c.userId,
      content: replyContent.value.trim(),
    });
    replyTo.value = null;
    replyContent.value = '';
    ElMessage.success('回复成功');
    setPageNum(1);
    await fetchComments();
  } finally {
    commentSubmitting.value = false;
  }
};

const onDeleteComment = async (c: CommentVO) => {
  try {
    await ElMessageBox.confirm('确认删除该评论?', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await commentApi.deleteComment(c.id);
  ElMessage.success('已删除');
  await fetchComments();
};

watch(postId, () => {
  post.value = null;
  fetchPost();
  fetchComments();
  fetchStatus();
});

onMounted(async () => {
  await fetchPost();
  await fetchComments();
  await fetchStatus();
});
</script>

<style lang="less" scoped>
.web-post-detail {
  padding: 24px 0;
}

.wpd-main {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 16px;
  border: 1px solid var(--el-border-color-lighter);
}

.wpd-title {
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 8px;
}

.wpd-meta {
  margin-top: 12px;
  display: flex;
  gap: 16px;
  color: var(--el-text-color-secondary);
  font-size: 13px;

  span {
    display: inline-flex;
    align-items: center;
    gap: 4px;
  }
}

.wpd-tags {
  margin-top: 12px;
  display: flex;
  gap: 6px;
}

.wpd-content {
  margin-top: 16px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  color: var(--el-text-color-primary);
}

.wpd-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  border-top: 1px solid var(--el-border-color-lighter);
  padding-top: 16px;
}

.wpd-comments {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid var(--el-border-color-lighter);
}

.wpd-comments-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.wpd-comments-input-foot {
  margin-top: 8px;
  text-align: right;
}

.wpd-comment-list {
  list-style: none;
  padding: 0;
  margin: 16px 0 0;
}

.wpd-comment {
  padding: 12px 0;
  border-top: 1px solid var(--el-border-color-lighter);
}

.wpd-comment-head {
  display: flex;
  align-items: center;
  gap: 8px;
}

.wpd-comment-info {
  display: flex;
  flex-direction: column;
}

.wpd-comment-user {
  font-weight: 500;
}

.wpd-comment-time {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.wpd-comment-body {
  margin-top: 8px;
  margin-left: 40px;
  color: var(--el-text-color-primary);
}

.wpd-comment-reply {
  color: var(--el-color-primary);
}

.wpd-comment-foot {
  margin-top: 4px;
  margin-left: 40px;
}

.wpd-comment-reply-form {
  margin-top: 8px;
  margin-left: 40px;
  background: var(--el-fill-color-light);
  padding: 8px;
  border-radius: 4px;
}

.wpd-comment-reply-form-foot {
  margin-top: 6px;
  text-align: right;
}
</style>
