<script setup>
import {ref, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {queryBookBorrowRecordService, renewBookService} from '@/api/borrow'
import ReaderTable from "@/components/ReaderTable.vue";
import {queryAllReaderTypeService} from "@/api/reader";
import {queryAllRolesService} from "@/api/system-admin";
import BorrowTable from "@/components/BorrowTable.vue";

// 查询的图书编号
const bookId = ref(null)
// 借阅的图书信息
const bookInfo = ref(null)
// 借阅信息
const borrows = ref([])
// 用户信息
const readers = ref([])
// 当前用户类型信息
const readerType = ref(null)
// 用户类型
const readerTypes = ref([])
// 系统角色
const roles = ref([])
// 是否在加载
const isLoading = ref(false)
// 续借天数
const continueDays = ref(null)
// 续借对话框是否可见
const renewDialogVisible = ref(false)
// 图书信息是否加载成功
const disabledRenewBtn = ref(true)

// 根据输入的bookId查询图书借阅信息
const getBorrowInfo = async () => {
  if (isLoading.value) return
  // 清除原本的readers
  readers.value = []
  borrows.value = []

  if (!bookId.value) {
    ElMessage.warning('请输入图书编号')
    return
  }
  isLoading.value = true
  let success = false
  try {
    const {data: {book, borrow, reader, readerType}} =
        await queryBookBorrowRecordService({bookId: bookId.value})
    bookInfo.value = book
    borrows.value.push(borrow)
    readers.value.push(reader)
    readerType.value = readerType
    ElMessage.success('查询成功')
    disabledRenewBtn.value = false
    success = true
  } catch (e) {
    ElMessage.error('查询借阅信息失败')
    console.log(e)
  } finally {
    isLoading.value = false
    if (!success) {
      bookId.value = null
    }
  }
}

// 获取所有的用户类型
const getAllReaderTypes = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await queryAllReaderTypeService()
    readerTypes.value = result.data
  } catch (e) {
    ElMessage.error('获取用户类型失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}
// 获取所有的系统角色
const getAllRoles = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await queryAllRolesService()
    roles.value = result.data
  } catch (e) {
    ElMessage.error('获取系统角色失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

// 打开续借确认弹窗
const openRenewDialog = () => {
  if (!bookId.value) {
    ElMessage.warning('请输入图书编号')
    return
  }
  renewDialogVisible.value = true
}
// 确认续借
const renewBorrow = async () => {
  if (isLoading.value) return
  isLoading.value = true
  let success = false
  try {
    const result = await renewBookService({
      id: borrows.value[0].id,
      readerId: readers.value[0].id,
      bookId: bookInfo.value.id,
      continueDays: continueDays.value
    })
    ElMessage.success('续借成功')
    success = true
  } catch (e) {
    ElMessage.error('续借失败')
    console.log(e)
  } finally {
    isLoading.value = false
    renewDialogVisible.value = false
    continueDays.value = null
    if (success) {
      await getBorrowInfo()
    }
  }
}

onMounted(async () => {
  await getAllReaderTypes()
  await getAllRoles()
})
</script>

<template>
  <div class="renew-wrapper">
    <!-- 工具栏 -->
    <div class="tool-bar">
      <el-input v-model="bookId" style="width: 120px" clearable placeholder="请输入图书编号"/>
      <el-button type="primary" @click="getBorrowInfo">查询</el-button>
      <el-button type="success" :disabled="disabledRenewBtn" @click="openRenewDialog">续借</el-button>
    </div>

    <!-- 显示的读者表格 -->
    <div class="reader-wrapper">
      <ReaderTable
          :loading="isLoading"
          :roles="roles"
          :reader-types="readerTypes"
          :reader="readers"
      />
    </div>

    <!-- 显示的借阅信息表格 -->
    <BorrowTable :loading="isLoading" :borrow="borrows"/>

    <!-- 续借对话框 -->
    <el-dialog
        v-model="renewDialogVisible"
        title="续借"
        width="500"
        align-center
        v-if="borrows.length > 0 && bookInfo"
    >
      <el-form-item label="借阅编号">
        <el-input v-model="borrows[0].id" disabled />
      </el-form-item>
      <el-form-item label="图书名称">
        <el-input v-model="bookInfo.name" disabled/>
      </el-form-item>
      <el-form-item label="续借天数">
        <el-input v-model="continueDays"/>
      </el-form-item>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="renewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="renewBorrow">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.renew-wrapper {
  width: 100%;
  height: 100%;
  padding: 20px 45px;
  display: flex;
  flex-direction: column;
}

.tool-bar {
  width: 100%;
  height: 33px;
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.reader-wrapper {
  width: 1190px;
  margin: 20px 0;
}
</style>
