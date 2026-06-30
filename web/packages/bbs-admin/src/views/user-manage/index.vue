<template>
  <bbs-page-container class="user-manage">
    <template #header>
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input v-model="searchForm.keyword" v-inputFilter:text :maxlength="20" placeholder="请输入关键词" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchList">查询</el-button>
          <el-button type="primary" @click="openAddDrawer">新增</el-button>
        </el-form-item>
      </el-form>
    </template>
    <template #content>
      <el-table :data="dataSource" style="width: 100%" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="80" align="left">
          <template #default="{ $index }">
            {{ (pageNum - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户名" align="left" show-overflow-ellipsis />
        <el-table-column prop="email" label="邮箱" align="left" show-overflow-ellipsis />
        <el-table-column prop="phone" label="手机" align="left" />
        <el-table-column label="性别" align="left" width="80">
          <template #default="{ row }">{{ getGenderText(row.gender) }}</template>
        </el-table-column>
        <el-table-column label="注册时间" align="left" width="180">
          <template #default="{ row }">{{ row.createTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="left">
          <template #default="{ row }">
            <el-button text type="primary" @click="openViewDrawer(row)">详情</el-button>
            <el-button text type="primary" @click="openEditDrawer(row)">编辑</el-button>
            <el-button text type="danger" @click="confirmDelete(row)">删除</el-button>
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
        @size-change="onPageSizeChange"
        @current-change="onPageNumChange"
      />
    </template>
  </bbs-page-container>
  <!-- 查看 -->
  <um-view-drawer ref="viewDrawerRef"></um-view-drawer>
  <!-- 新增/编辑 -->
  <um-add-edit-drawer ref="addEditDrawerRef" @saved="searchList"></um-add-edit-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { UmViewDrawer, UmAddEditDrawer } from './components';
import { UserForm, UserInfo } from './constant/model';
import { pageUsersApi, deleteUserApi } from '@/apis/user';

defineOptions({
  name: 'UserManage',
});

// 分页
const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
// 数据源
const dataSource = ref<UserInfo[]>([]);
const loading = ref(false);
// 表单
const searchForm = reactive<UserForm>({
  keyword: '',
});

/** 0未知 1男 2女 */
const getGenderText = (g?: string) => (g === '1' ? '男' : g === '2' ? '女' : '未知');

/** 拉数据 */
const fetchList = async () => {
  loading.value = true;
  try {
    const res = await pageUsersApi({
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

const openAddDrawer = () => {
  addEditDrawerRef.value?.open();
};

const openEditDrawer = (row: UserInfo) => {
  addEditDrawerRef.value?.open(row);
};

const confirmDelete = async (row: UserInfo) => {
  try {
    await ElMessageBox.confirm(`确认删除用户 ${row.userName} ?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await deleteUserApi(row.userId);
  ElMessage.success('删除成功');
  fetchList();
};

// 查看组件
const viewDrawerRef = ref<InstanceType<typeof UmViewDrawer>>();
const openViewDrawer = (row: UserInfo) => {
  viewDrawerRef.value?.open(row);
};

const addEditDrawerRef = ref<InstanceType<typeof UmAddEditDrawer>>();

// 首次进入拉取
fetchList();
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
