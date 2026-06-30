<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="480px"
    :close-on-click-modal="false"
    destroy-on-close
    @closed="onClosed"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="原因" prop="reasonType">
        <el-select v-model="form.reasonType" placeholder="请选择举报原因" style="width: 100%">
          <el-option v-for="opt in REPORT_REASON_OPTIONS" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="3"
          placeholder="补充说明 (选填, 不超过 500 字)"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { createReportApi } from '@/apis/report';
import { REPORT_REASON_OPTIONS, ReportTargetType } from '@/apis/report';
import { isLoggedIn } from '@/utils';

const props = defineProps<{
  modelValue: boolean;
  targetType?: ReportTargetType;
  targetId?: number;
  targetTitle?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean): void;
  (e: 'submitted'): void;
}>();

const visible = ref(props.modelValue);
const submitting = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
  reasonType: '' as string,
  content: '',
});

const rules: FormRules = {
  reasonType: [{ required: true, message: '请选择举报原因', trigger: 'change' }],
};

const title = `举报${props.targetType === 2 ? '评论' : '帖子'}${props.targetTitle ? ` - ${props.targetTitle}` : ''}`;

watch(
  () => props.modelValue,
  (v) => {
    visible.value = v;
  },
);
watch(visible, (v) => emit('update:modelValue', v));

const onCancel = () => {
  visible.value = false;
};

const onClosed = () => {
  form.reasonType = '';
  form.content = '';
  formRef.value?.clearValidate();
};

const onSubmit = async () => {
  if (!isLoggedIn()) {
    ElMessage.warning('请先登录');
    return;
  }
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  if (props.targetType == null || props.targetId == null) {
    ElMessage.error('举报目标缺失');
    return;
  }
  submitting.value = true;
  try {
    await createReportApi({
      targetType: props.targetType,
      targetId: props.targetId,
      reasonType: form.reasonType,
      content: form.content.trim() || undefined,
    });
    ElMessage.success('举报已提交,等待管理员处理');
    visible.value = false;
    emit('submitted');
  } catch (e: any) {
    // 业务错误已经由 service 统一提示
  } finally {
    submitting.value = false;
  }
};
</script>
