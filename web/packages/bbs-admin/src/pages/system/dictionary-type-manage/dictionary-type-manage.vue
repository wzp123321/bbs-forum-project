<template>
  <div class="dictionary-type-manage">
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="字典类型：">
        <el-input v-model="searchForm.name" placeholder="字典类型" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button type="primary" @click="handleDialog('Add', {
          name: ''
        })">新增</el-button>
      </el-form-item>
    </el-form>
    <el-divider />
    <el-table :data="table.data" :border="true" style="width: 100%">
      <el-table-column prop="id" label="ID" width="120" />
      <el-table-column prop="name" label="字典类型" />
      <el-table-column fixed="right" label="操作" min-width="120">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleDialog('Detail', scope.row)">
            Detail
          </el-button>
          <el-button link type="primary" size="small" @click="handleDialog('Edit', scope.row)">Edit</el-button>
          <el-button link type="primary" size="small" @click="handleDelete">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="table.currentPage" v-model:page-size="table.pageSize"
      :page-sizes="[10, 20, 30, 40]" background layout="total, sizes, prev, pager, next, jumper" :total="table.total"
      @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    <!-- 详情/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogFlag === 'Edit' ? '编辑弹窗' : '详情弹窗'" width="500" center>
      <el-form label-position="top" label-width="auto" :model="dialogForm" style="max-width: 600px"
        :disabled="dialogFlag === 'Detail'">
        <el-form-item label="字典类型：">
          <el-input v-model="dialogForm.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer" v-if="dialogFlag === 'Edit'">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="dialogVisible = false">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
defineOptions({
  name: 'DictionaryTypeManage',
});

const searchForm = reactive({
  name: '',
})
const table = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 100,
  data: [
    {
      id: 1,
      name: 'Tom',
    },
    {
      id: 2,
      name: 'Tom',
    },
    {
      id: 3,
      name: 'Tom',
    }
  ]
})
const dialogFlag = ref('Edit')
const dialogVisible = ref(false)
const dialogForm = reactive({
  name: ''
})

const onSubmit = () => {
  console.log('submit!')
}

const handleSizeChange = (val: number) => {
  console.log(`${val} items per page`)
}

const handleCurrentChange = (val: number) => {
  console.log(`current page: ${val}`)
}

const handleDialog = (type: string, row: object) => {
  dialogFlag.value = type
  dialogForm.name = row.name
  dialogVisible.value = true
}

const handleDelete = () => {
  ElMessageBox.confirm(
    '确定删除该数据?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      ElMessage({
        type: 'success',
        message: 'Delete completed',
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: 'Delete canceled',
      })
    })
}
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




