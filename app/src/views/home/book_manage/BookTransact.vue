<script setup>
import {reactive, ref} from 'vue'
import {addBookService} from "@/api/book";
import {ElMessage} from "element-plus";

const fileList = ref([])

// 新增图书表单数据
const addBookForm = reactive({
  code: '',
  name: '',
  author: '',
  press: '',
  datePress: '',
  isbn: '',
  catalog: '',
  language: '',
  pages: '',
  price: '',
  brief: '',
})
const cover = ref()

const isLoading = ref(false)

const addBook = async () => {
  if (isLoading.value) return

  isLoading.value = true
  try {
    // 添加图书
    const result = await addBookService(reactiveToFormData())
    ElMessage.success('添加图书成功')
    clearFormData()
  } catch (e) {
    ElMessage.error('添加图书失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

const clearFormData = () => {
  addBookForm.code = ''
  addBookForm.name = ''
  addBookForm.author = ''
  addBookForm.press = ''
  addBookForm.datePress = ''
  addBookForm.isbn = ''
  addBookForm.catalog = ''
  addBookForm.language = ''
  addBookForm.pages = ''
  addBookForm.price = ''
  addBookForm.brief = ''
  cover.value = ''
}

const handleFileChange = (file, files) => {
  // 原生 File 对象
  cover.value = file.raw
}

const reactiveToFormData = () => {
  const formData = new FormData()
  for (const key in addBookForm) {
    formData.append(key, addBookForm[key])
  }
  // 添加封面
  formData.append('cover', cover.value)
  return formData
}
</script>

<template>
  <div class="transact-wrapper">
    <el-form :model="addBookForm" label-width="auto" style="max-width: 600px">
      <el-form-item label="图书编号">
        <el-input v-model="addBookForm.code"/>
      </el-form-item>
      <el-form-item label="书名">
        <el-input v-model="addBookForm.name"/>
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="addBookForm.author"/>
      </el-form-item>
      <el-form-item label="出版社">
        <el-input v-model="addBookForm.press"/>
      </el-form-item>
      <el-form-item label="出版日期">
        <el-col :span="11">
          <el-date-picker
              v-model="addBookForm.datePress"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
              value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-col>
      </el-form-item>
      <el-form-item label="isbn">
        <el-input v-model="addBookForm.isbn"/>
      </el-form-item>
      <el-form-item label="分类号">
        <el-input v-model="addBookForm.catalog"/>
      </el-form-item>
      <el-form-item label="语言">
        <el-select v-model="addBookForm.language" placeholder="请选择图书语言">
          <el-option label="中文" value="0"/>
          <el-option label="英文" value="1"/>
          <el-option label="日文" value="2"/>
          <el-option label="俄文" value="3"/>
          <el-option label="德文" value="4"/>
          <el-option label="法文" value="5"/>
        </el-select>
      </el-form-item>
      <el-form-item label="页数">
        <el-input v-model="addBookForm.pages"/>
      </el-form-item>
      <el-form-item label="价格">
        <el-input v-model="addBookForm.price"/>
      </el-form-item>
      <el-form-item label="内容简介">
        <el-input v-model="addBookForm.brief"/>
      </el-form-item>
      <el-form-item label="封面">
        <el-upload
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="addBook">创建</el-button>
        <el-button @click="clearFormData">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.transact-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
  padding: 30px 50px;
}
</style>