<template>
  <div class="registrations-page">
    <!-- 页面头部 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">我的报名</h1>
          <p class="page-subtitle">查看和管理您的竞赛报名记录</p>
        </div>
        <el-button type="primary" size="large" class="action-btn" @click="router.push('/guest/competitions')">
          <el-icon style="margin-right: 6px"><Plus /></el-icon>
          报名竞赛
        </el-button>
      </div>
    </div>

    <!-- 报名列表 -->
    <el-card class="table-card">
      <!-- 状态筛选 -->
      <div class="filter-section">
        <el-radio-group v-model="statusFilter" size="default" @change="handleStatusFilter">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="PENDING">待审核</el-radio-button>
          <el-radio-button label="APPROVED">已通过</el-radio-button>
          <el-radio-button label="REJECTED">已驳回</el-radio-button>
        </el-radio-group>
      </div>
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

        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <span class="status-pill" :class="`pill-${scope.row.status?.toLowerCase()}`">
              <span class="pill-dot"></span>
              {{ getStatusText(scope.row.status) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="报名时间" width="180">
          <template #default="scope">
            <span class="time-text">{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="rejectReason" label="备注" min-width="150">
          <template #default="scope">
            <span class="muted-text">{{ scope.row.rejectReason || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'APPROVED'"
              type="primary"
              link
              size="small"
              class="action-link"
              @click="router.push('/participant/works')"
            >
              提交作品
            </el-button>
            <span v-else-if="scope.row.status === 'PENDING'" class="muted-text">等待审核</span>
            <span v-else class="muted-text">已驳回</span>
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
          background
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
const statusFilter = ref('')

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
      size: pagination.size,
      status: statusFilter.value || undefined
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

const handleStatusFilter = () => {
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
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header-wrapper {
  position: relative;
  padding: 32px 32px 24px;
  margin-bottom: 24px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #1e293b 100%);
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.deco-circle-1 {
  width: 160px;
  height: 160px;
  background: var(--primary-color);
  top: -50px;
  right: -30px;
}

.deco-circle-2 {
  width: 100px;
  height: 100px;
  background: #4facfe;
  bottom: -20px;
  left: 15%;
}

.page-header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.action-btn {
  flex-shrink: 0;
  padding: 10px 24px;
  border-radius: var(--radius-md);
  font-weight: 600;
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.4);
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.55) !important;
}

/* 表格卡片 */
.table-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.table-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
  font-size: 13px;
}

.competition-link {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.competition-link:hover {
  color: var(--primary-700);
  text-decoration: underline;
}

.muted-text {
  font-size: 13px;
  color: var(--text-placeholder);
}

.time-text {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 状态胶囊 */
.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.pill-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.pill-pending {
  background: rgba(230, 162, 60, 0.12);
  color: #d48806;
}
.pill-pending .pill-dot { background: #e6a23c; }

.pill-approved {
  background: rgba(103, 194, 58, 0.12);
  color: #389e0d;
}
.pill-approved .pill-dot { background: #67c23a; }

.pill-rejected {
  background: rgba(245, 108, 108, 0.12);
  color: #cf1322;
}
.pill-rejected .pill-dot { background: #f56c6c; }

.action-link {
  font-weight: 500;
}

/* 筛选区域 */
.filter-section {
  padding: 16px 0 8px;
  margin-bottom: 8px;
}
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 24px 0 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header-wrapper {
    padding: 24px 20px 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-title {
    font-size: 22px;
  }
}
</style>
