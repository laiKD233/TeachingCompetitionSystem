<template>
  <div class="results-page">
    <!-- 页面标题 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">成绩公示</h1>
          <p class="page-subtitle">发布竞赛成绩与奖项信息</p>
        </div>
      </div>
    </div>

    <!-- 筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="竞赛">
          <el-select
            v-model="selectedCompetition"
            placeholder="选择竞赛"
            clearable
            style="width: 280px"
            @change="handleCompetitionChange"
          >
            <el-option
              v-for="item in competitionOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 成绩列表 -->
    <el-card class="table-card" v-if="selectedCompetition">
      <template #header>
        <div class="card-header">
          <h3>竞赛成绩</h3>
          <div>
            <el-button type="primary" size="small" @click="showAwardDialog">
              <el-icon><Trophy /></el-icon>
              <span>发布奖项</span>
            </el-button>
            <el-button type="success" size="small" @click="handlePublishAnnouncement">
              <el-icon><Bell /></el-icon>
              <span>公示成绩</span>
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="results" v-loading="loading" stripe>
        <el-table-column type="index" label="排名" width="70" />

        <el-table-column prop="participantName" label="参赛者" width="120">
          <template #default="scope">
            <span>{{ scope.row.participantName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="workTitle" label="作品名称" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.workTitle || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="avgScore" label="得分" width="100">
          <template #default="scope">
            <span v-if="scope.row.avgScore !== null && scope.row.avgScore !== undefined" class="score-value">
              {{ Number(scope.row.avgScore).toFixed(1) }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="awardLevel" label="奖项" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.awardLevel" :type="getAwardType(scope.row.awardLevel)" effect="plain" size="small">
              {{ scope.row.awardLevel }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="reviewComment" label="评语" min-width="200">
          <template #default="scope">
            <span>{{ scope.row.reviewComment || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 已发布奖项列表 -->
    <el-card class="table-card" v-if="selectedCompetition && awards.length > 0" style="margin-top: 24px">
      <template #header>
        <div class="card-header">
          <h3>已发布奖项</h3>
        </div>
      </template>

      <el-table :data="enrichedAwards" stripe>
        <el-table-column prop="awardLevel" label="奖项等级" width="120">
          <template #default="scope">
            <el-tag :type="getAwardType(scope.row.awardLevel)" effect="dark" size="small">
              {{ scope.row.awardLevel || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="workTitle" label="获奖作品" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.workTitle || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="participantName" label="获奖者" width="120">
          <template #default="scope">
            <span>{{ scope.row.participantName || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="!selectedCompetition" description="请选择竞赛查看成绩" />
    <el-empty v-if="selectedCompetition && results.length === 0 && !loading" description="暂无成绩数据" />

    <!-- 发布奖项对话框 -->
    <el-dialog v-model="awardDialogVisible" title="发布奖项" width="500px" @close="resetAwardForm">
      <el-form :model="awardForm" :rules="awardRules" ref="awardFormRef" label-width="100px">
        <el-form-item label="奖项等级" prop="awardLevel">
          <el-select v-model="awardForm.awardLevel" placeholder="请选择奖项等级" style="width: 100%">
            <el-option label="特等奖" value="特等奖" />
            <el-option label="一等奖" value="一等奖" />
            <el-option label="二等奖" value="二等奖" />
            <el-option label="三等奖" value="三等奖" />
            <el-option label="优秀奖" value="优秀奖" />
          </el-select>
        </el-form-item>

        <el-form-item label="获奖作品" prop="workId">
          <el-select v-model="awardForm.workId" placeholder="请选择作品" style="width: 100%">
            <el-option
              v-for="item in results"
              :key="item.workId"
              :label="item.workTitle || `参赛者: ${item.participantName || '未知'}`"
              :value="item.workId"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="awardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublishAward">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCompetitions } from '@/api/competition'
import { publishAward, publishAnnouncement, getAwardList, getCompetitionResults } from '@/api/award'

const loading = ref(false)
const selectedCompetition = ref(null)
const competitionOptions = ref([])
const results = ref([])
const awards = ref([])
const awardDialogVisible = ref(false)
const awardFormRef = ref(null)

const awardForm = reactive({
  awardLevel: '',
  workId: ''
})

const awardRules = {
  awardLevel: [{ required: true, message: '请选择奖项等级', trigger: 'change' }],
  workId: [{ required: true, message: '请选择获奖作品', trigger: 'change' }]
}

const enrichedAwards = computed(() => {
  return awards.value.map(award => {
    const match = results.value.find(r => r.workId === award.workId)
    return {
      ...award,
      workTitle: match?.workTitle || '-',
      participantName: match?.participantName || '-'
    }
  })
})

onMounted(() => {
  fetchCompetitions()
})

const fetchCompetitions = async () => {
  try {
    const res = await getAdminCompetitions({ page: 1, size: 100 })
    competitionOptions.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleCompetitionChange = (val) => {
  if (val) {
    fetchResultsAndAwards(val)
  } else {
    results.value = []
    awards.value = []
  }
}

const fetchResultsAndAwards = async (competitionId) => {
  loading.value = true
  try {
    const [resultsRes, awardsRes] = await Promise.allSettled([
      getCompetitionResults(competitionId),
      getAwardList(competitionId)
    ])
    results.value = resultsRes.status === 'fulfilled' ? (resultsRes.value.data || []) : []
    awards.value = awardsRes.status === 'fulfilled' ? (awardsRes.value.data || []) : []
  } catch (error) {
    ElMessage.error('获取数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showAwardDialog = () => {
  resetAwardForm()
  awardDialogVisible.value = true
}

const handlePublishAward = async () => {
  if (!awardFormRef.value) return

  await awardFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await publishAward({
          competitionId: selectedCompetition.value,
          items: [{
            workId: awardForm.workId,
            awardLevel: awardForm.awardLevel
          }]
        })
        ElMessage.success('奖项发布成功')
        awardDialogVisible.value = false
        fetchResultsAndAwards(selectedCompetition.value)
      } catch (error) {
        ElMessage.error('发布失败')
        console.error(error)
      }
    }
  })
}

const handlePublishAnnouncement = async () => {
  try {
    await ElMessageBox.confirm('确定要公示该竞赛的成绩吗？公示后所有参赛者均可查看。', '确认公示', {
      confirmButtonText: '确定公示',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishAnnouncement(selectedCompetition.value)
    ElMessage.success('成绩已公示')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('公示失败')
      console.error(error)
    }
  }
}

const resetAwardForm = () => {
  awardFormRef.value?.resetFields()
  Object.assign(awardForm, {
    awardLevel: '',
    workId: ''
  })
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
.results-page {
  padding: 0;
}

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
  background: #fbbf24;
  bottom: -20px;
  left: 15%;
}

.page-header {
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 4px;
}

.page-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
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

.card-header .el-button {
  margin-left: 8px;
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
  font-size: 16px;
  font-weight: 700;
  color: var(--el-color-primary);
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

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
