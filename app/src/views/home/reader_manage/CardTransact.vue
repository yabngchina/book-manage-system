<script setup>
import {ref, reactive, onMounted} from 'vue'
import { ElMessage } from 'element-plus'
import {addReaderService, queryAllReaderTypeService, queryReaderService} from "@/api/reader";

// 查询到的读者列表
const readerList = ref([])

const registerDate = ref(new Date())
const photoPreview = ref(null)
const formRef = ref()

const isLoading = ref(false)

const readerTypes = ref([])

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  readerTypeId: [{ required: true, message: '请选择类别', trigger: 'change' }],
  department: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  photo: [{ required: true, message: '请上传照片', trigger: 'change' }]
}

// 新增读者表单项
const form = reactive({
  name: '',
  sex: '',
  readerTypeId: null,
  department: '',
  photo: null
})

// 查询条件
const query = reactive({
  readerTypeId: null,
  department: '',
  name: ''
})

// 查询读者是否存在
const handleQuery = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await queryReaderService(query)
    readerList.value = result.data
    ElMessage.success('查询读者成功')
  } catch (e) {
    ElMessage.error('查询读者失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

// 提交
const handleSubmit = () => {
  if (isLoading.value) return

  formRef.value.validate(async (valid) => {
    if (!valid) return

    const fd = new FormData()
    Object.keys(form).forEach((k) => {
      fd.append(k, form[k])
    })
    let success = false
    try {
      const result = await addReaderService(fd)
      ElMessage.success('办证成功')
      success = true
    } catch (e) {
      ElMessage.error('提交失败')
      console.log(e)
    } finally {
      if (success) {
        // 新增成功，重置表单数据
        formRef.value.resetFields()
        photoPreview.value = null
      }
      isLoading.value = false
    }
  })
}

// 处理照片上传
const handlePhotoChange = (file) => {
  form.photo = file.raw
  photoPreview.value = URL.createObjectURL(file.raw)
}

// 查询所有读者类型
const queryAllReaderType = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    const result = await queryAllReaderTypeService()
    readerTypes.value = result.data
  } catch (e) {
    ElMessage.error('查询读者类型失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await queryAllReaderType()
})
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="page-title">办理借书证</div>
    </template>

    <!-- 查询区域 -->
    <el-card class="block-card" shadow="never">
      <template #header>查询读者</template>

      <el-form :model="query" inline label-width="80px">
        <el-form-item label="读者类别">
          <el-select v-model="query.readerTypeId" clearable style="width: 160px">
            <el-option
                v-for="t in readerTypes"
                :key="t.id"
                :label="t.name"
                :value="t.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="单位">
          <el-input v-model="query.department" placeholder="单位名称" />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="读者姓名" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 查询结果 -->
    <el-card class="block-card" shadow="never">
      <template #header>查询结果</template>

      <el-table
          :data="readerList"
          border
          stripe
          empty-text="暂无查询结果"
      >
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="sex" label="性别" width="80" />
        <el-table-column prop="department" label="单位" />
        <el-table-column prop="status" label="证件状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === '有效'">{{scope.row.status}}</el-tag>
            <el-tag type="warning" v-else-if="scope.row.status === '挂失'">{{scope.row.status}}</el-tag>
            <el-tag type="danger" v-else-if="scope.row.status === '注销'">{{scope.row.status}}</el-tag>
            <el-tag v-else>未知</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 办证区域 -->
    <el-card class="block-card" shadow="hover">
      <template #header>办理新借书证</template>

      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="100px"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="读者姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>

            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio label="男" />
                <el-radio label="女" />
              </el-radio-group>
            </el-form-item>

            <el-form-item label="读者类别" prop="readerTypeId">
              <el-select v-model="form.readerTypeId">
                <el-option
                    v-for="t in readerTypes"
                    :key="t.id"
                    :label="t.name"
                    :value="t.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="单位" prop="department">
              <el-input v-model="form.department" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="办证日期">
              <el-date-picker
                  v-model="registerDate"
                  type="date"
                  disabled
                  style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="读者照片" prop="photo">
              <el-upload
                  :auto-upload="false"
                  :show-file-list="false"
                  :on-change="handlePhotoChange"
              >
                <el-button>上传照片</el-button>
              </el-upload>

              <el-image
                  v-if="photoPreview"
                  :src="photoPreview"
                  class="photo-preview"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-footer">
          <el-button type="success" size="large" @click="handleSubmit">
            确认办证
          </el-button>
        </div>
      </el-form>
    </el-card>
  </el-card>
</template>

<style scoped>
.page-container {
  border-radius: 14px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
}

.block-card {
  margin-bottom: 18px;
  border-radius: 12px;
}

.form-footer {
  text-align: center;
  margin-top: 24px;
}

.photo-preview {
  margin-top: 12px;
  width: 120px;
  height: 160px;
  border-radius: 8px;
}
</style>
