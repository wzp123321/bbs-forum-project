<template>
  <div class="web-post-publish">
    <h2 class="wpp-title">发布新帖</h2>
    <el-card shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @submit.prevent="handleSubmit">
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
          <div class="wpp-editor">
            <div class="wpp-editor-toolbar">
              <el-button-group size="small">
                <el-button title="加粗 (Ctrl+B)" @click="wrap('strong', '加粗文字')">
                  <el-icon><EditPen /></el-icon>
                </el-button>
                <el-button title="斜体" @click="wrap('em', '斜体文字')">
                  <em>I</em>
                </el-button>
                <el-button title="删除线" @click="wrap('del', '删除文字')">
                  <el-icon><Delete /></el-icon>
                </el-button>
                <el-button title="行内代码" @click="wrap('code', 'code')">
                  <el-icon><Coin /></el-icon>
                </el-button>
              </el-button-group>
              <el-button-group size="small">
                <el-button title="一级标题" @click="prependLine('## ', '')">H2</el-button>
                <el-button title="二级标题" @click="prependLine('### ', '')">H3</el-button>
                <el-button title="引用" @click="prependLine('> ', '引用内容')">引用</el-button>
                <el-button title="无序列表" @click="prependLine('- ', '列表项')">列表</el-button>
                <el-button title="有序列表" @click="prependLine('1. ', '列表项')">有序</el-button>
              </el-button-group>
              <el-button-group size="small">
                <el-button title="插入链接" :icon="Link" @click="insertLink">链接</el-button>
                <el-button title="插入代码块" :icon="Files" @click="insertCodeBlock">代码块</el-button>
                <el-button title="插入水平线" @click="insertHr">分割线</el-button>
              </el-button-group>
              <el-button-group size="small">
                <el-upload
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :http-request="handleUpload"
                  accept="image/*"
                >
                  <el-button :loading="uploading" :icon="Picture">插入图片</el-button>
                </el-upload>
                <el-button :icon="VideoCameraFilled" @click="onOpenVideoDialog">插入视频</el-button>
              </el-button-group>
              <span v-if="uploading && uploadPercent < 100" class="wpp-upload-tip">上传中 {{ uploadPercent }}%</span>
            </div>
            <el-input
              ref="contentRef"
              v-model="form.content"
              type="textarea"
              :rows="14"
              placeholder="请输入帖子正文 (支持 HTML/Markdown,可插入图片)"
              maxlength="20000"
              show-word-limit
              resize="vertical"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">发布</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-card shadow="never" class="wpp-hint">
        <div>
          阅读权限
          <el-radio-group v-model="form.readPerm" size="small">
            <el-radio-button v-for="(label, val) in readPermLabels" :key="val" :value="Number(val)">
              {{ label }}
            </el-radio-button>
          </el-radio-group>
        </div>
      </el-card>
    </el-card>

    <!-- 插入视频对话框 -->
    <el-dialog v-model="videoDialog.visible" title="插入视频" width="560px">
      <el-input
        v-model="videoDialog.url"
        placeholder="请输入视频链接 (支持 B站/YouTube 等)"
        clearable
        @input="onPreviewVideo"
      />
      <div v-if="videoDialog.preview" class="wpp-video-preview" v-html="videoDialog.preview.html"></div>
      <template #footer>
        <el-button @click="videoDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="onConfirmVideo">确定插入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus';
import { Coin, Delete, EditPen, Files, Link, Picture, VideoCameraFilled } from '@element-plus/icons-vue';
import { listCategoriesApi } from '@/apis/category';
import { listTagsApi } from '@/apis/tag';
import { createPostApi } from '@/apis/post';
import { uploadAttachment } from '@/apis/attachment';
import { parseVideoEmbed } from '@/utils/media';
import { READ_PERM_LABELS } from '@/apis/post';
import type { CategoryVO } from '@/apis/category';
import type { TagVO } from '@/apis/tag';

defineOptions({ name: 'WebPostPublish' });

const router = useRouter();
const formRef = ref<FormInstance>();
const contentRef = ref();
const submitting = ref(false);
const loadingCategory = ref(false);
const categories = ref<CategoryVO[]>([]);
const tags = ref<TagVO[]>([]);
const uploading = ref(false);
const uploadPercent = ref(100);

const videoDialog = reactive({
  visible: false,
  url: '',
  html: '',
  preview: null as ReturnType<typeof parseVideoEmbed>,
});

const form = reactive({
  title: '',
  content: '',
  contentType: 1,
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  readPerm: 1,
});

const readPermLabels = READ_PERM_LABELS;

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
    categories.value = await listCategoriesApi();
  } finally {
    loadingCategory.value = false;
  }
  try {
    tags.value = await listTagsApi();
  } catch {
    // ignore
  }
};

/** 把指定 HTML 标签包裹当前选中区域 (无选中则插入占位) */
const wrap = (tag: string, placeholder: string) => {
  insertAtCursor(`<${tag}>${placeholder}</${tag}>`);
};

/** 在当前行前插入前缀, 没有选中时插入占位 */
const prependLine = (prefix: string, placeholder: string) => {
  const ta = getTextarea();
  if (!ta) {
    form.content += `\n${prefix}${placeholder}\n`;
    return;
  }
  const start = ta.selectionStart;
  const before = form.content.substring(0, start);
  const lineStart = before.lastIndexOf('\n') + 1;
  const after = form.content.substring(start);
  // 在当前行首插入 prefix; 如果是空行,直接放 placeholder
  const currentLineEnd = after.indexOf('\n') === -1 ? after.length : after.indexOf('\n');
  const currentLine = after.substring(0, currentLineEnd);
  if (!currentLine) {
    form.content = form.content.substring(0, lineStart) + `${prefix}${placeholder}\n` + after.substring(currentLineEnd);
  } else {
    form.content = form.content.substring(0, lineStart) + prefix + after;
  }
  // 重新聚焦并把光标移到最后
  setTimeout(() => {
    if (ta) {
      ta.focus();
      const newPos = lineStart + prefix.length + (currentLine ? currentLine.length : placeholder.length);
      ta.setSelectionRange(newPos, newPos);
    }
  });
};

const insertLink = async () => {
  try {
    const { value: url } = await ElMessageBox.prompt('请输入链接地址', '插入链接', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /^https?:\/\//,
      inputErrorMessage: '请输入 http(s) 开头的链接',
    });
    const { value: text } = await ElMessageBox.prompt('请输入链接文字 (可空, 默认使用 URL)', '插入链接', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
    const t = text || url;
    insertAtCursor(`<a href="${url}" target="_blank" rel="noopener">${t}</a>`);
  } catch {
    // 取消
  }
};

const insertCodeBlock = () => {
  insertAtCursor('\n<pre><code>// 在这里写代码</code></pre>\n');
};

const insertHr = () => {
  insertAtCursor('\n<hr/>\n');
};

const insertAtCursor = (text: string) => {
  const ta = getTextarea();
  if (!ta) {
    form.content = (form.content || '') + text;
    return;
  }
  const start = ta.selectionStart;
  const end = ta.selectionEnd;
  form.content = form.content.substring(0, start) + text + form.content.substring(end);
  // 把光标移到插入文本中间
  setTimeout(() => {
    if (ta) {
      ta.focus();
      const pos = start + text.length;
      ta.setSelectionRange(pos, pos);
    }
  });
};

const getTextarea = (): HTMLTextAreaElement | null => {
  // el-input type=textarea 实际渲染成 <textarea class="el-textarea__inner">
  const root = contentRef.value?.$el ?? contentRef.value;
  if (!root) return null;
  return root.querySelector?.('textarea') ?? null;
};

const beforeUpload = (file: File) => {
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB');
    return false;
  }
  return true;
};

const handleUpload = async (option: { file: File }) => {
  uploading.value = true;
  uploadPercent.value = 0;
  try {
    const att = await uploadAttachment(option.file, {
      bizType: 'post',
      onProgress: (p) => (uploadPercent.value = p),
    });
    if (att?.url) {
      const imgTag = `\n<img src="${att.url}" alt="${att.originName || ''}" />\n`;
      insertAtCursor(imgTag);
      ElMessage.success('图片已插入');
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '图片上传失败');
  } finally {
    uploading.value = false;
    uploadPercent.value = 100;
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
    const id = await createPostApi({
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

const onOpenVideoDialog = () => {
  videoDialog.visible = true;
  videoDialog.url = '';
  videoDialog.html = '';
  videoDialog.preview = null;
};

const onConfirmVideo = () => {
  if (!videoDialog.url) {
    ElMessage.warning('请输入视频链接');
    return;
  }
  const embed = parseVideoEmbed(videoDialog.url);
  if (embed) {
    insertAtCursor(embed.html);
    ElMessage.success('视频已插入');
  } else {
    ElMessage.error('无法解析该视频链接');
  }
  videoDialog.visible = false;
};

const onPreviewVideo = () => {
  videoDialog.preview = parseVideoEmbed(videoDialog.url);
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

.wpp-editor {
  width: 100%;
}

.wpp-editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1px solid var(--el-border-color);
  border-bottom: none;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  background: #fafafa;
}

.wpp-editor-toolbar :deep(.el-button + .el-button-group) {
  margin-left: 0;
}

.wpp-editor :deep(.el-textarea__inner) {
  border-top-left-radius: 0;
  border-top-right-radius: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 14px;
  line-height: 1.6;
}

.wpp-upload-tip {
  margin-left: 8px;
  color: var(--el-color-primary);
  font-size: 12px;
}
</style>
