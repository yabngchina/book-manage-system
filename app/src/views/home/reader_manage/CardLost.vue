<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  pageQueryReadersService,
  queryAllReaderTypeService,
  updateReaderService,
  reissueReaderCardService
} from '@/api/reader'

// 查询条件
const readerQuery = reactive({
  id: null,
  name: '',
  department: '',
  readerTypeId: null
})

// 分页数据
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 数据列表
const readerList = ref([])
const readerTypeOptions = ref([])

const loading = ref(false)

// 查询方法（分页 + 条件）
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

// 读者状态变更
const handleChangeStatus = (row) => {
  if (row.status === '注销') {
    ElMessage.warning('已注销的借书证不可修改状态')
    return
  }

  const targetStatus = row.status === '有效' ? '挂失' : '有效'
  const actionText = targetStatus === '挂失' ? '挂失' : '恢复'

  ElMessageBox.confirm(
      `确认将【${row.name}】的借书证（${row.id}）${actionText}？`,
      '状态变更确认',
      {
        confirmButtonText: `确认${actionText}`,
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    if (loading.value) return
    loading.value = true
    let success = false
    try {
      const formData = new FormData()
      formData.append('id', row.id)
      formData.append('status', targetStatus)
      await updateReaderService(formData)
      ElMessage.success(`${actionText}成功`)
      success = true
    } catch (e) {
      ElMessage.error(`${actionText}失败`)
    } finally {
      loading.value = false
      if (success) loadReaders()
    }
  })
}

// 补办业务
const reissueVisible = ref(false)
const reissueResult = ref(null)
const handleReissue = (row) => {
  if (row.status !== '挂失') {
    ElMessage.warning('只有挂失状态的借书证才能补办')
    return
  }

  ElMessageBox.confirm(
      `确认为【${row.name}】补办借书证？`,
      '补办确认',
      {
        confirmButtonText: '确认补办',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    if (loading.value) return
    loading.value = true
    try {
      const res = await reissueReaderCardService({id: row.id})
      reissueResult.value = res.data
      reissueVisible.value = true
      ElMessage.success('补办成功')
      await loadReaders()
    } catch (e) {
      ElMessage.error('补办失败')
    } finally {
      loading.value = false
    }
  })
}

// 获取读者类别名称
const getReaderTypeName = (typeId) => {
  return readerTypeOptions.value.find(t => t.id === typeId)?.name
}

// 读取所有读者类别
const loadReaderTypes = async () => {
  const res = await queryAllReaderTypeService()
  readerTypeOptions.value = res.data
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
          <el-input v-model="readerQuery.id" placeholder="请输入借书证号"/>
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="readerQuery.name" placeholder="请输入姓名"/>
        </el-form-item>

        <el-form-item label="单位">
          <el-input v-model="readerQuery.department" placeholder="请输入单位"/>
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

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
          :data="readerList"
          v-loading="loading"
          stripe
          border
      >
        <el-table-column label="照片" width="100">
          <template #default="scope">
            <el-avatar :src="scope.row.photo" size="60"/>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="借书证号" width="120"/>
        <el-table-column prop="name" label="姓名" width="120"/>
        <el-table-column prop="department" label="单位"/>
        <el-table-column prop="readerTypeId" label="读者类别" width="120">
          <template #default="scope">
            {{ getReaderTypeName(scope.row.readerTypeId) }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
                :type="scope.row.status === '有效' ? 'success' : scope.row.status === '挂失' ? 'warning': 'info'"
            >
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-space>
              <el-button
                  :type="scope.row.status === '有效' ? 'danger' : 'success'"
                  size="small"
                  :disabled="scope.row.status === '注销'"
                  @click="handleChangeStatus(scope.row)"
              >
                {{ scope.row.status === '有效' ? '挂失' : '恢复' }}
              </el-button>

              <el-button
                  type="primary"
                  size="small"
                  plain
                  :disabled="scope.row.status !== '挂失'"
                  @click="handleReissue(scope.row)"
              >
                补办
              </el-button>
            </el-space>
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

    <!-- 补办结果弹窗 -->
    <el-dialog
        v-model="reissueVisible"
        title="借书证补办结果"
        width="500px"
        destroy-on-close
    >
      <el-descriptions :column="2" border size="large">
        <el-descriptions-item label="借书证号">{{ reissueResult?.id }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ reissueResult?.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ reissueResult?.sex }}</el-descriptions-item>
        <el-descriptions-item label="读者类别">{{ getReaderTypeName(reissueResult?.readerTypeId) }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ reissueResult?.department }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag type="success">{{ reissueResult?.status }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="办证日期" :span="2">{{ reissueResult?.dateRegister }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button type="primary" @click="reissueVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  background-color: #f5f7fa;
}

.query-card {
  margin-bottom: 16px;
  border-radius: 12px;
}

.table-card {
  border-radius: 12px;
}
</style>
