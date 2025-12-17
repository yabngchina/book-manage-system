<script setup>
import dayjs from "dayjs";

const props = defineProps({
  borrow: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    required: true
  }
})
const formatDate = (val) => val ? dayjs(val).format('YYYY-MM-DD') : '-'
</script>

<template>
  <div class="borrow-wrapper">
    <el-table
        :data="borrow"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        v-if="borrow"
    >
      <el-table-column prop="id" label="借阅号" width="120" />
      <el-table-column prop="book" label="封面" width="100">
        <template #default="scope">
          <el-image
              v-if="scope.row.book?.cover"
              :src="scope.row.book.cover"
              fit
          />
        </template>
      </el-table-column>
      <el-table-column prop="book" label="书名" width="120">
        <template #default="scope">
          <span v-if="scope.row.book">{{ scope.row.book.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="continueTimes" label="续借次数" width="120"/>
      <el-table-column prop="dateOut" label="借阅日期" width="120">
        <template #default="scope">
          <span v-if="scope.row.dateOut">{{ formatDate(scope.row.dateOut) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="dateRetPlan" label="应还日期" width="120">
        <template #default="scope">
          <span v-if="scope.row.dateRetPlan">{{ formatDate(scope.row.dateRetPlan) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="dateRetAct" label="实际还书日期" width="120">
        <template #default="scope">
          <span v-if="scope.row.dateRetAct">{{ formatDate(scope.row.dateRetAct) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="overDay" label="超期天数" width="120"/>
      <el-table-column prop="overMoney" label="超期金额(元)" width="120"/>
      <el-table-column prop="punishMoney" label="罚款金额(元)" width="120"/>
      <el-table-column prop="hasReturn" label="是否已还" width="120"/>
      <el-table-column prop="operatorBorrow" label="借书操作员" width="120"/>
      <el-table-column prop="operatorReturn" label="还书操作员" width="120">
        <template #default="scope">
            <span v-if="scope.row.operatorReturn">
              <span>{{ scope.row.operatorReturn }} </span>
            </span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>