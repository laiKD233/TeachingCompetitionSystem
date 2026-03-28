<template>
  <div class="dashboard-page">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ userStore.user?.name }}</h1>
      <p class="welcome-desc">{{ currentDate }}</p>
    </div>
    
    <!-- 我的统计 -->
    <div class="stats-grid">
      <div class="stat-card registrations-card">
        <div class="stat-icon registrations-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">我的报名</p>
          <p class="stat-value">{{ stats.myRegistrations }}</p>
          <p class="stat-desc">参与竞赛</p>
        </div>
      </div>
      
      <div class="stat-card works-card">
        <div class="stat-icon works-icon">
          <el-icon :size="32"><Upload /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">我的作品</p>
          <p class="stat-value">{{ stats.myWorks }}</p>
          <p class="stat-desc">提交作品</p>
        </div>
      </div>
      
      <div class="stat-card pending-card">
        <div class="stat-icon pending-icon">
          <el-icon :size="32"><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">待审核</p>
          <p class="stat-value">{{ stats.pendingReviews }}</p>
          <p class="stat-desc">审核中</p>
        </div>
      </div>
      
      <div class="stat-card awards-card">
        <div class="stat-icon awards-icon">
          <el-icon :size="32"><Trophy /></el-icon>
        </div>
        <div class="stat-content">
          <p class="stat-label">获奖次数</p>
          <p class="stat-value">{{ stats.awards }}</p>
          <p class="stat-desc">获得荣誉</p>
        </div>
      </div>
    </div>
    
    <!-- 正在进行中的竞赛 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>热门竞赛</h3>
          <el-button type="primary" text @click="router.push('/guest/competitions')">
            查看全部
          </el-button>
        </div>
      </template>
      
      <div class="ongoing-list" v-loading="loading">
        <div
          v-for="competition in ongoingCompetitions"
          :key="competition.id"
          class="ongoing-item"
          @click="viewCompetition(competition.id)"
        >
          <div class="competition-image">
            <img :src="competition.coverImage || defaultImage" :alt="competition.name" @error="e => e.target.src = defaultImage" />
            <el-tag :type="getStatusType(competition.status)" class="image-tag">
              {{ getStatusText(competition.status) }}
            </el-tag>
          </div>
          
          <div class="competition-info">
            <h4 class="competition-name">{{ competition.name }}</h4>
            <p class="competition-type">{{ competition.type }}</p>
            
            <div class="competition-meta">
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>截止：{{ formatDate(competition.registrationEnd) }}</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ competition.participantCount || 0 }} 人参与</span>
              </div>
            </div>
            
            <div class="competition-actions">
              <el-button
                v-if="!competition.hasRegistered"
                type="primary"
                size="small"
                @click.stop="handleRegister(competition)"
              >
                立即报名
              </el-button>
              <el-tag v-else type="success" size="small" effect="plain">
                已报名
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-if="ongoingCompetitions.length === 0" description="暂无进行中的竞赛" />
    </el-card>
    
    <!-- 最近活动 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>最近动态</h3>
          <el-button type="primary" text @click="router.push('/participant/registrations')">
            查看全部
          </el-button>
        </div>
      </template>
      
      <el-timeline class="activity-timeline" v-if="recentActivities.length">
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="formatDate(activity.time)"
          placement="top"
        >
          <div class="activity-item">
            <div class="activity-header">
              <span class="activity-title">{{ activity.title }}</span>
              <el-tag :type="activity.type" size="small" effect="plain">
                {{ activity.category }}
              </el-tag>
            </div>
            <p class="activity-desc" v-if="activity.description">{{ activity.description }}</p>
          </div>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无动态" :image-size="60" />
    </el-card>
    
    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <h3>快捷操作</h3>
        </div>
      </template>
      
      <div class="quick-actions-grid">
        <div class="quick-action-item" @click="router.push('/participant/registrations')">
          <div class="action-icon registration-action">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">我的报名</p>
            <p class="action-desc">查看报名记录</p>
          </div>
        </div>
        
        <div class="quick-action-item" @click="router.push('/participant/works')">
          <div class="action-icon work-action">
            <el-icon :size="24"><Upload /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">作品管理</p>
            <p class="action-desc">提交和管理作品</p>
          </div>
        </div>
        
        <div class="quick-action-item" @click="router.push('/participant/scores')">
          <div class="action-icon score-action">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">成绩查询</p>
            <p class="action-desc">查看竞赛成绩</p>
          </div>
        </div>
        
        <div class="quick-action-item" @click="router.push('/guest/competitions')">
          <div class="action-icon explore-action">
            <el-icon :size="24"><Compass /></el-icon>
          </div>
          <div class="action-text">
            <p class="action-title">探索竞赛</p>
            <p class="action-desc">发现更多机会</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getPublicCompetitions } from '@/api/competition'
import { getRegistrations, createRegistration } from '@/api/registration'
import { getMyWorks } from '@/api/work'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const statsLoading = ref(false)
const defaultImage = `data:image/svg+xml,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="400" height="200" fill="none"><rect width="400" height="200" rx="12" fill="#e8eaf6"/><text x="200" y="95" text-anchor="middle" font-family="sans-serif" font-size="40" fill="#9fa8da">&#127942;</text><text x="200" y="130" text-anchor="middle" font-family="sans-serif" font-size="14" fill="#7986cb">&#26242;&#26080;&#23553;&#38754;</text></svg>')}`

const stats = ref({
  myRegistrations: 0,
  myWorks: 0,
  pendingReviews: 0,
  awards: 0
})

const ongoingCompetitions = ref([])
const recentActivities = ref([])

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日')
})

onMounted(() => {
  fetchStats()
  fetchOngoingCompetitions()
})

const fetchStats = async () => {
  statsLoading.value = true
  try {
    const [regRes, workRes] = await Promise.allSettled([
      getRegistrations({ page: 1, size: 100 }),
      getMyWorks({ page: 1, size: 100 })
    ])

    const registrations = regRes.status === 'fulfilled' ? regRes.value.data.records || [] : []
    const works = workRes.status === 'fulfilled' ? workRes.value.data.records || [] : []

    // 从报名列表中提取最近活动
    recentActivities.value = registrations.slice(0, 5).map(reg => {
      const statusMap = {
        'PENDING': { text: '待审核', type: 'warning' },
        'APPROVED': { text: '已通过', type: 'success' },
        'REJECTED': { text: '已驳回', type: 'danger' }
      }
      const s = statusMap[reg.status] || { text: reg.status, type: 'info' }
      return {
        id: reg.id,
        title: `报名${s.text}`,
        description: `${reg.competitionName || reg.projectName || '竞赛'} - ${reg.status === 'REJECTED' && reg.rejectReason ? reg.rejectReason : ''}`,
        time: reg.createdAt || '',
        type: s.type,
        category: '报名'
      }
    })

    stats.value = {
      myRegistrations: registrations.length,
      myWorks: works.length,
      pendingReviews: registrations.filter(r => r.status === 'PENDING').length,
      awards: 0
    }
  } catch (error) {
    console.error(error)
  } finally {
    statsLoading.value = false
  }
}

const fetchOngoingCompetitions = async () => {
  loading.value = true
  try {
    const res = await getPublicCompetitions({
      page: 1,
      size: 10
    })
    ongoingCompetitions.value = res.data.records.map(item => ({
      ...item,
      hasRegistered: false
    }))
  } catch (error) {
    ElMessage.error('获取竞赛列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'REGISTRATION': 'success',
    'ONGOING': 'warning',
    'REVIEWED': 'info',
    'ANNOUNCED': 'primary',
    'ANNOUNCEMENT': 'primary',
    'ENDED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'REGISTRATION': '报名中',
    'ONGOING': '进行中',
    'REVIEWED': '已评审',
    'ANNOUNCED': '公示中',
    'ANNOUNCEMENT': '公示中',
    'ENDED': '已结束'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

const viewCompetition = (id) => {
  router.push(`/guest/competition/${id}`)
}

const handleRegister = async (competition) => {
  try {
    await createRegistration({
      competitionId: competition.id
    })
    competition.hasRegistered = true
    ElMessage.success('报名成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '报名失败')
    console.error(error)
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

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
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

.registrations-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.works-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.pending-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.awards-icon {
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

/* 区域卡片 */
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

/* 进行中的竞赛列表 */
.ongoing-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ongoing-item {
  display: flex;
  gap: 20px;
  padding: 16px;
  border-radius: var(--radius-md);
  background: var(--bg-light);
  cursor: pointer;
  transition: all 0.3s ease;
}

.ongoing-item:hover {
  background: #f0f2fe;
  transform: translateX(8px);
}

.competition-image {
  position: relative;
  width: 200px;
  height: 140px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.competition-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.ongoing-item:hover .competition-image img {
  transform: scale(1.05);
}

.image-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  font-size: 12px;
}

.competition-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.competition-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.competition-type {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.competition-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.competition-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 快捷操作 */
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

.registration-action {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.work-action {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.score-action {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.explore-action {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .ongoing-item {
    flex-direction: column;
  }
  
  .competition-image {
    width: 100%;
    height: 180px;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-title {
    font-size: 24px;
  }
}
</style>
