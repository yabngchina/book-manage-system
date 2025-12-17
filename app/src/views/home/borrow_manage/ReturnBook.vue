<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {queryReaderNotReturnBookService} from '@/api/borrow'
import {returnBookService} from '@/api/borrow'
import ReaderTable from "@/components/ReaderTable.vue";
import {queryAllReaderTypeService} from "@/api/reader";
import {queryAllRolesService} from "@/api/system-admin";

const readerId = ref(null)
const loading = ref(false)

const readerInfo = ref([])
const borrowList = ref([])

// 还书对话框
const returnDialogVisible = ref(false)
const currentBorrow = ref(null)

// 查询未还书
const queryBorrowInfo = async () => {
  if (loading.value) return
  if (!readerId.value) {
    ElMessage.warning('请输入读者编号')
    return
  }
  readerInfo.value = []
  loading.value = true
  try {
    const res = await queryReaderNotReturnBookService({readerId: readerId.value})
    readerInfo.value.push(res.data.reader)
    borrowList.value = res.data.borrows
  } catch (e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 点击还书
const handleReturnClick = (row) => {
  currentBorrow.value = row
  returnDialogVisible.value = true
}

// 确认还书
const confirmReturn = async () => {
  const borrow = currentBorrow.value
  if (!borrow) return

  try {
    await returnBookService({
      id: borrow.id,
      readerId: borrow.readerId,
      bookId: borrow.bookId
    })
    ElMessage.success('还书成功')
    returnDialogVisible.value = false
    await queryBorrowInfo()
  } catch (e) {
    ElMessage.error('还书失败')
  }
}

// 计算总罚款
const getTotalPunish = () => {
  if (!currentBorrow.value) return 0
  const over = currentBorrow.value.overMoney || 0
  const punish = currentBorrow.value.punishMoney || 0
  return Number(over) + Number(punish)
}

// 用户类型
const readerTypes = ref([])
// 系统角色
const roles = ref([])
// 获取所有的用户类型
const getAllReaderTypes = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const result = await queryAllReaderTypeService()
    readerTypes.value = result.data
  } catch (e) {
    ElMessage.error('获取用户类型失败')
    console.log(e)
  } finally {
    loading.value = false
  }
}
// 获取所有的系统角色
const getAllRoles = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const result = await queryAllRolesService()
    roles.value = result.data
  } catch (e) {
    ElMessage.error('获取系统角色失败')
    console.log(e)
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  await getAllReaderTypes()
  await getAllRoles()
})
</script>

<template>
  <div class="return-book-page">
    <!-- 查询栏 -->
    <div class="toolbar">
      <el-input
          v-model="readerId"
          placeholder="请输入读者编号"
          style="width: 200px"
          clearable
      />
      <el-button type="primary" @click="queryBorrowInfo">查询</el-button>
    </div>

    <!-- 读者信息 -->
    <div class="reader-info">
      <ReaderTable :loading="loading" :roles="roles" :reader-types="readerTypes" :reader="readerInfo" />
    </div>
<!--    <el-card v-if="readerInfo" class="reader-card">
      <div>姓名：{{ readerInfo.name }}</div>
      <div>单位：{{ readerInfo.department }}</div>
      <div>证件状态：{{ readerInfo.status }}</div>
    </el-card>-->

    <el-table
        :data="borrowList"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 16px"
    >
      <el-table-column prop="id" label="借阅编号" width="100"/>
      <el-table-column prop="book.name" label="书名"/>
      <el-table-column prop="dateOut" label="借阅日期"/>
      <el-table-column prop="dateRetPlan" label="应还日期"/>
      <el-table-column prop="overDay" label="超期天数" width="100"/>
      <el-table-column prop="overMoney" label="超期罚款" width="120"/>
      <el-table-column prop="punishMoney" label="遗失罚款" width="120"/>

      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button
              type="danger"
              size="small"
              @click="handleReturnClick(scope.row)"
          >
            还书
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
        v-model="returnDialogVisible"
        title="还书确认"
        width="400px"
        :close-on-click-modal="false"
    >
      <div v-if="currentBorrow">
        <p>书名：{{ currentBorrow.book?.name }}</p>
        <p>超期天数：{{ currentBorrow.overDay }} 天</p>
        <p>超期罚款：{{ currentBorrow.overMoney || 0 }} 元</p>
        <p>遗失罚款：{{ currentBorrow.punishMoney || 0 }} 元</p>

        <el-divider/>

        <p style="font-weight: bold">
          应缴总罚款：{{ getTotalPunish() }} 元
        </p>

        <el-alert
            type="warning"
            show-icon
            title="请确认读者已缴纳罚款"
        />
      </div>

      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确认还书</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.return-book-page {
  padding: 20px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.reader-card {
  margin-bottom: 16px;
}
</style>