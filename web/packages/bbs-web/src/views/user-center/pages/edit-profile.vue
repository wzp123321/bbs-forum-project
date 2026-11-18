<template>
  <div class="uc-edit">
    <h3 class="uc-title">编辑资料</h3>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 480px">
      <el-form-item label="头像">
        <div class="uc-avatar-row">
          <el-avatar :size="72" :src="avatarPreview" class="uc-avatar">
            {{ (form.nickname || form.userName || '?').slice(0, 1) }}
          </el-avatar>
          <div class="uc-avatar-actions">
            <el-upload
              :show-file-list="false"
              accept="image/*"
              :http-request="handleAvatarUpload"
              :before-upload="beforeAvatarUpload"
            >
              <el-button size="small" :loading="uploading">
                {{ uploading ? `上传中 ${uploadPercent}%` : '更换头像' }}
              </el-button>
            </el-upload>
            <el-button v-if="form.avatar" size="small" link @click="form.avatar = ''">移除</el-button>
            <p class="uc-hint">支持 jpg / png，建议正方形，不超过 2MB</p>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" maxlength="20" show-word-limit disabled />
        <p class="uc-hint">用户名用于登录，不可修改</p>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" maxlength="20" show-word-limit placeholder="展示给其他用户的名字" />
      </el-form-item>
      <el-form-item label="个性签名" prop="signature">
        <el-input
          v-model="form.signature"
          type="textarea"
          :rows="3"
          maxlength="100"
          show-word-limit
          placeholder="一句话介绍自己"
        />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio value="M">男</el-radio>
          <el-radio value="F">女</el-radio>
          <el-radio value="">保密</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" maxlength="64" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" maxlength="20" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { computed, inject, onMounted, reactive, ref } from 'vue';
import { ElMessage, FormInstance, FormRules, UploadRequestOptions } from 'element-plus';
import { getUserApi, updateProfileApi } from '@/apis/user';
import { resolveFileUrl, uploadAttachment } from '@/apis/attachment';
import { userStore } from '@/utils';

defineOptions({ name: 'UcEditProfile' });

const refresh = inject<() => Promise<void>>('refreshUserCenter');

const formRef = ref<FormInstance>();
const submitting = ref(false);
const uploading = ref(false);
const uploadPercent = ref(0);

const form = reactive({
  userName: '',
  nickname: '',
  avatar: '',
  signature: '',
  gender: '' as '' | 'M' | 'F',
  email: '',
  phone: '',
});

const rules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度 2-20', trigger: 'blur' },
  ],
  signature: [{ max: 100, message: '签名不超过 100 字', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
};

/** 后端只接受 /upload/ 开头的站内地址，展示时再补全成完整 URL */
const avatarPreview = computed(() => resolveFileUrl(form.avatar));

const getMyId = () => userStore.getUserId();

const load = async () => {
  const id = getMyId();
  if (!id) return;
  try {
    const u = await getUserApi(id);
    form.userName = u.userName || '';
    form.nickname = u.nickname || u.userName || '';
    form.avatar = u.avatar || '';
    form.signature = u.signature || '';
    form.gender = (u.gender as '' | 'M' | 'F') || '';
    form.email = u.email || '';
    form.phone = u.phone || '';
  } catch {
    // ignore
  }
};

const beforeAvatarUpload = (file: File) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件');
    return false;
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 2MB');
    return false;
  }
  return true;
};

/** 上传接口返回的是完整 URL，入库前裁回 /upload/ 开头的相对路径 */
const toUploadPath = (url: string) => {
  const i = url.indexOf('/upload/');
  return i >= 0 ? url.slice(i) : url;
};

const handleAvatarUpload = async (opts: UploadRequestOptions) => {
  uploading.value = true;
  uploadPercent.value = 0;
  try {
    const att = await uploadAttachment(opts.file, {
      bizType: 'avatar',
      onProgress: (p) => (uploadPercent.value = p),
    });
    if (!att?.url) {
      ElMessage.warning('上传成功但未拿到图片地址');
      return;
    }
    form.avatar = toUploadPath(att.url);
    ElMessage.success('头像已上传，保存后生效');
  } catch (e: any) {
    ElMessage.error(e?.message || '上传失败');
  } finally {
    uploading.value = false;
  }
};

const onSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    await updateProfileApi(getMyId(), {
      nickname: form.nickname.trim(),
      avatar: form.avatar,
      signature: form.signature.trim(),
      gender: form.gender || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
    });
    ElMessage.success('保存成功');
    if (refresh) await refresh();
  } finally {
    submitting.value = false;
  }
};

const onReset = () => {
  formRef.value?.resetFields();
  load();
};

onMounted(load);
</script>

<style lang="less" scoped>
.uc-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.uc-avatar-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.uc-avatar {
  flex: none;
  font-size: 28px;
  background: var(--el-color-primary);
  color: #fff;
}

.uc-avatar-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.uc-hint {
  width: 100%;
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}
</style>
