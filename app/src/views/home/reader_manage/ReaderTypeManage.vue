<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  pageQueryReaderTypesService,
  addReaderTypeService,
  updateReaderTypeService,
  deleteReaderTypeService
} from '@/api/reader'

const isLoading = ref(false)

// 读者类型列表
const readerTypes = ref([])

// 分页参数
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 删除对话框是否可见
const deleteDialogVisible = ref(false)
// 编辑对话框是否可见
const editDialogVisible = ref(false)
// 新增对话框是否可见
const addDialogVisible = ref(false)

// 当前选中的条目
const currentSelectedReaderType = ref(null)

// 新增表单数据
const addFormData = reactive({
  name: '',
  canLendQuantity: '',
  canLendDay: '',
  canContinueTimes: '',
  punishRate: '',
  dateValid: ''
})
const formLabelWidth = '120px'

// 清空表单数据
const clearFormData = () => {
  addFormData.name = ''
  addFormData.canLendQuantity = ''
  addFormData.canLendDay = ''
  addFormData.canContinueTimes = ''
  addFormData.punishRate = ''
  addFormData.dateValid = ''
}

// 获取分页数据
const loadReaderTypes = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const res = await pageQueryReaderTypesService({ pageNo: pageNo.value, pageSize: pageSize.value })
    readerTypes.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    console.log(e)
    ElMessage.error('获取读者类型失败')
  } finally {
    isLoading.value = false
  }
}

// 分页变化
const handlePageChange = (val) => {
  pageNo.value = val
  loadReaderTypes()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNo.value = 1
  loadReaderTypes()
}

// 新增
const handleAddDialogConfirm = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    await addReaderTypeService(addFormData)
    ElMessage.success('新增成功')
  } catch (e) {
    console.log(e)
    ElMessage.error('新增失败')
  } finally {
    isLoading.value = false
    addDialogVisible.value = false
    clearFormData()
    await loadReaderTypes()
  }
}

// 编辑
const handleEditDialogConfirm = async () => {
  if (isLoading.value || !currentSelectedReaderType.value) return
  isLoading.value = true
  try {
    await updateReaderTypeService(currentSelectedReaderType.value)
    ElMessage.success('编辑成功')
  } catch (e) {
    console.log(e)
    ElMessage.error('编辑失败')
  } finally {
    isLoading.value = false
    editDialogVisible.value = false
    await loadReaderTypes()
  }
}

// 删除
const handleConfirmDelete = async () => {
  if (isLoading.value || !currentSelectedReaderType.value) return
  isLoading.value = true
  try {
    await deleteReaderTypeService({ id: currentSelectedReaderType.value.id })
    ElMessage.success('删除成功')
  } catch (e) {
    console.log(e)
    ElMessage.error('删除失败')
  } finally {
    isLoading.value = false
    deleteDialogVisible.value = false
    await loadReaderTypes()
  }
}

// 打开编辑/删除
const handleEditBtnClick = (row) => {
  currentSelectedReaderType.value = row
  editDialogVisible.value = true
}

const handleDeleteBtnClick = (row) => {
  currentSelectedReaderType.value = row
  deleteDialogVisible.value = true
}

onMounted(() => {
  loadReaderTypes()
})
</script>

<template>
  <div class="type-manage-wrapper">
    <div class="tool-bar">
      <el-button type="success" size="small" @click="addDialogVisible = true">新增</el-button>
    </div>

    <el-table
        v-loading="isLoading"
        :data="readerTypes"
        border
        style="width: 100%"
    >
      <el-table-column prop="name" label="读者类别名称" width="150" />
      <el-table-column prop="canLendQuantity" label="可借书数量" width="140" />
      <el-table-column prop="canLendDay" label="可借书天数" width="120" />
      <el-table-column prop="canContinueTimes" label="可续借次数" width="140" />
      <el-table-column prop="punishRate" label="罚款率（元/天）" width="140" />
      <el-table-column prop="dateValid" label="证书有效期（年）" width="140" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEditBtnClick(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDeleteBtnClick(scope.row)">删除</el-button>
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
        :page-sizes="[10, 20, 50, 100]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
        style="margin-top: 20px; text-align: right;"
    />

    <!-- 删除对话框 -->
    <el-dialog
        v-model="deleteDialogVisible"
        title="确认删除吗"
        width="500"
        align-center
    >
      <span>即将删除该条目</span>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmDelete">确认</el-button>
      </template>
    </el-dialog>

    <!-- 新增对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增读者类别" width="500">
      <el-form :model="addFormData">
        <el-form-item label="读者类别名称" :label-width="formLabelWidth">
          <el-input v-model="addFormData.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可借书数量" :label-width="formLabelWidth">
          <el-input v-model="addFormData.canLendQuantity" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可借书天数" :label-width="formLabelWidth">
          <el-input v-model="addFormData.canLendDay" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可续借次数" :label-width="formLabelWidth">
          <el-input v-model="addFormData.canContinueTimes" autocomplete="off" />
        </el-form-item>
        <el-form-item label="罚款率（元/天）" :label-width="formLabelWidth">
          <el-input v-model="addFormData.punishRate" autocomplete="off" />
        </el-form-item>
        <el-form-item label="证书有效期（年）" :label-width="formLabelWidth">
          <el-input v-model="addFormData.dateValid" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false; clearFormData()">取消</el-button>
        <el-button type="primary" @click="handleAddDialogConfirm">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑读者类别" width="500">
      <el-form :model="currentSelectedReaderType">
        <el-form-item label="读者类别名称" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可借书数量" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.canLendQuantity" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可借书天数" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.canLendDay" autocomplete="off" />
        </el-form-item>
        <el-form-item label="可续借次数" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.canContinueTimes" autocomplete="off" />
        </el-form-item>
        <el-form-item label="罚款率（元/天）" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.punishRate" autocomplete="off" />
        </el-form-item>
        <el-form-item label="证书有效期（年）" :label-width="formLabelWidth">
          <el-input v-model="currentSelectedReaderType.dateValid" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditDialogConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.type-manage-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 23px 27px;
}

.tool-bar {
  width: 100%;
  height: 50px;
}
</style>