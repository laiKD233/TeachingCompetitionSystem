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
  margin-bottom: 30px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.welcome-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: var(--radius-lg);
  background: #fff;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #fff;
}

.competitions-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.users-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.registrations-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.reviews-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.works-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-desc {
  font-size: 13px;
  color: var(--text-placeholder);
  margin: 0;
}

.section-card {
  border-radius: var(--radius-lg);
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.quick-action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: var(--radius-md);
  background: var(--bg-light);
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-action-item:hover {
  background: var(--primary-gradient);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.quick-action-item:hover .action-icon {
  color: #fff;
}

.quick-action-item:hover .action-title,
.quick-action-item:hover .action-desc {
  color: #fff;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.competition-action {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.registration-action {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.review-action {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.result-action {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
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
}
</style>
