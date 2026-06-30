<template>
  <el-dialog class="fm-deal-dialog" v-model="visible" title="处理反馈" width="500" :before-close="closeDialog">
    <el-form :model="form" label-width="80px">
      <el-form-item label="反馈内容">
        <div class="fm-content">{{ row?.content || '-' }}</div>
      </el-form-item>
      <el-form-item label="回复内容" required>
        <el-input
          v-model="form.reply"
          :rows="3"
          type="textarea"
          placeholder="请输入回复内容"
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useDialogOpen } from '@/hooks';
import { replyFeedbackApi } from '@/apis/feedback';
import type { FeedbackVO } from '@/apis/feedback';
import { ReplyForm } from '../constant/model';

defineOptions({ name: 'FmDealDialog' });

const emit = defineEmits<{ (e: 'replied'): void }>();

const { visible, closeDialog, openDialog } = useDialogOpen();

const form = reactive<ReplyForm>({ reply: '' });
const submitting = ref(false);
const row = ref<FeedbackVO | null>(null);

const open = (r: FeedbackVO) => {
  row.value = r;
  form.reply = r.reply || '';
  openDialog();
};

const submitForm = async () => {
  if (!form.reply.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  if (!row.value) return;
  submitting.value = true;
  try {
    await replyFeedbackApi(row.value.id, { reply: form.reply });
    ElMessage.success('已回复');
    closeDialog();
    emit('replied');
  } finally {
    submitting.value = false;
  }
};

defineExpose({ open });
</script>

<style lang="less" scoped>
.fm-deal-dialog {
  .fm-content {
    color: var(--el-text-color-secondary);
    background: var(--el-fill-color-light);
    padding: 8px 12px;
    border-radius: 4px;
    max-height: 200px;
    overflow-y: auto;
  }
}
</style>
