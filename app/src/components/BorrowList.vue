<script setup>
import dayjs from 'dayjs'

const props = defineProps({
  borrows: {
    type: Array,
    required: true
  }
})

const formatDate = (val) => {
  return val ? dayjs(val).format('YYYY-MM-DD HH:mm') : '-'
}
</script>

<template>
  <el-card class="borrow-card">
    <template #header>
      <div class="card-header">
        <span>📚 借阅记录</span>
      </div>
    </template>

    <el-table :data="borrows" stripe border style="width: 100%">
      <el-table-column prop="id" label="借阅ID" width="80" />

      <el-table-column label="图书信息">
        <template #default="scope">
          <div class="book-info">
            <el-image
                v-if="scope.row.book?.cover"
                :src="scope.row.book.cover"
                fit="cover"
                class="book-cover"
            />
            <div class="book-text">
              <div class="title">{{ scope.row.book.name }}</div>
              <div class="sub">作者：{{ scope.row.book.author }}</div>
              <div class="sub">出版社：{{ scope.row.book.press }}</div>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="借阅信息" width="300">
        <template #default="scope">
          <div class="borrow-info">
            <div>📅 借书：{{ formatDate(scope.row.dateOut) }}</div>
            <div>⏳ 应还：{{ formatDate(scope.row.dateRetPlan) }}</div>
            <div v-if="scope.row.hasReturn">
              ✅ 实还：{{ formatDate(scope.row.dateRetAct) }}
            </div>
            <div v-else class="not-return">❌ 未归还</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="140">
        <template #default="scope">
          <el-tag :type="scope.row.hasReturn ? 'success' : 'warning'">
            {{ scope.row.hasReturn ? '已归还' : '借阅中' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="费用" width="200">
        <template #default="scope">
          <div>超期天数：{{ scope.row.overDay }} 天</div>
          <div>应罚款：￥{{ scope.row.overMoney }}</div>
          <div>实罚款：￥{{ scope.row.punishMoney }}</div>
        </template>
      </el-table-column>

      <el-table-column label="操作员" width="200">
        <template #default="scope">
          <div>借书：{{ scope.row.operatorBorrow }}</div>
          <div v-if="scope.row.operatorReturn">还书：{{ scope.row.operatorReturn }}</div>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<style scoped>
.borrow-card {
  width: 100%;
  border-radius: 12px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.book-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 6px;
}

.book-text .title {
  font-weight: bold;
}

.book-text .sub {
  font-size: 12px;
  color: #666;
}

.borrow-info {
  font-size: 13px;
  line-height: 1.6;
}

.not-return {
  color: #e6a23c;
}
</style>