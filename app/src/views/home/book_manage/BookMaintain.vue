<script setup>
import {onMounted, reactive, ref, watch} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {deleteBookService, pageQueryBooksService,  updateBookService} from "@/api/book";
import {BOOK_QUERY,formatLanguage} from "@/views/home/book_manage/book";

const currentSelectedBook = ref(null)

const handleEdit = (row) => {
  currentSelectedBook.value = row
  editDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除《${row.name}》这本书吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
    )
  } catch {
    // 点击取消 / 关闭
    return
  }

  if (isLoading.value) return
  isLoading.value = true

  let success = false
  try {
    await deleteBookService({ id: row.id })
    ElMessage.success('删除成功')
    success = true
  } catch (e) {
    ElMessage.error('删除图书失败')
    console.log(e)
  } finally {
    isLoading.value = false
    if (success) {
      await pageQueryBooks()
    }
  }
}

const editDialogVisible = ref(false)
const formRef = ref()

const editFormData = reactive({
  id: null,
  code: '',
  name: '',
  author: '',
  press: '',
  datePress: '',
  isbn: '',
  catalog: '',
  language: null,
  pages: null,
  price: null,
  dateIn: '',
  brief: '',
  cover: '',
  status: '在馆'
})
const newCover = ref(null)

const rules = {
  code: [{ required: true, message: '请输入图书编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  language: [{ required: true, message: '请选择语言', trigger: 'change' }]
}

const handleCoverChange = (file) => {
  const reader = new FileReader()
  reader.onload = () => {
    editFormData.cover = reader.result
  }
  reader.readAsDataURL(file.raw)
  newCover.value = file.raw
}

const handleEditFormConfirm = async () => {
  if (isLoading.value) return

  isLoading.value = true
  await formRef.value.validate()
  let success = false
  try {
    const result = await updateBookService(getFormData())
    ElMessage.success('编辑成功')
    success = true
  } catch (e) {
    ElMessage.error('编辑失败')
    console.log(e)
  } finally {
    isLoading.value = false
    newCover.value = null
    editDialogVisible.value = false
    if (success) {
      await pageQueryBooks()
    }
  }
}

// 获取表单数据
const getFormData = () => {
  const formData = new FormData()
  for (const key in editFormData) {
    formData.append(key, editFormData[key])
  }
  formData.append('cover', newCover.value)
  if (!newCover.value) {
    formData.delete('cover')
  }
  return formData
}

// 监听选中的图书变化
watch(() => currentSelectedBook.value, (newValue) => {
  editFormData.id = newValue.id
  editFormData.code = newValue.code
  editFormData.name = newValue.name
  editFormData.author = newValue.author
  editFormData.press = newValue.press
  editFormData.datePress = newValue.datePress
  editFormData.isbn = newValue.isbn
  editFormData.catalog = newValue.catalog
  editFormData.language = newValue.language
  editFormData.pages = newValue.pages
  editFormData.price = newValue.price
  editFormData.dateIn = newValue.dateIn
  editFormData.brief = newValue.brief
  editFormData.cover = newValue.cover
  editFormData.status = newValue.status
  newCover.value = null
})

const bookQuery = reactive({...BOOK_QUERY})
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)
const books = ref([])

const isLoading = ref(false)

// 分页查询图书
const pageQueryBooks = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await pageQueryBooksService({
      ...bookQuery,
      pageNo: pageNo.value,
      pageSize: pageSize.value
    })
    books.value = result.data.list
    total.value = result.data.total
  } catch (e) {
    ElMessage.error('查询图书失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

// 搜索按钮触发
const handleSearch = () => {
  pageNo.value = 1
  pageQueryBooks()
}

// 页码变化
const handlePageChange = (newPage) => {
  pageNo.value = newPage
  pageQueryBooks()
}

// 每页条数变化
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  pageNo.value = 1
  pageQueryBooks()
}

// 重置搜索表单
const resetBookQuery = () => {
  bookQuery.id = null
  bookQuery.code = ''
  bookQuery.name = ''
  bookQuery.author = ''
  bookQuery.press = ''
  bookQuery.isbn = ''
  bookQuery.catalog = ''
  bookQuery.language = null
  bookQuery.dateIn = ''
  bookQuery.status = ''
}

// 处理表单重置
const handleReset = () => {
  resetBookQuery()
  pageNo.value = 1
  pageQueryBooks()
}

// 初始化
onMounted(async () => {
  pageNo.value = 1
  await pageQueryBooks()
})
</script>

<template>
  <div class="maintain-wrapper">
    <!-- 搜索栏 --->
    <!-- 工具栏 -->
    <el-form :inline="true" :model="bookQuery" class="toolbar-form">
      <el-form-item label="图书编号">
        <el-input v-model="bookQuery.code" placeholder="请输入图书编号" />
      </el-form-item>
      <el-form-item label="书名">
        <el-input v-model="bookQuery.name" placeholder="请输入书名" />
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="bookQuery.author" placeholder="请输入作者" />
      </el-form-item>
      <el-form-item label="语言">
        <el-select v-model="bookQuery.language" placeholder="请选择语言">
          <el-option label="中文" :value="0" />
          <el-option label="英文" :value="1" />
          <el-option label="日文" :value="2" />
          <el-option label="俄文" :value="3" />
          <el-option label="德文" :value="4" />
          <el-option label="法文" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="bookQuery.status" placeholder="请选择状态">
          <el-option label="在馆" value="在馆" />
          <el-option label="借出" value="借出" />
          <el-option label="遗失" value="遗失" />
          <el-option label="变卖" value="变卖" />
          <el-option label="销毁" value="销毁" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 --->
    <el-table :data="books" style="width: 100%">
      <el-table-column label="图书编号" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.code }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="书名" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.name }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="作者" width="80">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.author }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="出版社" width="100">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.press }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="出版日期" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.datePress }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="ISBN书号" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.isbn }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="分类号" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.catalog }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="语言" width="80">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ formatLanguage(scope.row.language) }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="页数" width="60">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.pages }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="价格" width="60">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.price }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="入馆日期" width="120">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.dateIn }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="内容简介" width="80">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.brief }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="封面" width="80">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <img width="40px" height="40px" :src="scope.row.cover" alt="封面"/>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="图书状态" width="80">
        <template #default="scope">
          <div style="display: flex; align-items: center">
            <span style="margin-left: 10px">{{ scope.row.status }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
        v-model="editDialogVisible"
        title="编辑图书"
        width="700px"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="editFormData"
          :rules="rules"
          label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图书编号" prop="code">
              <el-input v-model="editFormData.code" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="书名" prop="name">
              <el-input v-model="editFormData.name" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="editFormData.author" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社" prop="press">
              <el-input v-model="editFormData.press" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="出版日期" prop="datePress">
              <el-date-picker
                  v-model="editFormData.datePress"
                  type="date"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="editFormData.isbn" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="分类号" prop="catalog">
              <el-input v-model="editFormData.catalog" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="语言" prop="language">
              <el-select v-model="editFormData.language" placeholder="请选择语言">
                <el-option label="中文" :value="0" />
                <el-option label="英文" :value="1" />
                <el-option label="日文" :value="2" />
                <el-option label="俄文" :value="3" />
                <el-option label="德文" :value="4" />
                <el-option label="法文" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="页数" prop="pages">
              <el-input-number v-model="editFormData.pages" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number
                  v-model="editFormData.price"
                  :min="0"
                  :precision="2"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="入馆日期" prop="dateIn">
              <el-date-picker
                  v-model="editFormData.dateIn"
                  type="date"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="editFormData.status">
                <el-option label="在馆" value="在馆" />
                <el-option label="借出" value="借出" />
                <el-option label="遗失" value="遗失" />
                <el-option label="变卖" value="变卖" />
                <el-option label="销毁" value="销毁" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="内容简介" prop="brief">
              <el-input
                  v-model="editFormData.brief"
                  type="textarea"
                  :rows="3"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="封面">
              <el-upload
                  :show-file-list="false"
                  :auto-upload="false"
                  :on-change="handleCoverChange"
              >
                <el-button>选择封面</el-button>
              </el-upload>
              <el-image
                  v-if="editFormData.cover"
                  :src="editFormData.cover"
                  style="width: 100px; margin-left: 16px"
                  fit="cover"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditFormConfirm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分页组件 -->
    <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNo"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        :page-sizes="[10, 20, 50, 100]"
        style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<style scoped>
.maintain-wrapper {
  width: 100%;
  height: 100%;
  padding: 20px;
}
</style>