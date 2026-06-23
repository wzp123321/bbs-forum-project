<template>
  <el-drawer
    custom-class="pm-add-edit-drawer"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-model="visible"
    :title="isEdit ? '编辑帖子' : '新增帖子'"
    direction="rtl"
    :before-close="handleClose"
    size="560px"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" v-loading="loading">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入帖子标题" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="板块" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="请选择板块" style="width: 100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tagIds" multiple collapse-tags collapse-tags-tooltip placeholder="请选择标签" style="width: 100%">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="正文" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入帖子正文" />
      </el-form-item>
      <el-form-item label="置顶">
        <el-switch v-model="topSwitch" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item label="精华">
        <el-switch v-model="essenceSwitch" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">正常</el-radio>
          <el-radio :value="0">已删</el-radio>
          <el-radio :value="2">审核中</el-radio>
          <el-radio :value="3">审核不通过</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { categoryApi, postApi, tagApi } from '@/utils';
import type { CategoryVO } from '@/apis/category';
import type { PostVO } from '@/apis/post';
import type { TagVO } from '@/apis/tag';
import { RowFrom } from '../constant/model';

defineOptions({ name: 'PmAddEditDrawer' });

const emit = defineEmits<{ (e: 'saved'): void }>();

const visible = ref(false);
const loading = ref(false);
const submitting = ref(false);
const formRef = ref<FormInstance>();

const form = reactive<RowFrom>({
  title: '',
  content: '',
  contentType: 1,
  categoryId: undefined,
  tagIds: [],
  status: 1,
  isTop: 0,
  isEssence: 0,
});

// 开关代理 (el-switch 的 v-model 直接绑 isTop/isEssence 不便于重置,这里用 computed)
const topSwitch = computed<number>({
  get: () => form.isTop,
  set: (v) => (form.isTop = v),
});
const essenceSwitch = computed<number>({
  get: () => form.isEssence,
  set: (v) => (form.isEssence = v),
});

const isEdit = computed(() => !!form.id);

const rules: FormRules<RowFrom> = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择板块', trigger: 'change' }],
};

const categories = ref<CategoryVO[]>([]);
const tags = ref<TagVO[]>([]);

const loadOptions = async () => {
  loading.value = true;
  try {
    const [cs, ts] = await Promise.all([categoryApi.listCategories(), tagApi.listTags()]);
    categories.value = cs;
    tags.value = ts;
  } finally {
    loading.value = false;
  }
};

const reset = () => {
  form.id = undefined;
  form.title = '';
  form.content = '';
  form.contentType = 1;
  form.categoryId = undefined;
  form.tagIds = [];
  form.status = 1;
  form.isTop = 0;
  form.isEssence = 0;
};

const handleOpen = async (row?: PostVO) => {
  reset();
  await loadOptions();
  if (row) {
    form.id = row.id;
    form.title = row.title || '';
    form.content = row.content || '';
    form.contentType = row.contentType ?? 1;
    form.categoryId = row.categoryId;
    form.tagIds = [...(row.tagIds || [])];
    form.status = row.status ?? 1;
    form.isTop = row.isTop ?? 0;
    form.isEssence = row.isEssence ?? 0;
  }
  visible.value = true;
};

const handleClose = () => {
  visible.value = false;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();
  submitting.value = true;
  try {
    const payload = {
      title: form.title,
      content: form.content,
      contentType: form.contentType,
      categoryId: form.categoryId!,
      tagIds: form.tagIds,
      status: form.status,
      isTop: form.isTop,
      isEssence: form.isEssence,
    };
    if (isEdit.value) {
      await postApi.updatePost(form.id!, payload);
      ElMessage.success('修改成功');
    } else {
      await postApi.createPost(payload);
      ElMessage.success('新增成功');
    }
    handleClose();
    emit('saved');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  // 首次进入组件时预拉下拉数据
  loadOptions();
});

defineExpose({ handleOpen });
</script>

<style lang="less" scoped>
.pm-add-edit-drawer {
}
</style>
