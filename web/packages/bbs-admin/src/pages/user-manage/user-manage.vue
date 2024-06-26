<template>
  <bbs-page-container class="user-manage">
    <template #header>
      <section class="um-btn">
        <el-button>新增</el-button>
      </section>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.keyword" placeholder="请输入关键词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </template>
    <template #content>
      <el-table :data="dataSource" style="width: 100%" stripe>
        <el-table-column type="index" label="序号" width="80" align="left">
          <template #default="{ $index }">
            {{ (pageNum - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" align="left" show-overflow-ellipsis />
        <el-table-column label="操作" width="240" align="left">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleView(row)">详情</el-button>
            <el-button text type="primary">编辑</el-button>
            <el-button text type="danger">删除</el-button>
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
  <!-- 查看 -->
  <um-view-drawer ref="viewDrawerRef"></um-view-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { UserForm, UserInfo } from './model';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';

import { UmViewDrawer, UmAddEditDrawer } from './components';

defineOptions({
  name: 'UserManage',
});
// 分页
const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
// 数据源
const dataSource = ref<UserInfo[]>([
  {
    id: 1,
    username: '11',
    sex: '1',
    avatar: '',
  },
  {
    id: 2,
    username: '2311',
    sex: '1',
    avatar: '',
  },
]);
// 表单
const searchForm = reactive<UserForm>({
  keyword: '',
});
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
 * 查询
 */
const handleSearch = () => {};

// 查看组件
const viewDrawerRef = ref<InstanceType<typeof UmViewDrawer>>();
/**
 * 查看
 * @param {UserInfo} row
 */
const handleView = (row: UserInfo) => {
  viewDrawerRef.value?.handleOpen(row);
};
</script>

<style lang="less" scoped>
.user-manage {
  :deep(.bpc-header) {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-form {
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }

  .bpc-content {
    overflow: hidden;

    .el-table {
      flex: auto;
    }
  }
}
</style>
