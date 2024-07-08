<template>
  <div class="dictionary-manage">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="发帖用户：">
        <el-input v-model="searchForm.name" placeholder="发帖用户" clearable />
      </el-form-item>
      <el-form-item label="帖子标题：">
        <el-input v-model="searchForm.title" placeholder="帖子标题" clearable />
      </el-form-item>
      <el-form-item label="帖子类型：">
        <el-select v-model="searchForm.type" placeholder="帖子类型" style="width: 220px">
          <el-option v-for="item in typeList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="帖子状态：">
        <el-select v-model="searchForm.status" placeholder="帖子状态" style="width: 220px">
          <el-option v-for="item in statusList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="primary" @click="handleDialog()">新增</el-button>
      </el-form-item>
    </el-form>
    <el-divider />
    <el-table :data="tableData" :border="true" style="width: 100%">
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          {{ statusList.find((item) => item.value === scope.row.status)?.label }}
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型">
        <template #default="scope">
          {{ typeList.find((item) => item.value === scope.row.type)?.label }}
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" min-width="120">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleDialog(scope.row)">编辑</el-button>
          <el-button link type="primary" size="small" @click="handleDelete">删除</el-button>
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
  </div>
  <!-- 新增/编辑弹窗 -->
  <UmDetailsDrawer ref="UmDetailsDrawerRef" />
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { SearchForm, TableData } from './model';
import { UmDetailsDrawer } from './components';
defineOptions({
  name: 'DictionaryManage',
});

// 表格数据
const tableData = ref<TableData[]>([
  {
    id: '1',
    name: '1',
    title: '1',
    content: '1',
    status: 1,
    type: 1,
    purview: '11',
  },
  {
    id: '2',
    name: '1',
    title: '1',
    content: '1',
    status: 1,
    type: 1,
    purview: '11',
  },
]);
// 帖子类型列表
const typeList = [
  {
    value: 1,
    label: '11',
  },
  {
    value: 2,
    label: '22',
  },
];
// 帖子状态列表
const statusList = [
  {
    value: 1,
    label: '11',
  },
  {
    value: 2,
    label: '22',
  },
];

// 分页
const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
// 修改条数
const handleSizeChange = (value: number) => {
  setPageSize(value);
  handleSearch();
};
// 修改页码
const handleCurrentChange = (value: number) => {
  setPageNum(value);
  handleSearch();
};

// 查询表单
const searchForm = reactive<SearchForm>({
  type: 1,
  name: '',
  title: '',
  status: 1,
});
// 查询
const handleSearch = () => {};

const UmDetailsDrawerRef = ref<typeof UmDetailsDrawer>();
// 打开抽屉
const handleDialog = (row?: TableData) => {
  if (!UmDetailsDrawerRef.value) return;
  UmDetailsDrawerRef.value.handleOpen(row);
};
// 删除
const handleDelete = () => {
  ElMessageBox.confirm('确定删除该数据?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      ElMessage({
        type: 'success',
        message: '删除 completed',
      });
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '删除 canceled',
      });
    });
};
</script>

<style lang="less" scoped>
.dictionary-manage {
  height: 100%;
  padding: 20px;
  background: #fff;
  border-radius: 10px;

  .search-form .el-input {
    --el-input-width: 220px;
  }
}
</style>
