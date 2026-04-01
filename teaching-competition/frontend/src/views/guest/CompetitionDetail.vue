<template>
  <div class="competition-detail-page" v-loading="loading">
    <!-- 顶部封面区域 -->
    <div class="detail-hero" v-if="competition">
      <div class="hero-bg">
        <img v-if="competition.coverImage" :src="competition.coverImage" :alt="competition.name" class="hero-img" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge">
          <el-tag :type="getStatusType(competition.status)" size="large" effect="dark" round>
            {{ getStatusText(competition.status) }}
          </el-tag>
        </div>
        <h1 class="hero-title">{{ competition.name }}</h1>
        <div class="hero-meta">
          <span class="meta-item">
            <el-icon><Collection /></el-icon>
            {{ competition.type }}
          </span>
          <span class="meta-item" v-if="competition.theme">
            <el-icon><Flag /></el-icon>
            {{ competition.theme }}
          </span>
        </div>
      </div>
    </div>

    <div class="detail-body" v-if="competition">
      <!-- 基本信息 -->
      <div class="info-section">
        <h3 class="section-title">
          <el-icon><InfoFilled /></el-icon>
          竞赛信息
        </h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">竞赛类型</span>
            <span class="info-value">{{ competition.type }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">竞赛主题</span>
            <span class="info-value">{{ competition.theme || '未设置' }}</span>
          </div>
        </div>
      </div>

      <!-- 时间信息 -->
      <div class="time-section">
        <h3 class="section-title">
          <el-icon><Clock /></el-icon>
          时间安排
        </h3>
        <div class="timeline-grid">
          <div class="timeline-item">
            <div class="timeline-dot dot-start"></div>
            <div class="timeline-content">
              <span class="timeline-label">报名开始</span>
              <span class="timeline-date">{{ formatDate(competition.registrationStart) }}</span>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-dot dot-end"></div>
            <div class="timeline-content">
              <span class="timeline-label">报名截止</span>
              <span class="timeline-date">{{ formatDate(competition.registrationEnd) }}</span>
            </div>
          </div>
          <div class="timeline-item">
            <div class="timeline-dot dot-deadline"></div>
            <div class="timeline-content">
              <span class="timeline-label">作品截止</span>
              <span class="timeline-date">{{ formatDate(competition.submissionDeadline) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 竞赛描述 -->
      <div class="desc-section" v-if="competition.description">
        <h3 class="section-title">
          <el-icon><Document /></el-icon>
          竞赛描述
        </h3>
        <div class="desc-content">{{ competition.description }}</div>
      </div>

      <!-- 竞赛规则 -->
      <div class="rules-section" v-if="competition.rules">
        <h3 class="section-title">
          <el-icon><List /></el-icon>
          竞赛规则
        </h3>
        <div class="rules-content" v-html="competition.rules"></div>
      </div>

      <!-- 报名按钮 -->
      <div class="action-section" v-if="competition.status === 'REGISTRATION'">
        <template v-if="isRegistrationExpired">
          <el-button type="info" size="large" disabled class="cta-button">
            报名已截止
          </el-button>
          <p class="expired-tip">报名时间已过，无法报名</p>
        </template>
        <template v-else>
          <el-button type="primary" size="large" @click="showRegisterDialog = true" class="cta-button">
            <el-icon><Edit /></el-icon>
            立即报名
          </el-button>
        </template>
      </div>

      <!-- 获奖公示信息（公示中或已结束时显示） -->
      <div v-if="competition.status === 'ANNOUNCED' || competition.status === 'ENDED'" class="results-section">
        <h3 class="section-title">
          <el-icon><Trophy /></el-icon>
          获奖公示
        </h3>
        <div v-if="resultsLoading" v-loading="true" style="min-height: 100px;"></div>
        <template v-else-if="awardResults.length > 0">
          <div class="winner-list">
            <div v-for="item in awardResults" :key="item.workId" class="winner-card">
              <div class="winner-rank" :class="'rank-' + item.rank">{{ item.rank }}</div>
              <div class="winner-info">
                <div class="winner-name">{{ item.participantName || '-' }}</div>
                <div class="winner-work">{{ item.workTitle || '-' }}</div>
              </div>
              <div class="winner-award">
                <el-tag :type="getLevelType(item.awardLevel)" effect="dark" size="small">
                  {{ item.awardLevel }}
                </el-tag>
                <span class="winner-score">{{ item.avgScore != null ? item.avgScore + '分' : '-' }}</span>
              </div>
            </div>
          </div>
          <el-card v-if="allResults.length > 0" class="full-results-card">
            <template #header>
              <h3>完整成绩排名</h3>
            </template>
            <el-table :data="allResults" stripe size="small">
              <el-table-column prop="rank" label="排名" width="60" />
              <el-table-column prop="participantName" label="参赛者" width="100" />
              <el-table-column prop="workTitle" label="作品名称" min-width="160" />
              <el-table-column prop="avgScore" label="得分" width="80">
                <template #default="scope">
                  <span>{{ scope.row.avgScore != null ? scope.row.avgScore : '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="awardLevel" label="奖项" width="100">
                <template #default="scope">
                  <el-tag v-if="scope.row.awardLevel" :type="getLevelType(scope.row.awardLevel)" effect="plain" size="small">
                    {{ scope.row.awardLevel }}
                  </el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </template>
        <el-empty v-else description="暂无获奖信息" />
      </div>
    </div>

    <!-- 报名弹窗 -->
    <el-dialog
      v-model="showRegisterDialog"
      title="竞赛报名"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        class="register-form"
      >
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="registerForm.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="指导教师">
          <el-input v-model="registerForm.advisor" placeholder="请输入指导教师姓名" />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            type="textarea"
            v-model="registerForm.description"
            :rows="4"
            placeholder="请简要描述您的项目"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showRegisterDialog = false">取消</el-button>
          <el-button type="primary" @click="handleRegister" :loading="submitting">
            提交报名
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetition } from '@/api/competition'
import { createRegistration } from '@/api/registration'
import { getPublicResults } from '@/api/award'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const competition = ref(null)
const showRegisterDialog = ref(false)
const resultsLoading = ref(false)
const allResults = ref([])

// 判断报名是否已截止
const isRegistrationExpired = computed(() => {
  if (!competition.value) return false
  const end = competition.value.registrationEnd
  if (!end) return false
  return new Date(end) < new Date()
})

// 有奖项的结果（用于卡片展示）
const awardResults = computed(() => {
  return allResults.value.filter(item => item.awardLevel)
})

const registerForm = ref({
  projectName: '',
  advisor: '',
  description: ''
})

const registerRules = {
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  fetchCompetition()
})

const fetchCompetition = async () => {
  loading.value = true
  try {
    const res = await getCompetition(route.params.id)
    competition.value = res.data
    // 如果竞赛已公示或已结束，加载获奖信息
    if (competition.value && (competition.value.status === 'ANNOUNCED' || competition.value.status === 'ENDED')) {
      fetchResults()
    }
  } catch (error) {
    ElMessage.error('获取竞赛详情失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchResults = async () => {
  resultsLoading.value = true
  try {
    const res = await getPublicResults(route.params.id)
    const data = res.data || []
    allResults.value = data.map((item, index) => ({
      ...item,
      rank: index + 1
    }))
  } catch (error) {
    console.error('获取竞赛结果失败', error)
    allResults.value = []
  } finally {
    resultsLoading.value = false
  }
}

const handleRegister = async () => {
  submitting.value = true
  try {
    await createRegistration({
      competitionId: route.params.id,
      ...registerForm.value
    })
    ElMessage.success('报名成功！请等待管理员审核')
    showRegisterDialog.value = false
    // 可选：跳转到我的报名页面
    // router.push('/participant/registrations')
  } catch (error) {
    ElMessage.error('报名失败：' + (error.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

const getStatusType = (status) => {
    const typeMap = {
        'DRAFT': 'info',
        'PUBLISHED': 'primary',
        'REGISTRATION': 'success',
        'ONGOING': 'warning',
        'REVIEWED': 'info',
        'ANNOUNCEMENT': 'primary',
        'ENDED': 'info'
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
        'ANNOUNCEMENT': '公示中',
        'ANNOUNCED': '公示中',
        'ENDED': '已结束'
    }
    return textMap[status] || status
}


const formatDate = (date) => {
  if (!date) return '待定'
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getLevelType = (level) => {
  const typeMap = {
    '特等奖': 'danger',
    '一等奖': 'warning',
    '二等奖': 'primary',
    '三等奖': 'success',
    '优秀奖': 'info'
  }
  return typeMap[level] || 'info'
}
</script>

<style scoped>
.competition-detail-page {
  max-width: 100%;
  padding: 0;
}

/* Hero区域 */
.detail-hero {
  position: relative;
  border-radius: var(--radius-2xl);
  overflow: hidden;
  margin-bottom: 32px;
  min-height: 280px;
  box-shadow: var(--shadow-lg);
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.detail-hero:hover .hero-img {
  transform: scale(1.05);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.85), rgba(30, 41, 59, 0.7));
}

.hero-content {
  position: relative;
  z-index: 1;
  padding: 48px 36px;
}

.hero-badge {
  margin-bottom: 20px;
}

.hero-badge :deep(.el-tag) {
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 700;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  color: var(--text-white);
  margin: 0 0 20px;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.hero-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.hero-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.hero-meta .el-icon {
  color: var(--primary-400);
  font-size: 18px;
}

/* 主体内容 */
.detail-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section,
.time-section,
.desc-section,
.rules-section {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  padding: 28px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
  transition: all var(--transition-normal);
}

.info-section:hover,
.time-section:hover,
.desc-section:hover,
.rules-section:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-2px);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  padding-bottom: 12px;
  border-bottom: 2px solid var(--bg-secondary);
}

.section-title .el-icon {
  color: var(--primary-500);
  font-size: 22px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px 18px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  border-left: 4px solid var(--primary-500);
  transition: all var(--transition-normal);
}

.info-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.info-label {
  font-size: 12px;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.8px;
  font-weight: 700;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 时间线 */
.timeline-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px 0;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;
  border: 3px solid var(--bg-white);
  box-shadow: 0 0 0 4px var(--bg-secondary);
  z-index: 2;
  position: relative;
}

.dot-start { background: var(--success-500); }
.dot-end { background: var(--warning-500); }
.dot-deadline { background: var(--error-500); }

.timeline-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding: 16px 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  position: relative;
  transition: all var(--transition-normal);
}

.timeline-content:hover {
  background: var(--bg-tertiary);
  transform: translateX(8px);
}

.timeline-label {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
}

.timeline-date {
  font-size: 14px;
  color: var(--text-secondary);
  font-weight: 600;
}

.desc-content {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-secondary);
  white-space: pre-wrap;
  font-weight: 500;
}

.rules-content {
  background: var(--bg-secondary);
  padding: 24px;
  border-radius: var(--radius-lg);
  line-height: 1.8;
  font-size: 15px;
  border-left: 4px solid var(--secondary-500);
}

/* 报名按钮 */
.action-section {
  text-align: center;
  padding: 36px 32px;
  background: linear-gradient(135deg, var(--bg-white) 0%, var(--bg-secondary) 100%);
  border-radius: var(--radius-2xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-light);
}

.cta-button {
  padding: 16px 48px;
  font-size: 17px;
  font-weight: 700;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--primary-500), var(--primary-700)) !important;
  border: none !important;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
  transition: all var(--transition-normal);
  letter-spacing: 0.5px;
}

.cta-button:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.6) !important;
}

.cta-button:active {
  transform: translateY(0) scale(0.98);
}

.expired-tip {
  margin-top: 12px;
  font-size: 13px;
  color: var(--text-tertiary);
}

/* 获奖公示 */
.results-section {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  padding: 28px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
}

.winner-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.winner-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  transition: all var(--transition-normal);
}

.winner-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
}

.winner-rank {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 800;
  flex-shrink: 0;
  color: #fff;
}

.rank-1 { background: linear-gradient(135deg, #f59e0b, #d97706); }
.rank-2 { background: linear-gradient(135deg, #9ca3af, #6b7280); }
.rank-3 { background: linear-gradient(135deg, #b45309, #92400e); }

.winner-info {
  flex: 1;
  min-width: 0;
}

.winner-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.winner-work {
  font-size: 13px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.winner-award {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.winner-score {
  font-size: 13px;
  color: var(--text-tertiary);
  font-weight: 600;
}

.full-results-card {
  border-radius: var(--radius-lg);
}

.full-results-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.full-results-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .hero-content {
    padding: 32px 20px;
  }

  .hero-title {
    font-size: 28px;
  }

  .info-section,
  .time-section,
  .desc-section,
  .rules-section {
    padding: 20px;
  }

  .timeline-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>
