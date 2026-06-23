<template>
  <div class="posts-manage">
    <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch" class="search-form">
      <el-form-item label="标题">
        <el-input v-model="searchForm.keyword" placeholder="请输入帖子标题" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="板块">
        <el-select v-model="searchForm.categoryId" placeholder="全部板块" clearable style="width: 160px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="searchForm.tagId" placeholder="全部标签" clearable style="width: 160px">
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
          <el-option label="正常" :value="1" />
          <el-option label="已删" :value="0" />
          <el-option label="审核中" :value="2" />
          <el-option label="审核不通过" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" @click="handleAdd">新增帖子</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataSource" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
      <el-table-column label="板块" width="120">
        <template #default="{ row }">{{ row.categoryName || '-' }}</template>
      </el-table-column>
      <el-table-column label="标签" min-width="160">
        <template #default="{ row }">
          <el-tag v-for="n in row.tagNames" :key="n" size="small" effect="plain" class="pm-tag">{{ n }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="作者" width="120">
        <template #default="{ row }">{{ row.userName || row.userId || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="70" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.isTop === 1" @change="(v: boolean | string | number) => onToggleTop(row, v)" />
        </template>
      </el-table-column>
      <el-table-column label="精华" width="70" align="center">
        <template #default="{ row }">
          <el-switch :model-value="row.isEssence === 1" @change="(v: boolean | string | number) => onToggleEssence(row, v)" />
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="70" align="center" />
      <el-table-column prop="commentCount" label="评论" width="70" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pm-pagination"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <PmAddEditDrawer ref="drawerRef" @saved="fetchList" />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { usePagination, COMMON_PAGE_SIZES as pageSizes } from '@bbs/core';
import { categoryApi, postApi, tagApi } from '@/utils';
import type { CategoryVO } from '@/apis/category';
import type { PostVO } from '@/apis/post';
import type { TagVO } from '@/apis/tag';
import { SearchForm } from './constant/model';
import { PmAddEditDrawer } from './components';

defineOptions({ name: 'PostsManage' });

const { pageNum, pageSize, total, setPageNum, setPageSize } = usePagination();
const dataSource = ref<PostVO[]>([]);
const loading = ref(false);
const searchForm = reactive<SearchForm>({ keyword: '', categoryId: undefined, tagId: undefined, status: undefined });
const drawerRef = ref<InstanceType<typeof PmAddEditDrawer>>();

const categories = ref<CategoryVO[]>([]);
const tags = ref<TagVO[]>([]);

const loadFilters = async () => {
  try {
    const [cs, ts] = await Promise.all([categoryApi.listCategories(), tagApi.listTags()]);
    categories.value = cs;
    tags.value = ts;
  } catch {
    // 忽略下拉加载失败,不影响列表
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await postApi.pagePosts({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: searchForm.keyword || undefined,
      categoryId: searchForm.categoryId,
      tagId: searchForm.tagId,
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
  searchForm.categoryId = undefined;
  searchForm.tagId = undefined;
  searchForm.status = undefined;
  handleSearch();
};

const handleAdd = () => {
  drawerRef.value?.handleOpen();
};

const handleEdit = (row: PostVO) => {
  drawerRef.value?.handleOpen(row);
};

const handleDelete = async (row: PostVO) => {
  try {
    await ElMessageBox.confirm(`确认删除帖子《${row.title}》?`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  } catch {
    return;
  }
  await postApi.deletePost(row.id);
  ElMessage.success('删除成功');
  fetchList();
};

const onToggleTop = async (row: PostVO, v: boolean | string | number) => {
  const isTop = v === true || v === 1 || v === '1' ? 1 : 0;
  await postApi.toggleTop(row.id, { isTop });
  ElMessage.success(isTop === 1 ? '已置顶' : '已取消置顶');
  fetchList();
};

const onToggleEssence = async (row: PostVO, v: boolean | string | number) => {
  const isEssence = v === true || v === 1 || v === '1' ? 1 : 0;
  await postApi.toggleEssence(row.id, { isEssence });
  ElMessage.success(isEssence === 1 ? '已设为精华' : '已取消精华');
  fetchList();
};

const statusLabel = (s?: number) => {
  switch (s) {
    case 0: return '已删';
    case 1: return '正常';
    case 2: return '审核中';
    case 3: return '审核不通过';
    default: return '-';
  }
};
const statusType = (s?: number) => {
  switch (s) {
    case 1: return 'success';
    case 2: return 'warning';
    case 3: return 'danger';
    case 0: return 'info';
    default: return 'info';
  }
};

onMounted(() => {
  loadFilters();
  fetchList();
});
</script>

<style lang="less" scoped>
.posts-manage {
  padding: var(--bbs-space-16);

  .search-form {
    margin-bottom: var(--bbs-space-16);
  }

  .pm-tag {
    margin-right: 4px;
  }

  .pm-pagination {
    margin-top: var(--bbs-space-16);
    justify-content: flex-end;
  }
}
</style>
