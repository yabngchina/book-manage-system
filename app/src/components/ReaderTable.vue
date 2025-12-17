<script setup>

import dayjs from "dayjs";

const props = defineProps({
  reader: {
    type: Array,
    required: true
  },
  readerTypes: {
    type: Array,
    required: true
  },
  roles: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    required: true
  }
})

// 获取读者类别名
const getReaderTypeName = (readerTypeId) => {
  const readerTypeName = props.readerTypes.find(type => {
    return type.id === readerTypeId
  })?.name
  return readerTypeName ? readerTypeName : '未知'
}
// 获取角色名
const getRoleName = (roleId) => {
  const name = props.roles.find(role => {
    return role.id === roleId
  })?.name
  return name ? name : '未知'
}

const formatDate = (val) => val ? dayjs(val).format('YYYY-MM-DD') : '-'
</script>

<template>
  <div class="table-wrapper">
    <el-table
        :data="reader"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        v-if="reader"
    >
      <el-table-column prop="id" label="借书证号" width="120" />
      <el-table-column prop="name" label="姓名" width="120"/>
      <el-table-column prop="sex" label="性别" width="55"/>
      <el-table-column prop="readerTypeId" label="类别" width="120">
        <template #default="scope">
            <span v-if="scope.row.readerTypeId">
              <span>{{ getReaderTypeName(scope.row.readerTypeId) }} </span>
            </span>
        </template>
      </el-table-column>

      <el-table-column prop="department" label="单位" width="180"/>
      <el-table-column prop="dateRegister" label="办证日期" width="120">
        <template #default="scope">
          <span v-if="scope.row.dateRegister">
            <span>{{ formatDate(scope.row.dateRegister) }} </span>
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="photo" label="照片" width="100">
        <template #default="scope">
          <el-image
              v-if="scope.row.photo"
              :src="scope.row.photo"
              fit
          />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="证件状态" width="90">
        <template #default="scope">
            <span v-if="scope.row.status">
              <el-tag v-if="scope.row.status === '有效'" type="success">有效</el-tag>
              <el-tag v-if="scope.row.status === '挂失'" type="warning">挂失</el-tag>
              <el-tag v-if="scope.row.status === '注销'" type="danger">注销</el-tag>
            </span>
        </template>
      </el-table-column>
      <el-table-column prop="borrowQuantity" label="已借数量" width="100"/>
      <el-table-column prop="adminRoles" label="角色" width="180">
        <template #default="scope">
            <span v-if="scope.row.adminRoles">
              <span>{{ getRoleName(scope.row.adminRoles) }} </span>
            </span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>