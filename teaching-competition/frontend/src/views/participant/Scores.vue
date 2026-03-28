<template>
  <div class="scores-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">成绩查询</h1>
    </div>

    <!-- 竞赛选择 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="选择竞赛">
          <el-select
            v-model="selectedCompetition"
            placeholder="请选择竞赛"
            clearable
            style="width: 280px"
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

    <!-- 成绩列表 -->
    <el-card class="table-card">
      <el-table :data="scores" v-loading="loading" stripe>
        <el-table-column prop="competitionName" label="竞赛名称" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.competitionName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="workTitle" label="作品名称" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.workTitle || scope.row.title || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="得分" width="120">
          <template #default="scope">
            <span v-if="scope.row.score" class="score-value">{{ scope.row.score }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="rank" label="排名" width="100">
          <template #default="scope">
            <span v-if="scope.row.rank">{{ scope.row.rank }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="award" label="奖项" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.award" :type="getAwardType(scope.row.award)" effect="plain" size="small">
              {{ scope.row.award }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="comment" label="评语" min-width="200">
          <template #default="scope">
            <span>{{ scope.row.comment || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="scores.length === 0 && !loading" description="暂无成绩信息，请选择竞赛查看" />
    </el-card>
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
}

.page-header {
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

.score-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--el-color-primary);
}

@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }
}
</style>
