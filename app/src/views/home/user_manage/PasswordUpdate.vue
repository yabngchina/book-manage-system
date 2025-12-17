<script setup>
import {reactive, ref} from 'vue'
import {ElMessage} from "element-plus";
import {updatePasswordService} from "@/api/reader";
import router from '@/router/index'
import {useAuthStore} from "@/stores/auth";
import {useIsAdminStore} from "@/stores/is-admin";
import {useMenuStore} from "@/stores/menu-store";
import {useReaderInfoStore} from "@/stores/reader-info";

const authStore = useAuthStore()
const isAdminStore = useIsAdminStore()
const menuStore = useMenuStore()
const readerInfoStore = useReaderInfoStore()

const isLoading = ref(false)

// 修改密码表单
const updatePwdForm = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
})

// 处理更新密码
const handleUpdatePwd = async () => {
  if (updatePwdForm.newPwd !== updatePwdForm.confirmPwd) {
    ElMessage.error('新密码和确认密码不一致')
    return
  }
  if (isLoading.value) return
  isLoading.value = true
  try {
    // 提交表单
    const result = await updatePasswordService(updatePwdForm)
    ElMessage.success('修改密码成功')
    clearFormData()
    // 清除登录信息，提醒重新登录
    authStore.clearToken()
    isAdminStore.removeIsAdmin()
    menuStore.removeMenus()
    readerInfoStore.removeReaderInfo()
    ElMessage.success('请重新登录')
    await router.push('/login')
  } catch (e) {
    ElMessage.error('修改密码失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

// 清空表单值
const clearFormData = () => {
  updatePwdForm.oldPwd = ''
  updatePwdForm.newPwd = ''
  updatePwdForm.confirmPwd = ''
}
</script>

<template>
  <div class="update-password-wrapper">
    <div class="title-wrapper">
      <h5>修改密码</h5>
    </div>

    <el-form :model="updatePwdForm" label-width="auto" style="max-width: 600px">
      <el-form-item label="旧密码">
        <el-input v-model="updatePwdForm.oldPwd" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="updatePwdForm.newPwd" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="updatePwdForm.confirmPwd" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleUpdatePwd">确认</el-button>
        <el-button @click="clearFormData">取消</el-button>
      </el-form-item>
    </el-form>
  </div>

</template>

<style scoped>
.update-password-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>