<script setup>

import dayjs from "dayjs";

const props = defineProps({
  book: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    required: true
  }
})

const formatDate = (val) => val ? dayjs(val).format('YYYY-MM-DD') : '-'

const LANGUAGE_MAP = {
  0: '中文',
  1: '英文',
  2: '日文',
  3: '俄文',
  4: '德文',
  5: '法文'
}

const formatLanguage = (val) => {
  return LANGUAGE_MAP[val] ?? '未知'
}
</script>

<template>
  <el-table
      :data="book"
      v-loading="loading"
      stripe
      border
      style="width: 100%"
      v-if="book"
  >
    <el-table-column prop="id" label="图书序号" width="120" />
    <el-table-column prop="code" label="图书编号" width="120" />
    <el-table-column prop="cover" label="封面" width="100">
      <template #default="scope">
        <el-image
            v-if="scope.row?.cover"
            :src="scope.row.cover"
            fit
        />
      </template>
    </el-table-column>
    <el-table-column prop="name" label="书名" width="120">
      <template #default="scope">
        <span v-if="scope.row?.name">{{ scope.row.name }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="author" label="作者" width="80"/>
    <el-table-column prop="press" label="出版社" width="120" />
    <el-table-column prop="datePress" label="出版日期" width="120">
      <template #default="scope">
        <span v-if="scope.row.datePress">{{ formatDate(scope.row.datePress) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="isbn" label="ISBN" width="120" />
    <el-table-column prop="catalog" label="分类号" width="90"/>
    <el-table-column prop="language" label="语言" width="60">
      <template #default="scope">
        <span v-if="scope.row.language">{{ formatLanguage(scope.row.language) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="pages" label="页数" width="60"/>
    <el-table-column prop="price" label="价格(元)" width="80"/>
    <el-table-column prop="dateIn" label="入馆日期" width="120">
      <template #default="scope">
        <span v-if="scope.row.dateIn">{{ formatDate(scope.row.dateIn) }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="brief" label="内容简介" width="120"/>
    <el-table-column prop="status" label="状态" width="70">
      <template #default="scope">
        <el-tag type="success" v-if="scope.row.status === '在馆'">在馆</el-tag>
        <el-tag type="info" v-if="scope.row.status === '借出'">借出</el-tag>
        <el-tag type="warning" v-if="scope.row.status === '遗失'">Tag 3</el-tag>
        <el-tag type="info" v-if="scope.row.status === '变卖'">变卖</el-tag>
        <el-tag type="danger" v-if="scope.row.status === '销毁'">销毁</el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<style scoped>

</style>