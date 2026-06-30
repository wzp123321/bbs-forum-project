<template>
  <el-dialog
    class="rm-deal-dialog"
    v-model="visible"
    :title="row ? `处理举报 #${row.id}` : '处理举报'"
    width="560"
    :before-close="closeDialog"
  >
    <div v-if="row" class="rm-deal-detail">
      <div class="rm-row">
        <span class="rm-label">举报人:</span>
        <span>{{ row.userName || row.userId }}</span>
      </div>
      <div class="rm-row">
        <span class="rm-label">举报原因:</span>
        <span>{{ REASON_TEXT[row.reasonType ?? ''] || row.reasonType }}</span>
      </div>
      <div class="rm-row">
        <span class="rm-label">补充说明:</span>
        <span class="rm-content">{{ row.content || '-' }}</span>
      </div>
      <div class="rm-row">
        <span class="rm-label">被举报内容:</span>
        <div class="rm-target">
          <div>
            <el-tag size="small">{{ row.targetTypeText }}</el-tag>
            <span style="margin-left: 8px">{{ row.targetUserName || row.targetUserId }}</span>
          </div>
          <div v-if="row.targetTitle" class="rm-target-title">{{ row.targetTitle }}</div>
          <div v-if="row.targetContent" class="rm-target-content">{{ row.targetContent }}</div>
        </div>
      </div>
    </div>

    <el-form :model="form" label-width="80px">
      <el-form-item label="处理结果" required>
        <el-radio-group v-model="form.status">
          <el-radio :value="1">已处理(下架内容)</el-radio>
          <el-radio :value="2">驳回举报</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理备注">
        <el-input
          v-model="form.handleRemark"
          type="textarea"
          :rows="3"
          placeholder="可填写处理说明 (选填, 不超过 500 字)"
          maxlength="500"
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
import { handleReportApi } from '@/apis/report';
import type { ReportVO } from '@/apis/report';
import { HandleForm, REASON_TEXT } from '../constant/model';

defineOptions({ name: 'RmDealDialog' });

const emit = defineEmits<{ (e: 'handled'): void }>();

const { visible, closeDialog, openDialog } = useDialogOpen();

const form = reactive<HandleForm>({ status: 1, handleRemark: '' });
const submitting = ref(false);
const row = ref<ReportVO | null>(null);

const open = (r: ReportVO) => {
  row.value = r;
  form.status = 1;
  form.handleRemark = r.handleRemark || '';
  openDialog();
};

const submitForm = async () => {
  if (!row.value) return;
  submitting.value = true;
  try {
    await handleReportApi(row.value.id, {
      status: form.status,
      handleRemark: form.handleRemark.trim() || undefined,
    });
    ElMessage.success('已处理');
    closeDialog();
    emit('handled');
  } finally {
    submitting.value = false;
  }
};

defineExpose({ open });
</script>

<style lang="less" scoped>
.rm-deal-dialog {
  .rm-deal-detail {
    background: var(--el-fill-color-light);
    border-radius: 4px;
    padding: 12px;
    margin-bottom: 16px;
  }

  .rm-row {
    display: flex;
    align-items: flex-start;
    font-size: 13px;
    line-height: 1.6;
    margin-bottom: 8px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .rm-label {
    flex: 0 0 80px;
    color: var(--el-text-color-secondary);
  }

  .rm-content {
    color: var(--el-text-color-primary);
    word-break: break-word;
  }

  .rm-target {
    flex: 1;
  }

  .rm-target-title {
    font-weight: 600;
    margin-top: 4px;
  }

  .rm-target-content {
    margin-top: 4px;
    color: var(--el-text-color-secondary);
    word-break: break-word;
  }
}
</style>
