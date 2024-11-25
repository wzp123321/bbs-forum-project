<template>
  <div class="dictionary-manage">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="字典名称：">
        <el-input v-model="searchForm.name" placeholder="名称" clearable />
      </el-form-item>
      <el-form-item label="字典类型：">
        <el-select v-model="searchForm.type" placeholder="类型" style="width: 220px">
          <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button
          type="primary"
          @click="
            handleDialog({
              name: '',
              status: false,
              describe: '',
              type: '',
            })
          "
        >
          新增
        </el-button>
      </el-form-item>
    </el-form>
    <el-divider />
    <el-table :data="tableData" :border="true" style="width: 100%">
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-switch v-model="scope.row.status" disabled active-text="激活" inactive-text="未激活" />
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="describe" label="描述" />
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
  <UmAddEditDrawer ref="UmAddEditDrawerRef" />
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { RowFrom, SearchForm, TableData } from './model';
import { UmAddEditDrawer } from './components';
defineOptions({
  name: 'DictionaryManage',
});

const tableData = ref<TableData[]>([
  {
    id: '1',
    name: 'Tom',
    status: true,
    describe: '1111',
    type: '1',
  },
  {
    id: '2',
    name: 'Tom2',
    status: true,
    describe: '2222',
    type: '2',
  },
]);
const options = [
  {
    value: '1',
    label: '1',
  },
  {
    value: '2',
    label: '2',
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
  type: '',
  name: '',
});
// 查询
const handleSearch = () => {};

const UmAddEditDrawerRef = ref<typeof UmAddEditDrawer>();
// 打开抽屉
const handleDialog = (row: RowFrom) => {
  if (!UmAddEditDrawerRef.value) return;
  UmAddEditDrawerRef.value.handleOpen(row);
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
