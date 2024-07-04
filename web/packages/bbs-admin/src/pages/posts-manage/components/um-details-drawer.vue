<template>
  <el-drawer custom-class="um-add-edit-drawer" :close-on-press-escape="false" :close-on-click-modal="false"
    v-model="dialogVisible" title="新增" direction="rtl" :before-close="handleClose">
    <el-form label-position="top" label-width="auto" :model="dialogForm" style="max-width: 600px">
      <el-form-item label="发帖用户：">
        <el-input v-model="dialogForm.name" />
      </el-form-item>
      <el-form-item label="帖子标题：">
        <el-input v-model="dialogForm.title" placeholder="帖子标题" clearable />
      </el-form-item>
      <el-form-item label="帖子类型：">
        <el-select v-model="dialogForm.type" placeholder="帖子类型" style="width:220px">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="帖子状态：">
        <el-select v-model="dialogForm.status" placeholder="帖子状态" style="width:220px">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="帖子标题：">
        <Editor style="height: 500px; overflow-y: hidden;" v-model="dialogForm.content" :defaultConfig="editorConfig"
          mode="default" @onCreated="handleCreated" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          确定
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script lang="ts" setup>
import { onBeforeUnmount, ref, shallowRef, onMounted, reactive } from 'vue';
import { TableData } from '../model'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor } from '@wangeditor/editor-for-vue'
defineOptions({
  name: 'UmAddEditDrawer',
});


// 表单
const dialogForm = reactive<TableData>({
  id: '',
  name: '',
  title: '',
  content: '',
  status: 1,
  type: 1,
  purview: ''
})
// 帖子类型列表
const typeList = [
  {
    value: 1,
    label: '11',
  },
  {
    value: 2,
    label: '22',
  }
]
// 帖子状态列表
const statusList = [
  {
    value: 1,
    label: '11',
  },
  {
    value: 2,
    label: '22',
  }
]
// 开关
const dialogVisible = ref(false);
// 打开
const handleOpen = (row: TableData) => {
  dialogVisible.value = true;
  dialogForm.name = row.name
  dialogForm.title = row.title
  dialogForm.content = row.content
  dialogForm.type = row.type
  dialogForm.status = row.status
  dialogForm.purview = row.purview
};
// 关闭
const handleClose = () => {
  dialogVisible.value = false;
};

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()
// 内容 HTML
const valueHtml = ref('<p>hello</p>')
// 模拟 ajax 异步获取内容
onMounted(() => {
  setTimeout(() => {
    valueHtml.value = '<p>模拟 Ajax 异步设置内容</p>'
  }, 1500)
})
const editorConfig = { placeholder: '请输入内容...', MENU_CONF: {} }
editorConfig.MENU_CONF['uploadImage'] = {
  // 服务端地址
  server: '/api/upload',
  // form-data fieldName ，默认值 'wangeditor-uploaded-image'
  fieldName: 'your-custom-name',
  // 单个文件的最大体积限制，默认为 2M
  maxFileSize: 1 * 1024 * 1024, // 1M
  // 最多可上传几个文件，默认为 100
  maxNumberOfFiles: 10,
  // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
  allowedFileTypes: ['image/*'],
  // 自定义上传参数，例如传递验证的 token 等。参数会被添加到 formData 中，一起上传到服务端。
  meta: {
    token: 'xxx',
    otherKey: 'yyy'
  },
  // 将 meta 拼接到 url 参数中，默认 false
  metaWithUrl: false,
  // 自定义增加 http  header
  headers: {
    Accept: 'text/x-json',
    otherKey: 'xxx'
  },
  // 跨域是否传递 cookie ，默认为 false
  withCredentials: true,
  // 超时时间，默认为 10 秒
  timeout: 5 * 1000, // 5 秒
}
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
defineExpose({
  handleOpen,
});
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
</script>
<style lang="less" scoped>
.um-add-edit-drawer {}
</style>
  