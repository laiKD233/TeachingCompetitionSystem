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
          <p class="stat-label">总用户数</p>
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
          <p class="stat-label">报名总数</p>
          <p class="stat-value">{{ stats.totalRegistrations }}</p>
          <p class="stat-trend" v-if="stats.pendingRegistrations > 0">
            <span class="trend-warn">{{ stats.pendingRegistrations }}</span>
            <span class="trend-desc">待审核</span>
          </p>
          <p class="stat-trend" v-else>
            <span class="trend-ok">全部已处理</span>
          </p>
        </div>
      </div>
      
      <div class="stat-card works-card">
        <div class="stat-icon works-icon">
          <el-icon :size="32"><Upload /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">作品总数</p>
          <p class="stat-value">{{ stats.totalWorks }}</p>
          <p class="stat-trend" v-if="stats.pendingReviews > 0">
            <span class="trend-warn">{{ stats.pendingReviews }}</span>
            <span class="trend-desc">待评审</span>
          </p>
          <p class="stat-trend" v-else>
            <span class="trend-ok">全部已评审</span>
          </p>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 用户增长趋势 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <h3>用户增长趋势</h3>
            <el-radio-group v-model="userTrendPeriod" size="small">
              <el-radio-button label="week">周</el-radio-button>
              <el-radio-button label="month">月</el-radio-button>
              <el-radio-button label="year">年</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-placeholder">
            <el-icon :size="64" color="#ddd"><DataLine /></el-icon>
            <p>图表数据加载中...</p>
          </div>
        </div>
      </el-card>
      
      <!-- 竞赛参与情况 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <h3>竞赛参与情况</h3>
            <el-select v-model="competitionType" size="small" style="width: 120px">
              <el-option label="全部类型" value="all" />
              <el-option label="程序设计" value="programming" />
              <el-option label="数学建模" value="math" />
              <el-option label="创新创业" value="innovation" />
            </el-select>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-placeholder">
            <el-icon :size="64" color="#ddd"><PieChart /></el-icon>
            <p>图表数据加载中...</p>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 最近活动 -->
    <div class="recent-section">
      <el-card class="recent-card">
        <template #header>
          <div class="card-header">
            <h3>最近活动</h3>
            <el-button type="primary" size="small" text>查看全部</el-button>
          </div>
        </template>
        
        <el-timeline class="activity-timeline">
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            placement="top"
          >
            <div class="activity-item">
              <div class="activity-header">
                <span class="activity-title">{{ activity.title }}</span>
                <el-tag :type="activity.type" size="small" effect="plain">
                  {{ activity.category }}
                </el-tag>
              </div>
              <p class="activity-desc">{{ activity.description }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </el-card>
      
      <!-- 快捷操作 -->
      <el-card class="quick-actions-card">
        <template #header>
          <div class="card-header">
            <h3>快捷操作</h3>
          </div>
        </template>
        
        <div class="quick-actions-grid">
          <div class="quick-action-item" @click="router.push('/super-admin/users')">
            <div class="action-icon user-action">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <span>用户管理</span>
          </div>
          
          <div class="quick-action-item" @click="router.push('/super-admin/logs')">
            <div class="action-icon log-action">
              <el-icon :size="24"><Document /></el-icon>
            </div>
            <span>查看日志</span>
          </div>
          
          <div class="quick-action-item" @click="router.push('/admin/competitions')">
            <div class="action-icon add-action">
              <el-icon :size="24"><Plus /></el-icon>
            </div>
            <span>创建竞赛</span>
          </div>
          
          <div class="quick-action-item" @click="router.push('/super-admin/users')">
            <div class="action-icon settings-action">
              <el-icon :size="24"><Setting /></el-icon>
            </div>
            <span>用户管理</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'
import { getDashboardStats } from '@/api/super-admin'
import { getOperationLogs } from '@/api/super-admin'

const router = useRouter()
const userStore = useUserStore()

const userTrendPeriod = ref('week')
const competitionType = ref('all')

const stats = ref({
  totalUsers: 0,
  totalCompetitions: 0,
  totalRegistrations: 0,
  totalWorks: 0,
  pendingRegistrations: 0,
  pendingReviews: 0
})

const recentActivities = ref([])

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日')
})

onMounted(() => {
  fetchDashboardData()
})

const fetchDashboardData = async () => {
  try {
    const [statsRes, logsRes] = await Promise.allSettled([
      getDashboardStats(),
      getOperationLogs({ page: 1, size: 5 })
    ])

    if (statsRes.status === 'fulfilled' && statsRes.value.data) {
      const data = statsRes.value.data
      stats.value.totalUsers = data.totalUsers ?? 0
      stats.value.totalCompetitions = data.totalCompetitions ?? 0
      stats.value.totalRegistrations = data.totalRegistrations ?? 0
      stats.value.totalWorks = data.totalWorks ?? 0
      stats.value.pendingRegistrations = data.pendingRegistrations ?? 0
      stats.value.pendingReviews = data.pendingReviews ?? 0
    }

    if (logsRes.status === 'fulfilled' && logsRes.value.data) {
      const logs = logsRes.value.data.records || []
      recentActivities.value = logs.map(log => ({
        id: log.id,
        title: log.operation || '系统操作',
        description: log.module || '',
        time: log.createdAt ? dayjs(log.createdAt).format('YYYY-MM-DD HH:mm') : '-',
        type: 'primary',
        category: log.module || '系统'
      }))
    }
  } catch (error) {
    console.error('获取仪表盘数据失败', error)
  }
}
</script>

<style scoped>
.dashboard-page {
  padding: 0;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 30px;
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

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
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
  color: #fff;
  box-shadow: var(--shadow-md);
  transition: transform var(--transition-fast);
}

.stat-card:hover .stat-icon {
  transform: scale(1.1) rotate(5deg);
}

.users-icon {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
}

.competitions-icon {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
}

.registrations-icon {
  background: linear-gradient(135deg, #60A5FA 0%, #2563EB 100%);
}

.works-icon {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
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
  margin-bottom: 8px;
}

.stat-desc {
  font-size: 13px;
  color: var(--text-placeholder);
  margin: 0;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.stat-trend.positive {
  color: var(--success-color);
}

.stat-trend.negative {
  color: var(--danger-color);
}

.trend-desc {
  color: var(--text-placeholder);
  margin-left: 4px;
}

.trend-warn {
  color: var(--el-color-warning);
  font-weight: 600;
  margin-right: 4px;
}

.trend-ok {
  color: var(--el-color-success);
  font-size: 13px;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 30px;
}

.chart-card {
  border-radius: var(--radius-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: var(--text-placeholder);
}

.chart-placeholder p {
  margin-top: 12px;
  font-size: 14px;
}

/* 最近活动区域 */
.recent-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.recent-card,
.quick-actions-card {
  border-radius: var(--radius-lg);
}

.activity-timeline {
  padding-left: 10px;
}

.activity-item {
  padding-bottom: 20px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.activity-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.activity-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

/* 快捷操作 */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
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

.quick-action-item:hover span {
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

.user-action {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--primary-700) 100%);
}

.log-action {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
}

.add-action {
  background: linear-gradient(135deg, #60A5FA 0%, #2563EB 100%);
}

.settings-action {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
}

.quick-action-item span {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .recent-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .quick-actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .welcome-title {
    font-size: 24px;
  }
}
</style>
