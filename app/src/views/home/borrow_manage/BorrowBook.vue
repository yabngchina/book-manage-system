<script setup>
import {onMounted, reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {borrowBookService, queryReaderNotReturnBookService} from "@/api/borrow";
import ReaderTable from "@/components/ReaderTable.vue";
import {queryAllRolesService} from "@/api/system-admin";
import {queryAllReaderTypeService} from "@/api/reader";
import BorrowTable from "@/components/BorrowTable.vue";
import BookTable from "@/components/BookTable.vue";
import {queryBookByIdService} from "@/api/book";

const rules = {
  readerId: [{ required: true, message: '请输入读者编号', trigger: 'blur' }],
  bookId: [{ required: true, message: '请输入图书编号', trigger: 'blur' }],
  dateRetPlan: [{ required: true, message: '请选择应还日期', trigger: 'change' }]
}

const readerId = ref(null)
const bookId = ref(null)
const books = ref([])
const readers = ref([])
const borrows = ref([])

const isLoading = ref(false)
const borrowBookDialogVisible = ref(false)
const borrowDialogFormRef = ref(null)
const borrowBookForm = reactive({
  readerId: null,
  bookId: null,
})

const handleQuery = async () => {
  await queryReaderNotReturnBook()
  await queryBookById()
}
// 查询用户未归还的书
const queryReaderNotReturnBook = async () => {
  if (isLoading.value) return
  if (!readerId.value) {
    ElMessage.error('请输入读者编号')
    return
  }
  readers.value = []
  borrows.value = []
  borrowBookForm.dateRetPlan = null
  isLoading.value = true
  try {
    const result = await queryReaderNotReturnBookService({ readerId: readerId.value})
    readers.value.push(result.data.reader)
    borrows.value.push(...result.data.borrows)
  } catch (e) {
    ElMessage.error('查询失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}
// 根据输入id查询图书
const queryBookById = async () => {
  if (isLoading.value) return
  if (!bookId.value) return
  books.value = []
  isLoading.value = true
  try {
    const result = await queryBookByIdService({ id: bookId.value})
    books.value.push(result.data)
  } catch (e) {
    ElMessage.error('查询图书失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

// 确认借书
const handelConfirmBorrow = async () => {
  if (isLoading.value) return

  borrowDialogFormRef.value.validate(async (valid) => {
    if (!valid) return
    isLoading.value = true
    try {
      const result = await borrowBookService(borrowBookForm)
      ElMessage.success('借书成功')
      clearBorrowFormData()
    } catch (e) {
      ElMessage.error('借书失败')
      console.log(e)
    } finally {
      isLoading.value = false
      borrowBookDialogVisible.value = false
    }
  })
}
// 清空表单数据
const clearBorrowFormData = () => {
  borrowBookForm.readerId = null
  borrowBookForm.bookId = null
  borrowBookForm.dateRetPlan = null
}

// 用户类型
const readerTypes = ref([])
// 系统角色
const roles = ref([])
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

// 监听readerId变化
watch(() => readerId.value, (newVal) => {
  borrowBookForm.readerId = newVal
})
// 监听bookId变化
watch(() => bookId.value, (newVal) => {
  borrowBookForm.bookId = newVal
})

onMounted(async () => {
  await getAllReaderTypes()
  await getAllRoles()
})
</script>

<template>
 <div class="borrows-wrapper">
   <!-- 查询栏 -->
   <div class="tool-bar">
     <el-input v-model="readerId" style="width: 120px" clearable placeholder="请输入读者编号"/>
     <el-input v-model="bookId" style="width: 120px" clearable placeholder="请输入图书编号"/>
     <el-button type="primary" @click="handleQuery">查询</el-button>
     <el-button type="success" @click="borrowBookDialogVisible = true">借书</el-button>
   </div>

   <!-- 图书信息 -->
   <div class="book-wrapper">
     <span>图书信息</span>
     <BookTable :loading="isLoading" :book="books" />
   </div>
   <!-- 读者信息 -->
   <div class="reader-wrapper">
     <span>读者信息</span>
     <ReaderTable :loading="isLoading" :roles="roles" :reader-types="readerTypes" :reader="readers" />
   </div>
   <!-- 借书信息 -->
    <div class="borrow-wrapper">
      <span>借阅信息</span>
      <BorrowTable :loading="isLoading" :borrow="borrows" />
    </div>

   <el-dialog
       v-model="borrowBookDialogVisible"
       title="借书"
       width="480px"
       :close-on-click-modal="false"
   >
     <el-form
         ref="borrowDialogFormRef"
         :model="borrowBookForm"
         :rules="rules"
         label-width="90px"
         class="borrow-form"
     >
       <el-form-item label="读者编号" prop="readerId">
         <el-input
             v-model="borrowBookForm.readerId"
             placeholder="请输入读者编号"
             clearable
         />
       </el-form-item>

       <el-form-item label="图书编号" prop="bookId">
         <el-input
             v-model="borrowBookForm.bookId"
             placeholder="请输入图书编号"
             clearable
         />
       </el-form-item>
     </el-form>

     <template #footer>
       <div class="dialog-footer">
         <el-button @click="clearBorrowFormData();borrowBookDialogVisible = false">取消</el-button>
         <el-button type="primary" @click="handelConfirmBorrow">确认借书</el-button>
       </div>
     </template>
   </el-dialog>
 </div>
</template>

<style scoped>
.borrows-wrapper {
  width: 100%;
  height: 100%;
  padding: 20px 20px;
  display: flex;
  flex-direction: column;
}

.tool-bar {
  width: 100%;
  height: 33px;
  margin: 10px 0;
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.reader-wrapper {
  width: 1190px;
  margin: 20px 0;
}
</style>