<template>
  <bbs-page-container class="feedback-manage">
    <template #header>
      <el-form :inline="true">
        <el-form-item>
          <el-input v-model="searchLabel" placeholder="请输入关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </template>
    <!-- 还需要在字典里面维护反馈类型 -->
    <template #content></template>
  </bbs-page-container>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { FeedbackInfo } from './model';

defineOptions({
  name: 'FeedbackManage',
});
const searchLabel = ref('');
// 数据
const dataSource = ref<FeedbackInfo[]>([]);

const query = () => {};
/**
 * 查询
 */
const handleSearch = () => {
  query();
};
/**
 * 重置
 */
const handleReset = () => {
  searchLabel.value = '';
  query();
};
</script>

<style lang="less" scoped></style>
