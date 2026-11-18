<template>
  <div class="dictionary-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="searchList">
      <el-form-item>
        <el-input
          v-model="searchForm.keyword"
          placeholder="展示名 / 存储值"
          clearable
          @keyup.enter="searchList"
        />
      </el-form-item>
      <el-form-item>
        <el-select v-model="searchForm.typeId" placeholder="全部类型" clearable style="width: 200px" @change="searchList">
          <el-option v-for="t in typeOptions" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchList">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="openAddDrawer">新增字典项</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="typeName" label="所属类型" min-width="130" />
      <el-table-column prop="label" label="展示名" min-width="140" />
      <el-table-column prop="value" label="存储值" min-width="120" />
      <el-table-column prop="sort" label="排序" width="80" align="center" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="(val: string | number | boolean) => toggleStatus(row, Number(val))"
          />
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
      class="dm-pagination"
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
import {
  changeDictionaryStatusApi,
  deleteDictionaryApi,
  listDictionaryTypesApi,
  pageDictionariesApi,
} from '@/apis';
import type { DictionaryTypeVO, DictionaryVO } from '@/apis';
import { SearchForm } from './constant/model';
import { UmAddEditDrawer } from './components';

defineOptions({ name: 'DictionaryManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<DictionaryVO[]>([]);
const typeOptions = ref<DictionaryTypeVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '', typeId: undefined });
const drawerRef = ref<InstanceType<typeof UmAddEditDrawer>>();

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageDictionariesApi({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      typeId: searchForm.typeId,
    });
    dataSource.value = res.list;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

const fetchTypes = async () => {
  try {
    typeOptions.value = await listDictionaryTypesApi();
  } catch {
    typeOptions.value = [];
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
  searchForm.typeId = undefined;
  searchList();
};

const openAddDrawer = () => drawerRef.value?.open();

const openEditDrawer = (row: DictionaryVO) => drawerRef.value?.open(row);

const toggleStatus = async (row: DictionaryVO, status: number) => {
  await changeDictionaryStatusApi(row.id, status);
  row.status = status;
  ElMessage.success(status === 1 ? '已启用' : '已停用');
};

const confirmDelete = async (row: DictionaryVO) => {
  try {
    await ElMessageBox.confirm(`确认删除字典项 ${row.label} ?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteDictionaryApi(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

fetchTypes();
fetchList();
</script>

<style lang="less" scoped>
.dm-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
