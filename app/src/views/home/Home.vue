<script setup>
import {useMenuStore} from "@/stores/menu-store";
import router from '@/router/index'
import {onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import {getMenuService} from "@/api/menu";

const menuStore = useMenuStore()

const menus = ref([])
const isLoading = ref(false)

// 获取菜单
const getMenu = async () => {
  if (isLoading.value) return
  isLoading.value = true
  try {
    // 加载菜单
    const result = await getMenuService()
    menus.value = result.data
    menuStore.setMenus(result.data)
  } catch (e) {
    ElMessage.error('获取菜单失败')
    console.log(e)
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await getMenu()
})
</script>

<template>
  <div class="home-page-wrapper">
    <el-row class="tac">
      <el-col>
        <h5 class="mb-2">图书管理系统</h5>

        <el-menu
            router
            :default-active="router.path"
            class="side-menu"
        >
          <el-sub-menu
              v-for="menu in menus"
              :key="menu.path"
              :index="menu.path"
          >
            <template #title>
              {{ menu.name }}
            </template>

            <el-menu-item
                v-for="child in menu.children"
                :key="child.path"
                :index="child.path"
            >
              {{ child.name }}
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-col>
    </el-row>

    <div class="page-view">
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
.home-page-wrapper {
  /* 撑满宽度 */
  width: 100vw;
  /* 撑满高度 */
  height: 100vh;
  display: flex;
  flex-direction: row;
}

.page-view {
  flex: 1;
  height: 100%;
  background-color: #f5f7fa;
}
</style>