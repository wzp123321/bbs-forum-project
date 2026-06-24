<template>
  <div class="comment-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch" class="search-form">
      <el-form-item label="内容">
        <el-input v-model="searchForm.keyword" placeholder="请输入评论内容" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="帖子ID">
        <el-input-number v-model="searchForm.postId" :min="0" controls-position="right" placeholder="帖子ID" style="width: 160px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
          <el-option label="正常" :value="1" />
          <el-option label="已删" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="所属帖子" min-width="220" show-overflow-tooltip>
        <template #default="{ row }">
          <span v-if="row.postId">
            [{{ row.postId }}]
            <el-button link type="primary" @click="emit('jumpPost', row.postId)">{{ row.postTitle || '-' }}</el-button>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="评论人" width="120">
        <template #default="{ row }">{{ row.userName || row.userId || '-' }}</template>
      </el-table-column>
      <el-table-column label="回复给" width="120">
        <template #default="{ row }">{{ row.replyToUserName || row.replyToUserId || '-' }}</template>
      </el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="240" show-overflow-tooltip />
      <el-table-column prop="likeCount" label="点赞" width="80" align="center" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '正常' : '已删' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="warning" link @click="handleChangeStatus(row, 0)">禁用</el-button>
          <el-button v-else type="success" link @click="handleChangeStatus(row, 1)">启用</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="cm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { commentApi } from '@/utils';
import type { CommentVO } from '@/apis/comment';
import { SearchForm } from './constant/model';

defineOptions({ name: 'CommentManage' });

const emit = defineEmits<{ (e: 'jumpPost', postId: number): void }>();

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<CommentVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '', postId: undefined, status: undefined });

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await commentApi.pageComments({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      postId: searchForm.postId,
      status: searchForm.status,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const handleSizeChange = (value: number) => {
  setPageSize(value);
  fetchList();
};

const handleCurrentChange = (value: number) => {
  setPageNum(value);
  fetchList();
};

const handleSearch = () => {
  setPageNum(1);
  fetchList();
};

const handleReset = () => {
  searchForm.keyword = '';
  searchForm.postId = undefined;
  searchForm.status = undefined;
  handleSearch();
};

const handleDelete = async (row: CommentVO) => {
  try {
    await ElMessageBox.confirm(`确认删除评论「${row.content}」?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await commentApi.deleteComment(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

const handleChangeStatus = async (row: CommentVO, status: number) => {
  await commentApi.changeStatus(row.id, { status });
  ElMessage.success(status === 1 ? '已启用' : '已禁用');
  fetchList();
};

defineExpose({ fetchList });
fetchList();
</script>

<style lang="less" scoped>
.comment-manage {
  padding: var(--bbs-space-16);

  .search-form {
    margin-bottom: var(--bbs-space-16);
  }

  .cm-pagination {
    margin-top: var(--bbs-space-16);
    justify-content: flex-end;
  }
}
</style>
