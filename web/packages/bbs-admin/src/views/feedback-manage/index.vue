<template>
  <div class="feedback-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="searchList" class="search-form">
      <el-form-item label="内容">
        <el-input v-model="searchForm.keyword" placeholder="请输入反馈内容" clearable @keyup.enter="searchList" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchForm.type" placeholder="全部类型" clearable style="width: 160px">
          <el-option label="BUG" value="bug" />
          <el-option label="建议" value="建议" />
          <el-option label="投诉" value="投诉" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
          <el-option label="未处理" :value="0" />
          <el-option label="已处理" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userName" label="反馈人" width="120">
        <template #default="{ row }">{{ row.userName || row.userId || '游客' }}</template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="content" label="反馈内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="reply" label="回复内容" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.reply">{{ row.reply }}</span>
          <span v-else class="fm-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">
            {{ row.status === 1 ? '已处理' : '未处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="反馈时间" width="170" />
      <el-table-column prop="replyTime" label="处理时间" width="170">
        <template #default="{ row }">{{ row.replyTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openReplyDialog(row)">
            {{ row.status === 1 ? '查看/修改' : '处理' }}
          </el-button>
          <el-button type="danger" link @click="confirmDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="fm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onPageSizeChange"
      @current-change="onPageNumChange"
    />

    <FmDealDialog ref="dealRef" @replied="fetchList" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { pageFeedbacksApi, deleteFeedbackApi } from '@/apis/feedback';
import type { FeedbackVO } from '@/apis/feedback';
import { SearchForm } from './constant/model';
import FmDealDialog from './components/fm-deal-dialog.vue';

defineOptions({ name: 'FeedbackManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<FeedbackVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '', type: undefined, status: undefined });
const dealRef = ref<InstanceType<typeof FmDealDialog>>();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageFeedbacksApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      type: searchForm.type,
      status: searchForm.status,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const onPageSizeChange = (value: number) => {
  setPageSize(value);
  fetchList();
};
const onPageNumChange = (value: number) => {
  setPageNum(value);
  fetchList();
};
const searchList = () => {
  setPageNum(1);
  fetchList();
};
const resetSearch = () => {
  searchForm.keyword = '';
  searchForm.type = undefined;
  searchForm.status = undefined;
  searchList();
};
const openReplyDialog = (row: FeedbackVO) => {
  dealRef.value?.open(row);
};
const confirmDelete = async (row: FeedbackVO) => {
  try {
    await ElMessageBox.confirm('确认删除该反馈?', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteFeedbackApi(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

fetchList();
</script>

<style lang="less" scoped>
.feedback-manage {
  padding: var(--bbs-space-16);

  .search-form {
    margin-bottom: var(--bbs-space-16);
  }

  .fm-muted {
    color: var(--el-text-color-placeholder);
  }

  .fm-pagination {
    margin-top: var(--bbs-space-16);
    justify-content: flex-end;
  }
}
</style>
