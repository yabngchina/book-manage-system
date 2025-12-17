<script setup>
import {ref, onMounted, reactive} from 'vue'
import { ElMessage } from 'element-plus'
import {queryAllReaderTypeService, pageQueryReadersService, updateReaderService} from "@/api/reader";
import {Plus} from "@element-plus/icons-vue";

const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 读者列表
const readerList = ref([])
// 读者类别
const readerTypeOptions = ref([])
// 搜索条件
const readerQuery = reactive({
  id: null,
  name: '',
  readerTypeId: null,
  department: '',
})

const isLoading = ref(false)

// 编辑弹窗
const editVisible = ref(false)
const editForm = ref({})
const newAvatarFile = ref(null)

const loadReaders = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await pageQueryReadersService({
      ...readerQuery,
      pageNo: pageNo.value,
      pageSize: pageSize.value
    })
    readerList.value = result.data.list
    total.value = result.data.total
  } catch (e) {
    ElMessage.error('获取读者数据失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

const handleSearch = () => {
  pageNo.value = 1
  loadReaders()
}

const handleReset = () => {
  readerQuery.id = null
  readerQuery.name = ''
  readerQuery.sex = ''
  readerQuery.readerTypeId = null
  readerQuery.department = ''
  readerQuery.status = ''
  pageNo.value = 1
  loadReaders()
}


const handlePageChange = (val) => {
  pageNo.value = val
  loadReaders()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNo.value = 1
  loadReaders()
}

// 获取所有读者类型
const queryAllReaderTypes = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await queryAllReaderTypeService()
    readerTypeOptions.value = result.data
  } catch (e) {
    ElMessage.error('查询读者类型失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

const openEdit = (row) => {
  editForm.value = { ...row }
  newAvatarFile.value = null
  editVisible.value = true
}

const handleAvatarChange = (file) => {
  newAvatarFile.value = file.raw

  // 本地预览
  const reader = new FileReader()
  reader.onload = () => {
    editForm.value.photo = reader.result
  }
  reader.readAsDataURL(file.raw)
}

const submitEdit = async () => {
  if (isLoading.value) return
  isLoading.value = true
  let success =  false
  try {
    const formData = new FormData()
    Object.keys(editForm.value).forEach((k) => {
      formData.append(k, editForm.value[k])
    })
    formData.delete('photo')
    if (newAvatarFile.value) {
      formData.append('photo', newAvatarFile.value)
    }
    // 调用后端更新接口
    const result = await updateReaderService(formData)
    ElMessage.success('读者信息更新成功')
    success = true
  } catch (e) {
    ElMessage.error('更新读者信息失败')
    console.log(e)
  } finally {
    isLoading.value = false
    editVisible.value = false
    if (success) {
      await loadReaders()
    }
  }
}

onMounted(async () => {
  await loadReaders()
  await queryAllReaderTypes()
})

</script>

<template>
  <div class="page">
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">读者信息维护</span>
        </div>
      </template>

      <el-form :inline="true" :model="readerQuery" class="toolbar-form" style="margin-bottom: 20px;">
        <el-form-item label="姓名">
          <el-input v-model="readerQuery.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="读者类别">
          <el-select v-model="readerQuery.readerTypeId" placeholder="请选择">
            <el-option
                v-for="type in readerTypeOptions"
                :key="type.id"
                :label="type.name"
                :value="type.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所在单位">
          <el-input v-model="readerQuery.department" placeholder="请输入单位" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
          :data="readerList"
          stripe
          border
          style="width: 100%"
      >
        <el-table-column prop="id" label="借书证号" width="110" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="sex" label="性别" width="80" />
        <el-table-column prop="readerTypeId" label="读者类别" width="120" />
        <el-table-column prop="department" label="所在单位" />
        <el-table-column prop="status" label="证件状态" width="100" />
        <el-table-column prop="borrowQuantity" label="已借数量" width="100" />
        <el-table-column label="照片" width="120">
          <template #default="scope">
            <el-avatar
                v-if="scope.row.photo"
                :src="scope.row.photo"
                :size="60"
                shape="square"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="openEdit(scope.row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="pageNo"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog
        v-model="editVisible"
        title="变更读者信息"
        width="520px"
        destroy-on-close
    >
      <el-form
          :model="editForm"
          label-width="100px"
          label-position="left"
      >
        <el-form-item label="读者照片">
          <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :auto-upload="false"
              accept="image/*"
              @change="handleAvatarChange"
          >
            <el-avatar
                v-if="editForm.photo"
                :src="editForm.photo"
                :size="80"
                shape="square"
            />
            <el-icon v-else class="avatar-placeholder">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="借书证号">
          <el-input v-model="editForm.id" disabled />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="editForm.name" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="editForm.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="读者类别">
          <el-select v-model="editForm.readerTypeId" placeholder="请选择">
            <el-option
                v-for="type in readerTypeOptions"
                :key="type.id"
                :label="type.name"
                :value="type.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="所在单位">
          <el-input v-model="editForm.department" />
        </el-form-item>

        <el-form-item label="证件状态">
          <el-select v-model="editForm.status">
            <el-option label="有效" value="有效" />
            <el-option label="挂失" value="挂失" />
            <el-option label="注销" value="注销" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  background-color: #f5f7fa;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.avatar-uploader {
  cursor: pointer;
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  border-radius: 6px;
}

</style>