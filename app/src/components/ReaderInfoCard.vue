<script setup>
import {computed} from 'vue'

const props = defineProps({
  reader: {
    type: Object,
    required: true
  }
})

/**
 * 读者状态样式
 */
const statusType = (status) => {
  switch (status) {
    case '有效':
      return 'success'
    case '挂失':
      return 'warning'
    case '注销':
      return 'danger'
    default:
      return 'info'
  }
}

/**
 * 读者类型（示例）
 */
const readerTypeText = (id) => {
  switch (id) {
    case 10:
      return '教师'
    case 20:
      return '本科生'
    case 21:
      return '专科生'
    case 30:
      return '硕士研究生'
    case 31:
      return '博士研究生'
    default:
      return '未知'
  }
}

/**
 * 管理角色位运算解析
 * 0-读者、1-借书证管理、2-图书管理、4-借阅管理、8-系统管理
 */
const ADMIN_ROLE_MAP = {
  0: '读者',
  1: '借书证管理员',
  2: '图书管理员',
  3: '借阅管理员',
  4: '系统管理员'
}

const adminRoleText = (role) => {
  return ADMIN_ROLE_MAP[role] || '未知'
}
</script>

<template>
  <el-card class="reader-card">
    <div class="header">
      <el-avatar
          :size="100"
          :src="reader.photo"
          shape="square"
      />
      <div class="base-info">
        <div class="name">{{ reader.name }}</div>
        <div class="meta">
          <el-tag>{{ reader.sex }}</el-tag>
          <el-tag
              :type="statusType(reader.status)"
              style="margin-left: 8px"
          >
            {{ reader.status }}
          </el-tag>
        </div>
      </div>
    </div>

    <el-descriptions
        :column="2"
        border
        style="margin-top: 20px"
    >
      <el-descriptions-item label="读者类型">
        {{ readerTypeText(reader.readerTypeId) }}
      </el-descriptions-item>

      <el-descriptions-item label="所属单位">
        {{ reader.department }}
      </el-descriptions-item>

      <el-descriptions-item label="登记日期">
        {{ reader.dateRegister }}
      </el-descriptions-item>

      <el-descriptions-item label="已借书数量">
        {{ reader.borrowQuantity }}
      </el-descriptions-item>

      <el-descriptions-item label="管理角色" :span="2">
        <el-tag type="info">
          {{ adminRoleText(reader.adminRoles) }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<style scoped>
.reader-card {
  width: 100%;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
}

.base-info {
  margin-left: 20px;
}

.name {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 8px;
}

.meta {
  display: flex;
  align-items: center;
}
</style>