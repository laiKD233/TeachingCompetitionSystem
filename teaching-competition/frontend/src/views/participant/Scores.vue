<template>
  <div class="scores-page">
    <!-- 页面头部 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">成绩查询</h1>
          <p class="page-subtitle">查看您在各竞赛中的成绩与排名</p>
        </div>
      </div>
    </div>

    <!-- 竞赛选择 -->
    <div class="filter-card">
      <div class="filter-inner">
        <el-icon class="filter-icon"><Trophy /></el-icon>
        <span class="filter-label">选择竞赛</span>
        <el-select
          v-model="selectedCompetition"
          placeholder="请选择竞赛"
          clearable
          style="width: 320px"
          @change="handleCompetitionChange"
        >
          <el-option
            v-for="item in competitionList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>
    </div>

    <!-- 成绩列表 -->
    <el-card class="table-card" v-if="selectedCompetition">
      <div class="scores-grid" v-loading="loading" v-if="scores.length > 0">
        <div
          v-for="(item, index) in scores"
          :key="index"
          class="score-card"
          :class="{ 'score-card-top': index === 0, 'score-card-2': index === 1, 'score-card-3': index === 2 }"
        >
          <div class="score-rank">
            <span class="rank-number" v-if="item.rank">{{ item.rank }}</span>
            <span class="rank-number" v-else>-</span>
          </div>
          <div class="score-info">
            <h4 class="score-work-title">{{ item.workTitle || item.title || '未命名作品' }}</h4>
            <p class="score-competition">{{ item.competitionName || '-' }}</p>
          </div>
          <div class="score-data">
            <div class="score-points" v-if="item.avgScore !== null && item.avgScore !== undefined">
              <span class="points-value">{{ Number(item.avgScore).toFixed(1) }}</span>
              <span class="points-label">分</span>
            </div>
            <div class="score-points" v-else>
              <span class="points-na">未评分</span>
            </div>
            <el-tag v-if="item.awardLevel" :type="getAwardType(item.awardLevel)" effect="plain" size="small" class="award-tag">
              {{ item.awardLevel }}
            </el-tag>
          </div>
          <div class="score-comment" v-if="item.reviewComment">
            <el-icon style="margin-right: 4px; flex-shrink: 0"><ChatLineSquare /></el-icon>
            <span>{{ item.reviewComment }}</span>
          </div>
        </div>
      </div>

      <el-empty v-if="scores.length === 0 && !loading" description="暂无成绩信息">
        <template #image>
          <div class="empty-visual">
            <el-icon :size="48" color="#dcdfe6"><TrendCharts /></el-icon>
          </div>
        </template>
      </el-empty>
    </el-card>

    <!-- 未选择竞赛时 -->
    <div v-if="!selectedCompetition" class="empty-guide">
      <div class="guide-icon">
        <el-icon :size="48" color="#c0c4cc"><Compass /></el-icon>
      </div>
      <h3>请先选择竞赛</h3>
      <p>选择一个已公示成绩的竞赛，查看您的成绩与排名</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublicCompetitions } from '@/api/competition'
import { getCompetitionResults } from '@/api/award'

const loading = ref(false)
const selectedCompetition = ref(null)
const competitionList = ref([])
const scores = ref([])

onMounted(() => {
  fetchCompetitions()
})

const fetchCompetitions = async () => {
  try {
    const res = await getPublicCompetitions({
      status: 'ANNOUNCED',
      page: 1,
      size: 100
    })
    competitionList.value = res.data.records
  } catch (error) {
    ElMessage.error('获取竞赛列表失败')
    console.error(error)
  }
}

const handleCompetitionChange = async (val) => {
  if (!val) {
    scores.value = []
    return
  }

  loading.value = true
  try {
    const res = await getCompetitionResults(val)
    scores.value = res.data || []
  } catch (error) {
    ElMessage.error('获取成绩失败')
    console.error(error)
    scores.value = []
  } finally {
    loading.value = false
  }
}

const getAwardType = (award) => {
  const typeMap = {
    '特等奖': 'danger',
    '一等奖': 'warning',
    '二等奖': 'primary',
    '三等奖': 'success',
    '优秀奖': 'info'
  }
  return typeMap[award] || 'info'
}
</script>

<style scoped>
.scores-page {
  padding: 0;
  max-width: 960px;
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
  background: #43e97b;
  top: -50px;
  right: -30px;
}

.deco-circle-2 {
  width: 100px;
  height: 100px;
  background: #00f2fe;
  bottom: -20px;
  left: 20%;
}

.page-header {
  position: relative;
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

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
  background: var(--bg-white);
  box-shadow: var(--shadow-sm);
}

.filter-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
}

.filter-icon {
  font-size: 20px;
  color: var(--warning-color);
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 成绩网格 */
.scores-grid {
  display: grid;
  gap: 16px;
}

.score-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  border-radius: var(--radius-lg);
  background: var(--bg-light);
  border-left: 4px solid var(--border-color);
  transition: all 0.3s ease;
}

.score-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
  background: var(--bg-white);
}

.score-card-top {
  border-left-color: #f59e0b;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.04), rgba(245, 158, 11, 0.01));
}

.score-card-2 {
  border-left-color: #9ca3af;
}

.score-card-3 {
  border-left-color: #cd7f32;
}

.score-rank {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-white);
  box-shadow: var(--shadow-sm);
}

.score-card-top .score-rank {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  color: #fff;
}

.rank-number {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.score-card-top .rank-number {
  color: #fff;
}

.score-info {
  flex: 1;
  min-width: 0;
}

.score-work-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.score-competition {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.score-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.score-points {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.points-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
}

.points-label {
  font-size: 13px;
  color: var(--text-placeholder);
}

.points-na {
  font-size: 14px;
  color: var(--text-placeholder);
}

.award-tag {
  font-weight: 600;
}

.score-comment {
  display: flex;
  align-items: flex-start;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  padding: 8px 12px;
  background: rgba(37, 99, 235, 0.04);
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  max-width: 200px;
  line-height: 1.5;
}

/* 空状态 */
.empty-guide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
  text-align: center;
}

.guide-icon {
  margin-bottom: 20px;
  animation: emptyFloat 3s ease-in-out infinite;
}

@keyframes emptyFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.empty-guide h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
}

.empty-guide p {
  font-size: 14px;
  color: var(--text-placeholder);
  margin: 0;
}

.empty-visual {
  padding: 16px;
}

.table-card {
  border-radius: var(--radius-lg);
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header-wrapper {
    padding: 24px 20px 20px;
  }

  .score-card {
    flex-wrap: wrap;
    gap: 12px;
  }

  .score-comment {
    width: 100%;
    max-width: none;
  }
}
</style>
