<script setup>
import {ref} from "vue";
import {useAuthStore} from "@/stores/auth";
import {useReaderInfoStore} from "@/stores/reader-info";
import {ElMessage} from "element-plus";
import {loginService} from "@/api/login";
import router from "@/router";

const accountId = ref('')
const password = ref('')

const isLoading = ref(false)
const authStore = useAuthStore()
const readerInfoStore = useReaderInfoStore()

const handleLogin = async () => {
  if (isLoading.value) return

  isLoading.value = true
  try {
    // 登录
    const {code, message, data} = await loginService({
      accountId: accountId.value,
      password: password.value
    })
    // 登录成功，将响应的jwt存入pinia
    authStore.setToken(data.token)
    // 存储用户信息
    readerInfoStore.setReaderInfo(data.reader)

    // 提示
    ElMessage.success('登录成功')
    // 跳转到首页
    await router.push('/home')
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-page-header">
      <span>欢迎使用图书管理系统</span>
    </div>

    <div class="login-page-body">
      <div class="login-card">
        <el-form label-width="auto" style="max-width: 600px">
          <el-form-item label="账号">
            <el-input v-model="accountId" placeholder="借阅卡id" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="password" type="password" placeholder="请输入密码" show-password/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleLogin">登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="login-page-footer">
      <span>Copyright © 2023</span>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  /* 撑满宽度 */
  width: 100vw;
  /* 撑满高度 */
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f2f4f7;
}

.login-page-header {
  width: 100%;
  height: 20%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.login-page-body {
  width: 100%;
  height: 70%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 600px;
  height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: 0 0 10px #ccc;
}

.login-page-footer {
  width: 100%;
  height: 10%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
</style>