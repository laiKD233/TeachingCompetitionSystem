<template>
  <div class="dashboard-page">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ userStore.user?.name }}</h1>
      <p class="welcome-desc">{{ currentDate }}</p>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card users-card">
        <div class="stat-icon users-icon">
          <el-icon :size="32"><User /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">用户总数</p>
          <p class="stat-value">{{ stats.totalUsers }}</p>
          <p class="stat-desc">注册用户</p>
        </div>
      </div>

      <div class="stat-card competitions-card">
        <div class="stat-icon competitions-icon">
          <el-icon :size="32"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">竞赛总数</p>
          <p class="stat-value">{{ stats.totalCompetitions }}</p>
          <p class="stat-desc">已创建竞赛</p>
        </div>
      </div>

      <div class="stat-card registrations-card">
        <div class="stat-icon registrations-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">待审核报名</p>
          <p class="stat-value">{{ stats.pendingRegistrations }}</p>
          <p class="stat-desc">需要审核</p>
        </div>
      </div>

      <div class="stat-card reviews-card">
        <div class="stat-icon reviews-icon">
          <el-icon :size="32"><Edit /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">待评审作品</p>
          <p class="stat-value">{{ stats.pendingReviews }}</p>
          <p class="stat-desc">分配评审中</p>
        </div>
      </div>

      <div class="stat-card works-card">
        <div class="stat-icon works-icon">
          <el-icon :size="32"><Upload /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">作品总数</p>
          <p class="stat-value">{{ stats.totalWorks }}</p>
          <p class="stat-desc">已提交作品</p>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>快捷操作</h3>
        </div>
      </template>

      <div class="quick-actions-grid">
        <div class="quick-action-item" @click="router.push('/admin/competitions')">
          <div class="action-icon competition-action">
            <el-icon :size="24"><Trophy /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">竞赛管理</p>
            <p class="action-desc">创建和管理竞赛</p>
          </div>
        </div>

        <div class="quick-action-item" @click="router.push('/admin/registrations')">
          <div class="action-icon registration-action">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">报名审核</p>
            <p class="action-desc">审核报名申请</p>
          </div>
        </div>

        <div class="quick-action-item" @click="router.push('/admin/reviews')">
          <div class="action-icon review-action">
            <el-icon :size="24"><Edit /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">评审分配</p>
            <p class="action-desc">分配评审任务</p>
          </div>
        </div>

        <div class="quick-action-item" @click="router.push('/admin/results')">
          <div class="action-icon result-action">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">成绩公示</p>
            <p class="action-desc">发布竞赛成绩</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近竞赛 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>最近竞赛</h3>
          <el-button type="primary" text @click="router.push('/admin/competitions')">
            管理全部
          </el-button>
        </div>
      </template>

      <el-table :data="recentCompetitions" v-loading="loading" stripe>
        <el-table-column prop="name" label="竞赛名称" min-width="180" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getAdminCompetitions } from '@/api/competition'
import { getDashboardStats } from '@/api/super-admin'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

const stats = ref({
  totalCompetitions: 0,
  pendingRegistrations: 0,
  pendingReviews: 0,
  totalWorks: 0,
  totalUsers: 0
})

const recentCompetitions = ref([])

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日')
})

onMounted(() => {
  fetchDashboardData()
})

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const [statsRes, competitionsRes] = await Promise.allSettled([
      getDashboardStats(),
      getAdminCompetitions({ page: 1, size: 5 })
    ])

    if (statsRes.status === 'fulfilled' && statsRes.value.data) {
      const data = statsRes.value.data
      stats.value.totalCompetitions = data.totalCompetitions ?? 0
      stats.value.pendingRegistrations = data.pendingRegistrations ?? 0
      stats.value.pendingReviews = data.pendingReviews ?? 0
      stats.value.totalWorks = data.totalWorks ?? 0
      stats.value.totalUsers = data.totalUsers ?? 0
    }

    if (competitionsRes.status === 'fulfilled' && competitionsRes.value.data) {
      recentCompetitions.value = competitionsRes.value.data.records || []
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'primary',
    'REGISTRATION': 'success',
    'ONGOING': 'warning',
    'REVIEWED': 'info',
    'ANNOUNCED': 'primary',
    'FINISHED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'REGISTRATION': '报名中',
    'ONGOING': '进行中',
    'REVIEWED': '已评审',
    'ANNOUNCED': '已公示',
    'FINISHED': '已结束'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

.welcome-section {
  margin-bottom: 32px;
  background: linear-gradient(135deg, var(--slate-800), var(--primary-700));
  padding: 36px 32px;
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: pulse 6s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.1; }
}

.welcome-title {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-white);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
  position: relative;
  z-index: 1;
}

.welcome-desc {
  font-size: 16px;
  color: rgba(255,255,255,0.8);
  margin: 0;
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 28px 24px;
  border-radius: var(--radius-xl);
  background: var(--bg-white);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
  border: 1px solid var(--border-light);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-500), var(--primary-700));
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--text-white);
  box-shadow: var(--shadow-md);
  transition: transform var(--transition-fast);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.competitions-icon {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
}

.users-icon {
  background: linear-gradient(135deg, var(--warning-500) 0%, var(--warning-600) 100%);
}

.registrations-icon {
  background: linear-gradient(135deg, var(--secondary-500) 0%, var(--secondary-600) 100%);
}

.reviews-icon {
  background: linear-gradient(135deg, var(--success-500) 0%, var(--success-600) 100%);
}

.works-icon {
  background: linear-gradient(135deg, var(--error-500) 0%, var(--error-600) 100%);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  font-weight: 600;
}

.stat-value {
  font-size: 36px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 4px;
  letter-spacing: -1px;
}

.stat-desc {
  font-size: 13px;
  color: var(--text-tertiary);
  margin: 0;
  font-weight: 500;
}

.section-card {
  border-radius: var(--radius-xl);
  margin-bottom: 24px;
  border: 1px solid var(--border-light);
  box-shadow: var(--shadow-md);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.section-card:hover {
  box-shadow: var(--shadow-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-light);
}

.card-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.section-card :deep(.el-table th) {
  background-color: var(--bg-secondary) !important;
  font-weight: 700 !important;
  color: var(--text-primary) !important;
  border-bottom: 1px solid var(--border-light);
}

.section-card :deep(.el-table td) {
  border-bottom: 1px solid var(--border-light);
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  padding: 4px;
}

.quick-action-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 24px;
  border-radius: var(--radius-xl);
  background: var(--bg-secondary);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid var(--border-light);
}

.quick-action-item:hover {
  background: linear-gradient(135deg, var(--primary-500), var(--primary-700));
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: transparent;
}

.quick-action-item:hover .action-icon {
  background: rgba(255, 255, 255, 0.2) !important;
  color: var(--text-white);
  transform: scale(1.1);
}

.quick-action-item:hover .action-title,
.quick-action-item:hover .action-desc {
  color: var(--text-white);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-white);
  color: var(--text-primary);
  transition: all var(--transition-normal);
  flex-shrink: 0;
  font-size: 24px;
  box-shadow: var(--shadow-sm);
}

.competition-action {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
  color: var(--text-white);
}

.registration-action {
  background: linear-gradient(135deg, var(--secondary-500) 0%, var(--secondary-600) 100%);
  color: var(--text-white);
}

.review-action {
  background: linear-gradient(135deg, var(--success-500) 0%, var(--success-600) 100%);
  color: var(--text-white);
}

.result-action {
  background: linear-gradient(135deg, var(--warning-500) 0%, var(--warning-600) 100%);
  color: var(--text-white);
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.action-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  font-weight: 500;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions-grid {
    grid-template-columns: 1fr;
  }

  .welcome-title {
    font-size: 24px;
  }
  
  .welcome-section {
    padding: 24px 20px;
  }
}
</style>
