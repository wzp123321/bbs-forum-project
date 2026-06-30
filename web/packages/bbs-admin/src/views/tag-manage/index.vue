<template>
  <div class="tag-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="searchList">
      <el-form-item>
        <el-input v-model="searchForm.keyword" placeholder="请输入标签名称" clearable @keyup.enter="searchList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="openAddDrawer">新增标签</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="标签名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
      <el-table-column prop="postCount" label="引用次数" width="100" align="center" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDrawer(row)">编辑</el-button>
          <el-button type="danger" link @click="confirmDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="tm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onPageSizeChange"
      @current-change="onPageNumChange"
    />

    <TmAddEditDrawer ref="drawerRef" @saved="fetchList" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { SearchForm } from './constant/model';
import { TmAddEditDrawer } from './components';
import { deleteTagApi, pageTagsApi, TagVO } from '@/apis';

defineOptions({ name: 'TagManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<TagVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '' });
const drawerRef = ref<InstanceType<typeof TmAddEditDrawer>>();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageTagsApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
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
  searchList();
};

const openAddDrawer = () => {
  drawerRef.value?.open();
};

const openEditDrawer = (row: TagVO) => {
  drawerRef.value?.open(row);
};

const confirmDelete = async (row: TagVO) => {
  try {
    await ElMessageBox.confirm(`确认删除标签 ${row.name} ?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteTagApi(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

fetchList();
</script>

<style lang="less" scoped>
.tag-manage {
  padding: var(--bbs-space-16);

  .tm-pagination {
    margin-top: var(--bbs-space-16);
    justify-content: flex-end;
  }
}
</style>
