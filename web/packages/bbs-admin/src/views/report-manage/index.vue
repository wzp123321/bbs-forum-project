<template>
  <div class="report-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="searchList" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="searchForm.keyword" placeholder="举报说明/补充" clearable @keyup.enter="searchList" />
      </el-form-item>
      <el-form-item label="目标">
        <el-select v-model="searchForm.targetType" placeholder="全部目标" clearable style="width: 140px">
          <el-option label="帖子" :value="1" />
          <el-option label="评论" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="原因">
        <el-select v-model="searchForm.reasonType" placeholder="全部原因" clearable style="width: 140px">
          <el-option v-for="(text, val) in REASON_TEXT" :key="val" :label="text" :value="val" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
          <el-option label="待处理" :value="0" />
          <el-option label="已处理" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="举报人" width="120">
        <template #default="{ row }">{{ row.userName || row.userId || '-' }}</template>
      </el-table-column>
      <el-table-column label="目标" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.targetTypeText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原因" width="120">
        <template #default="{ row }">{{ REASON_TEXT[row.reasonType ?? ''] || row.reasonType }}</template>
      </el-table-column>
      <el-table-column label="被举报内容" min-width="280">
        <template #default="{ row }">
          <div v-if="row.targetTitle" class="rm-target-title">{{ row.targetTitle }}</div>
          <div class="rm-target-meta">
            <span>作者: {{ row.targetUserName || row.targetUserId || '-' }}</span>
          </div>
          <div v-if="row.targetContent" class="rm-target-content" :title="row.targetContent">
            {{ row.targetContent }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="举报说明" min-width="180" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.content">{{ row.content }}</span>
          <span v-else class="rm-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 0" type="warning">待处理</el-tag>
          <el-tag v-else-if="row.status === 1" type="success">已处理</el-tag>
          <el-tag v-else type="info">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" width="170" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :disabled="row.status !== 0" @click="openDealDialog(row)">
            {{ row.status === 0 ? '处理' : '查看' }}
          </el-button>
          <el-button type="danger" link @click="confirmDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="rm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onPageSizeChange"
      @current-change="onPageNumChange"
    />

    <RmDealDialog ref="dealRef" @handled="fetchList" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { pageReportsApi, deleteReportApi } from '@/apis/report';
import type { ReportVO } from '@/apis/report';
import { SearchForm, REASON_TEXT } from './constant/model';
import RmDealDialog from './components/rm-deal-dialog.vue';

defineOptions({ name: 'ReportManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<ReportVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({
  keyword: '',
  targetType: undefined,
  reasonType: undefined,
  status: undefined,
});
const dealRef = ref<InstanceType<typeof RmDealDialog>>();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageReportsApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      targetType: searchForm.targetType,
      reasonType: searchForm.reasonType,
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
  searchForm.targetType = undefined;
  searchForm.reasonType = undefined;
  searchForm.status = undefined;
  searchList();
};
const openDealDialog = (row: ReportVO) => {
  dealRef.value?.open(row);
};
const confirmDelete = async (row: ReportVO) => {
  try {
    await ElMessageBox.confirm('确认删除该举报记录?', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteReportApi(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

fetchList();
</script>

<style lang="less" scoped>
.report-manage {
  padding: var(--bbs-space-16);

  .search-form {
    margin-bottom: var(--bbs-space-16);
  }

  .rm-muted {
    color: var(--el-text-color-placeholder);
  }

  .rm-target-title {
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin-bottom: 2px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .rm-target-meta {
    color: var(--el-text-color-secondary);
    font-size: 12px;
    margin-bottom: 2px;
  }

  .rm-target-content {
    color: var(--el-text-color-regular);
    font-size: 12px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .rm-pagination {
    margin-top: var(--bbs-space-16);
    justify-content: flex-end;
  }
}
</style>
