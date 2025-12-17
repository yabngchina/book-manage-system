<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageQueryReadersService,
  queryAllReaderTypeService,
  cancelReaderService
} from '@/api/reader'

// 查询条件
const readerQuery = reactive({
  id: null,
  name: '',
  department: '',
  readerTypeId: null
})

// 分页
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 数据
const readerList = ref([])
const readerTypeOptions = ref([])
const loading = ref(false)

// 注销确认弹窗
const cancelVisible = ref(false)
const currentReader = ref(null)

// 分页+条件查询
const loadReaders = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await pageQueryReadersService({
      ...readerQuery,
      pageNo: pageNo.value,
      pageSize: pageSize.value
    })
    readerList.value = res.data.list
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('查询失败')
    console.log(e)
  } finally {
    loading.value = false
  }
}

// 搜索按钮
const handleSearch = () => {
  pageNo.value = 1
  loadReaders()
}

// 重置按钮
const handleReset = () => {
  readerQuery.id = null
  readerQuery.name = ''
  readerQuery.department = ''
  readerQuery.readerTypeId = null
  readerQuery.status = ''
  pageNo.value = 1
  loadReaders()
}

// 页码变化
const handlePageChange = (val) => {
  pageNo.value = val
  loadReaders()
}

// 每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNo.value = 1
  loadReaders()
}

// 打开注销确认弹窗
const openCancelDialog = (row) => {
  if (row.status === '注销') {
    ElMessage.warning('该借书证已注销')
    return
  }
  if (row.borrowQuantity > 0) {
    ElMessage.warning('存在未归还图书，不能注销')
    return
  }
  currentReader.value = row
  cancelVisible.value = true
}

// 确认注销
const confirmCancel = async () => {
  if (loading.value || !currentReader.value) return
  loading.value = true
  let success = false
  try {
    await cancelReaderService({ id: currentReader.value.id })
    ElMessage.success('注销成功')
    cancelVisible.value = false
    success = true
  } catch {
    ElMessage.error('注销失败')
  } finally {
    loading.value = false
    if (success) await loadReaders()
  }
}

// 获取读者类别
const loadReaderTypes = async () => {
  const res = await queryAllReaderTypeService()
  readerTypeOptions.value = res.data || []
}

onMounted(async () => {
  await loadReaderTypes()
  await loadReaders()
})
</script>

<template>
  <div class="page">
    <!-- 条件搜索 -->
    <el-card class="query-card" shadow="never">
      <el-form :inline="true" :model="readerQuery">
        <el-form-item label="借书证号">
          <el-input v-model="readerQuery.id" placeholder="证号"/>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="readerQuery.name" placeholder="姓名"/>
        </el-form-item>

        <el-form-item label="单位">
          <el-input v-model="readerQuery.department" placeholder="单位"/>
        </el-form-item>

        <el-form-item label="读者类别">
          <el-select v-model="readerQuery.readerTypeId" placeholder="请选择">
            <el-option
                v-for="t in readerTypeOptions"
                :key="t.id"
                :label="t.name"
                :value="t.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
          :data="readerList"
          stripe
          border
          v-loading="loading"
      >
        <el-table-column label="照片" width="90">
          <template #default="scope">
            <el-avatar :src="scope.row.photo" size="56"/>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="借书证号" width="120"/>
        <el-table-column prop="name" label="姓名" width="120"/>
        <el-table-column prop="department" label="单位"/>
        <el-table-column prop="borrowQuantity" label="已借数量" width="100"/>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
                :type="
                scope.row.status === '有效'
                  ? 'success'
                  : scope.row.status === '挂失'
                    ? 'warning'
                    : 'info'
              "
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
                type="danger"
                size="small"
                :disabled="
                scope.row.status === '注销' ||
                scope.row.borrowQuantity > 0
              "
                @click="openCancelDialog(scope.row)"
            >
              注销
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="pageNo"
          :page-size="pageSize"
          :page-sizes="[10,20,50,100]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          style="margin-top: 20px; text-align: right;"
      />
    </el-card>

    <!-- 注销确认弹窗 -->
    <el-dialog
        v-model="cancelVisible"
        title="注销借书证确认"
        width="520px"
        destroy-on-close
    >
      <el-descriptions border :column="2">
        <el-descriptions-item label="借书证号">{{ currentReader?.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentReader?.name }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ currentReader?.department }}</el-descriptions-item>
        <el-descriptions-item label="已借书数量">{{ currentReader?.borrowQuantity }}</el-descriptions-item>
        <el-descriptions-item label="当前状态" :span="2">
          <el-tag type="warning">{{ currentReader?.status }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-alert
          type="warning"
          show-icon
          :closable="false"
          style="margin-top: 16px"
          title="注销后该借书证将永久失效，且不可恢复！"
      />

      <template #footer>
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel">确认注销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  background: #f5f7fa;
}

.query-card {
  margin-bottom: 16px;
  border-radius: 12px;
}

.table-card {
  border-radius: 12px;
}
</style>