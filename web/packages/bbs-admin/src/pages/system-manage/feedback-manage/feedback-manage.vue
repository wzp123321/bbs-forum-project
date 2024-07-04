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
    <template #content>
      <el-table :data="dataSource" stripe>
        <el-table-column type="index" label="序号" width="80" align="left">
          <template #default="{ $index }">
            {{ (pageNum - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" align="left" show-overflow-ellipsis />
        <el-table-column prop="typeName" label="反馈类型" align="left" show-overflow-ellipsis />
        <el-table-column prop="feedbackUser" label="反馈人" align="left" show-overflow-ellipsis />
        <el-table-column prop="reply" label="回复内容" align="left" show-overflow-ellipsis />
        <el-table-column label="反馈时间" align="right" show-overflow-ellipsis>
          <template #default="{ row }">
            {{ formatTimeStamp(row.createTime, 'YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="处理时间" align="right" show-overflow-ellipsis>
          <template #default="{ row }">
            {{ formatTimeStamp(row.dealTime, 'YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="left">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleDealOpen">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </template>
  </bbs-page-container>
  <fm-deal-dialog ref="dealRef"></fm-deal-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { FeedbackInfo } from './model';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { formatTimeStamp } from '@bbs/utils';
import FmDealDialog from './components/fm-deal-dialog.vue';

defineOptions({
  name: 'FeedbackManage',
});
// 分页
const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const searchLabel = ref('');
// 数据
const dataSource = ref<FeedbackInfo[]>([
  {
    id: 1,
    content: '3213123131231',
    type: '1',
    typeName: '系统BUG',
    reply: '我回复了',
    feedbackUser: '账上',
    status: '1',
    dealTime: 1720103224623,
    createTime: 1720103224623,
  },
]);
/**
 * 修改条数
 * @param {number} value
 */
const handleSizeChange = (value: number) => {
  setPageSize(value);
  handleSearch();
};
/**
 * 修改页码
 * @param {number} value
 */
const handleCurrentChange = (value: number) => {
  setPageNum(value);
  handleSearch();
};
/**
 * 重置
 */
const handleReset = () => {
  searchLabel.value = '';
  handleSearch();
};
/**
 * 查询
 */
const handleSearch = () => {};

const dealRef = ref<InstanceType<typeof FmDealDialog>>();
/**
 * 处理
 */
const handleDealOpen = () => {
  console.log(dealRef.value);
  dealRef.value?.show();
};
</script>

<style lang="less" scoped>
.feedback-manage {
  --el-input-width: 240px;
}
</style>
