<template>
  <div class="awards-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">往届成果</h1>
      <p class="page-desc">查看历届竞赛的获奖信息与优秀作品</p>
    </div>

    <!-- 筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="竞赛">
          <el-select
            v-model="selectedCompetition"
            placeholder="选择竞赛"
            clearable
            style="width: 240px"
            @change="handleCompetitionChange"
          >
            <el-option
              v-for="item in competitionList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 获奖信息 -->
    <div v-loading="loading" class="awards-content">
      <!-- 有数据时 -->
      <div v-if="awards.length > 0" class="awards-list">
        <el-card v-for="award in awards" :key="award.id" class="award-card">
          <div class="award-header">
            <el-tag :type="getLevelType(award.level)" effect="dark" class="award-level-tag">
              {{ getLevelText(award.level) }}
            </el-tag>
            <div class="award-info">
              <h3 class="award-title">{{ award.title || '获奖作品' }}</h3>
              <p class="award-winner">
                <el-icon><User /></el-icon>
                <span>{{ award.winnerName || award.participantName || '-' }}</span>
              </p>
            </div>
          </div>
          <div class="award-details">
            <div v-if="award.prize" class="detail-item">
              <span class="detail-label">奖项：</span>
              <span>{{ award.prize }}</span>
            </div>
            <div v-if="award.score" class="detail-item">
              <span class="detail-label">得分：</span>
              <span class="detail-score">{{ award.score }}</span>
            </div>
            <div v-if="award.competitionName" class="detail-item">
              <span class="detail-label">竞赛：</span>
              <span>{{ award.competitionName }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 成绩列表 -->
      <el-card v-if="results.length > 0" class="results-card">
        <template #header>
          <h3>竞赛成绩</h3>
        </template>
        <el-table :data="results" stripe>
          <el-table-column prop="participantName" label="参赛者" width="120" />
          <el-table-column prop="workTitle" label="作品名称" min-width="180">
            <template #default="scope">
              <span>{{ scope.row.workTitle || scope.row.title || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="score" label="得分" width="100">
            <template #default="scope">
              <span class="detail-score">{{ scope.row.score || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="rank" label="排名" width="80" />
          <el-table-column prop="award" label="奖项" width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.award" :type="getLevelType(scope.row.award)" effect="plain" size="small">
                {{ scope.row.award }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 无数据时 -->
      <el-empty v-if="awards.length === 0 && results.length === 0 && !loading" description="暂无往届成果信息" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPublicCompetitions } from '@/api/competition'
import { getFinishedAwards, getCompetitionResults } from '@/api/award'

const loading = ref(false)
const selectedCompetition = ref(null)
const competitionList = ref([])
const awards = ref([])
const results = ref([])

onMounted(() => {
  fetchCompetitions()
})

const fetchCompetitions = async () => {
  try {
    const res = await getPublicCompetitions({
      status: 'FINISHED',
      page: 1,
      size: 100
    })
    competitionList.value = res.data.records
    // 默认选中第一个已结束的竞赛
    if (competitionList.value.length > 0) {
      selectedCompetition.value = competitionList.value[0].id
      fetchAwardsAndResults(selectedCompetition.value)
    }
  } catch (error) {
    ElMessage.error('获取竞赛列表失败')
    console.error(error)
  }
}

const handleCompetitionChange = (val) => {
  if (val) {
    fetchAwardsAndResults(val)
  } else {
    awards.value = []
    results.value = []
  }
}

const fetchAwardsAndResults = async (competitionId) => {
  loading.value = true
  try {
    const [awardsRes, resultsRes] = await Promise.allSettled([
      getFinishedAwards(competitionId),
      getCompetitionResults(competitionId)
    ])
    awards.value = awardsRes.status === 'fulfilled' ? (awardsRes.value.data || []) : []
    results.value = resultsRes.status === 'fulfilled' ? (resultsRes.value.data || []) : []
  } catch (error) {
    ElMessage.error('获取成果信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
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

const getLevelText = (level) => {
  return level || '获奖'
}
</script>

<style scoped>
.awards-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
}

.awards-content {
  min-height: 200px;
}

.awards-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.award-card {
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
}

.award-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.award-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.award-level-tag {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 4px;
  flex-shrink: 0;
}

.award-info {
  flex: 1;
}

.award-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.award-winner {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.award-details {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  padding-top: 12px;
  border-top: 1px solid var(--border-light);
}

.detail-item {
  font-size: 14px;
  color: var(--text-secondary);
}

.detail-label {
  color: var(--text-placeholder);
  margin-right: 4px;
}

.detail-score {
  font-weight: 600;
  color: var(--el-color-primary);
  font-size: 16px;
}

.results-card {
  border-radius: var(--radius-lg);
}

.results-card h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.results-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .award-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .award-details {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
