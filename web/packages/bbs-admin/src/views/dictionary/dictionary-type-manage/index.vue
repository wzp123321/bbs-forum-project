<template>
  <div class="dictionary-type-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="searchList">
      <el-form-item>
        <el-input
          v-model="searchForm.keyword"
          placeholder="类型名称 / 编码"
          clearable
          @keyup.enter="searchList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchList">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="openAddDrawer">新增类型</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="类型名称" min-width="150" />
      <el-table-column prop="code" label="编码" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
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
      class="dtm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="onPageSizeChange"
      @current-change="onPageNumChange"
    />

    <UmAddEditDrawer ref="drawerRef" @saved="fetchList" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { deleteDictionaryTypeApi, pageDictionaryTypesApi } from '@/apis';
import type { DictionaryTypeVO } from '@/apis';
import { SearchForm } from './constant/model';
import { UmAddEditDrawer } from './components';

defineOptions({ name: 'DictionaryTypeManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<DictionaryTypeVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '' });
const drawerRef = ref<InstanceType<typeof UmAddEditDrawer>>();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageDictionaryTypesApi({
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

const openAddDrawer = () => drawerRef.value?.open();

const openEditDrawer = (row: DictionaryTypeVO) => drawerRef.value?.open(row);

const confirmDelete = async (row: DictionaryTypeVO) => {
  try {
    await ElMessageBox.confirm(`确认删除类型 ${row.name} ?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteDictionaryTypeApi(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

fetchList();
</script>

<style lang="less" scoped>
.dtm-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
