<template>
  <div class="logs-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">操作日志</h1>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="操作模块">
          <el-select v-model="searchForm.module" placeholder="全部模块" clearable style="width: 160px">
            <el-option label="用户管理" value="用户管理" />
            <el-option label="竞赛管理" value="竞赛管理" />
            <el-option label="报名管理" value="报名管理" />
            <el-option label="评审管理" value="评审管理" />
            <el-option label="奖项管理" value="奖项管理" />
            <el-option label="作品管理" value="作品管理" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作人">
          <el-input
            v-model="searchForm.username"
            placeholder="搜索操作人"
            clearable
            style="width: 160px"
          />
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card class="table-card">
      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column prop="module" label="操作模块" width="120">
          <template #default="scope">
            <el-tag effect="plain" size="small">{{ scope.row.module || '-' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="operation" label="操作类型" width="120">
          <template #default="scope">
            <el-tag :type="getOperationType(scope.row.operation)" size="small" effect="plain">
              {{ scope.row.operation || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="操作人" width="120">
          <template #default="scope">
            <span>{{ scope.row.username || scope.row.operator || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="操作描述" min-width="200">
          <template #default="scope">
            <span>{{ scope.row.description || scope.row.detail || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="ip" label="IP地址" width="140">
          <template #default="scope">
            <span>{{ scope.row.ip || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="操作时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt || scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOperationLogs } from '@/api/super-admin'

const loading = ref(false)
const logs = ref([])
const dateRange = ref(null)

const searchForm = reactive({
  module: '',
  username: '',
  startDate: '',
  endDate: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  fetchLogs()
})

const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (searchForm.module) params.module = searchForm.module
    if (searchForm.username) params.username = searchForm.username
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await getOperationLogs(params)
    logs.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取日志列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchLogs()
}

const handleReset = () => {
  searchForm.module = ''
  searchForm.username = ''
  dateRange.value = null
  pagination.page = 1
  fetchLogs()
}

const getOperationType = (operation) => {
  const typeMap = {
    '新增': 'success',
    '修改': 'warning',
    '删除': 'danger',
    '查询': 'info',
    '登录': 'primary',
    '登出': 'info',
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'LOGIN': 'primary'
  }
  return typeMap[operation] || 'info'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchLogs()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchLogs()
}
</script>

<style scoped>
.logs-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.table-card :deep(.el-table) {
  border-radius: var(--radius-lg);
}

.table-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid var(--border-light);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .filter-form {
    flex-direction: column;
  }
}
</style>
