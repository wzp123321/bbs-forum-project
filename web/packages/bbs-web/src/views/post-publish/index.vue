<template>
  <div class="web-post-publish">
    <h2 class="wpp-title">发布新帖</h2>
    <el-card shadow="never">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="板块" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择板块" style="width: 320px" :loading="loadingCategory">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="一句话说清你的主题" maxlength="100" show-word-limit clearable />
        </el-form-item>

        <el-form-item label="标签" prop="tagIds">
          <el-select
            v-model="form.tagIds"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或创建标签 (最多 5 个)"
            style="width: 100%"
            :max-collapse-tags="5"
            :multiple-limit="5"
          >
            <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="正文" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="12"
            placeholder="请输入帖子正文 (支持 MarkDown 纯文本)"
            maxlength="20000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">发布</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, FormInstance, FormRules } from 'element-plus';
import { categoryApi } from '@/apis/category';
import { tagApi } from '@/apis/tag';
import { postApi } from '@/apis/post';
import type { CategoryVO, TagVO } from '@/apis/post';

defineOptions({ name: 'WebPostPublish' });

const router = useRouter();
const formRef = ref<FormInstance>();
const submitting = ref(false);
const loadingCategory = ref(false);
const categories = ref<CategoryVO[]>([]);
const tags = ref<TagVO[]>([]);

const form = reactive({
  title: '',
  content: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
});

const rules: FormRules = {
  categoryId: [{ required: true, message: '请选择板块', trigger: 'change' }],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度 2-100', trigger: 'blur' },
  ],
  content: [
    { required: true, message: '请输入正文', trigger: 'blur' },
    { min: 5, message: '正文至少 5 个字符', trigger: 'blur' },
  ],
};

const loadOptions = async () => {
  loadingCategory.value = true;
  try {
    categories.value = await categoryApi.listCategories();
  } finally {
    loadingCategory.value = false;
  }
  try {
    tags.value = await tagApi.listTags();
  } catch {
    // ignore
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const id = await postApi.createPost({
      title: form.title.trim(),
      content: form.content,
      contentType: 1,
      categoryId: form.categoryId!,
      tagIds: form.tagIds.length ? form.tagIds : undefined,
    });
    ElMessage.success('发布成功');
    router.replace(`/post/${id}`);
  } finally {
    submitting.value = false;
  }
};

const onReset = () => {
  formRef.value?.resetFields();
};

onMounted(loadOptions);
</script>

<style lang="less" scoped>
.web-post-publish {
  padding: 24px 0;
}

.wpp-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
}
</style>
