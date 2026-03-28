<template>
  <div class="registrations-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">我的报名</h1>
      <el-button type="primary" @click="router.push('/guest/competitions')">
        <el-icon><Plus /></el-icon>
        <span>报名竞赛</span>
      </el-button>
    </div>

    <!-- 报名列表 -->
    <el-card class="table-card">
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column prop="competitionName" label="竞赛名称" min-width="180">
          <template #default="scope">
            <span
              class="competition-link"
              @click="router.push(`/guest/competition/${scope.row.competitionId}`)"
            >
              {{ scope.row.competitionName || '-' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="projectName" label="项目名称" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.projectName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="advisor" label="指导老师" width="120">
          <template #default="scope">
            <span>{{ scope.row.advisor || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="报名时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="rejectReason" label="备注" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.rejectReason || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'APPROVED'"
              type="primary"
              link
              size="small"
              @click="router.push('/participant/works')"
            >
              提交作品
            </el-button>
            <span v-else-if="scope.row.status === 'PENDING'" class="text-muted">等待审核</span>
            <span v-else class="text-muted">已驳回</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRegistrations } from '@/api/registration'

const router = useRouter()

const loading = ref(false)
const registrations = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  fetchRegistrations()
})

const fetchRegistrations = async () => {
  loading.value = true
  try {
    const res = await getRegistrations({
      page: pagination.page,
      size: pagination.size
    })
    registrations.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取报名列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchRegistrations()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchRegistrations()
}
</script>

<style scoped>
.registrations-page {
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

.competition-link {
  color: var(--el-color-primary);
  cursor: pointer;
}

.competition-link:hover {
  text-decoration: underline;
}

.text-muted {
  font-size: 13px;
  color: var(--text-placeholder);
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
}
</style>
