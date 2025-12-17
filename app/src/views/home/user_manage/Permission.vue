<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageQueryReadersService, queryAllReaderTypeService } from '@/api/reader'
import { queryAllAdminRolesService, updateRoleService } from '@/api/system-admin'

// 查询条件
const queryForm = ref({
  id: null,
  name: '',
  department: '',
  readerTypeId: null
})

// 查询结果列表
const readerList = ref([])
const readerTypeOptions = ref([])
const adminRoleOptions = ref([])
const allRoles = ref([])

// 分页参数
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loading = ref(false)

// 弹窗控制
const editVisible = ref(false)
const selectedReader = ref(null)
const selectedRole = ref(null)

// 查询读者
const handleQuery = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await pageQueryReadersService({ ...queryForm.value, pageNo: pageNo.value, pageSize: pageSize.value })
    readerList.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    console.log(e)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询条件
const handleReset = async () => {
  queryForm.value = {
    id: null,
    name: '',
    department: '',
    readerTypeId: null
  }
  pageNo.value = 1
  await handleQuery()
}

// 打开修改权限弹窗
const openEditRole = (reader) => {
  selectedReader.value = reader
  selectedRole.value = null

  // 根据 adminRoles 位运算取第一个权限
  if (reader.adminRoles != null) {
    const roles = [1, 2, 4, 8]  // 示例
    for (const r of roles) {
      if ((reader.adminRoles & r) === r) {
        selectedRole.value = r
        break
      }
    }
  }

  editVisible.value = true
}

// 提交权限修改
const submitRoles = async () => {
  if (!selectedReader.value) return
  if (selectedRole.value == null) {
    ElMessage.warning('请选择一个管理员角色')
    return
  }

  ElMessageBox.confirm('确认修改权限吗？', '权限修改', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await updateRoleService({ readerId: selectedReader.value.id, roleId: selectedRole.value })
      ElMessage.success('权限修改成功')
      editVisible.value = false
      await handleQuery()
    } catch {
      ElMessage.error('权限修改失败')
    } finally {
      loading.value = false
    }
  })
}

// 加载选项数据
const loadOptions = async () => {
  const readerTypeRes = await queryAllReaderTypeService()
  readerTypeOptions.value = readerTypeRes.data || []

  const roleRes = await queryAllAdminRolesService()
  adminRoleOptions.value = roleRes.data || []

  allRoles.value = roleRes.data || []
}

// 获取权限名
const getRoleName = (adminRoles) => {
  const roleName = allRoles.value.find(role => role.id === adminRoles)?.name
  return roleName || '未知'
}

// 获取读者类别名
const getReaderTypeName = (readerTypeId) => {
  return readerTypeOptions.value.find(type => type.id === readerTypeId)?.name || '未知'
}

// 分页事件
const handlePageChange = (val) => {
  pageNo.value = val
  handleQuery()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  pageNo.value = 1
  handleQuery()
}

onMounted(async () => {
  await loadOptions()
  await handleQuery()
})
</script>

<template>
  <div class="page">
    <!-- 查询条件 -->
    <el-card class="query-card" shadow="never">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="借书证号">
          <el-input v-model="queryForm.id" placeholder="证号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryForm.name" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="queryForm.department" placeholder="单位" />
        </el-form-item>
        <el-form-item label="读者类别">
          <el-select v-model="queryForm.readerTypeId" placeholder="请选择">
            <el-option
                v-for="t in readerTypeOptions"
                :key="t.id"
                :label="t.name"
                :value="t.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 查询结果表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
          :data="readerList"
          v-loading="loading"
          stripe
          border
          style="width: 100%"
      >
        <el-table-column prop="id" label="借书证号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="sex" label="性别" width="55" />
        <el-table-column prop="readerTypeId" label="读者类别" width="120">
          <template #default="scope">{{ getReaderTypeName(scope.row.readerTypeId) }}</template>
        </el-table-column>
        <el-table-column prop="department" label="单位" width="180" />
        <el-table-column prop="dateRegister" label="办证日期" width="120" />
        <el-table-column prop="photo" label="读者照片" width="100">
          <template #default="scope">
            <el-image v-if="scope.row.photo" :src="scope.row.photo" fit />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="证件状态" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '有效'" type="success">有效</el-tag>
            <el-tag v-else-if="scope.row.status === '挂失'" type="warning">挂失</el-tag>
            <el-tag v-else type="danger">注销</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowQuantity" label="已借书数量" width="100" />
        <el-table-column prop="adminRoles" label="管理员权限" width="180">
          <template #default="scope">{{ getRoleName(scope.row.adminRoles) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                :disabled="scope.row.adminRoles === 4"
                @click="openEditRole(scope.row)"
            >修改权限</el-button>
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
    </el-card>

    <!-- 修改权限弹窗 -->
    <el-dialog
        v-model="editVisible"
        title="设置管理员权限"
        width="520px"
        destroy-on-close
    >
      <el-form label-width="120px">
        <el-form-item label="读者姓名">
          <el-input v-model="selectedReader.name" disabled />
        </el-form-item>
        <el-form-item label="管理员角色">
          <el-radio-group v-model="selectedRole">
            <el-radio
                v-for="role in adminRoleOptions"
                :key="role.id"
                :label="role.id"
            >{{ role.name }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  background-color: #f5f7fa;
}

.query-card {
  margin-bottom: 16px;
  border-radius: 12px;
}

.query-form .el-form-item {
  margin-right: 20px;
  margin-bottom: 12px;
}

.table-card {
  border-radius: 12px;
}
</style>
