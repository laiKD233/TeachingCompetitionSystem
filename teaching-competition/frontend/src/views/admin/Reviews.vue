<template>
  <div class="reviews-page">
    <!-- 页面标题 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">评审分配</h1>
          <p class="page-subtitle">管理竞赛评审任务与评分</p>
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
            style="width: 240px"
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

    <!-- 作品评分列表 -->
    <el-card class="table-card" v-if="selectedCompetition">
      <template #header>
        <div class="card-header">
          <h3>作品评审情况</h3>
          <el-button type="primary" size="small" @click="showAssignDialog">
            <el-icon><Plus /></el-icon>
            <span>分配评审</span>
          </el-button>
        </div>
      </template>

      <el-table :data="scores" v-loading="loading" stripe>
        <el-table-column prop="participantName" label="参赛者" width="120">
          <template #default="scope">
            <span>{{ scope.row.participantName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="workTitle" label="作品名称" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.workTitle || scope.row.title || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="avgScore" label="平均得分" width="120">
          <template #default="scope">
            <span
              v-if="scope.row.avgScore !== null && scope.row.avgScore !== undefined"
              class="score-value"
            >
              {{ Number(scope.row.avgScore).toFixed(1) }}
            </span>
            <el-tag v-else type="warning" size="small" effect="plain">待评审</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="info" size="small" plain @click="showReviewDetail(scope.row)">
              <el-icon><View /></el-icon>
              <span>详情</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="!selectedCompetition" description="请选择竞赛查看评审情况" />
    <el-empty v-if="selectedCompetition && scores.length === 0 && !loading" description="暂无评审数据" />

    <!-- 评审详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="评审详情" width="600px">
      <el-descriptions :column="1" border v-if="currentDetail">
        <el-descriptions-item label="作品名称">{{ currentDetail.workTitle }}</el-descriptions-item>
        <el-descriptions-item label="参赛者">{{ currentDetail.participantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="作品文件">
          <el-button
            v-if="currentDetail.workFileUrl"
            type="primary"
            size="small"
            text
            @click="downloadWorkFile"
          >
            <el-icon><Download /></el-icon>
            <span>{{ currentDetail.workFileName || '下载文件' }}</span>
          </el-button>
          <span v-else class="text-muted">无文件</span>
        </el-descriptions-item>
        <el-descriptions-item label="平均得分">
          <span v-if="currentDetail.avgScore !== null && currentDetail.avgScore !== undefined" class="score-value">
            {{ Number(currentDetail.avgScore).toFixed(1) }}
          </span>
          <el-tag v-else type="warning" size="small">待评审</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评审任务数">{{ currentDetail.reviewCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="已完成评审">{{ currentDetail.completedCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="评审人列表">
          <div v-if="currentDetail.reviewers && currentDetail.reviewers.length > 0">
            <el-tag
              v-for="r in currentDetail.reviewers"
              :key="r.reviewerId"
              :type="r.status === 'COMPLETED' ? 'success' : 'warning'"
              size="small"
              style="margin: 2px 4px 2px 0"
            >
              {{ r.reviewerName }} - {{ r.status === 'COMPLETED' ? (r.score + '分') : '待评审' }}
            </el-tag>
          </div>
          <span v-else>暂无评审人</span>
        </el-descriptions-item>
        <el-descriptions-item label="评审评语" v-if="currentDetail.comments">
          <div v-for="(c, i) in currentDetail.comments" :key="i" style="margin-bottom: 4px">
            <span style="color: var(--text-secondary)">{{ c.reviewerName }}：</span>{{ c.comment }}
          </div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 分配评审对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配评审任务" width="500px" @close="resetAssignForm">
      <el-form :model="assignForm" :rules="assignRules" ref="assignFormRef" label-width="100px">
        <el-form-item label="参赛作品" prop="workId">
          <el-select v-model="assignForm.workId" placeholder="请选择作品" style="width: 100%">
            <el-option
              v-for="item in scores"
              :key="item.workId"
              :label="item.workTitle"
              :value="item.workId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="评审人" prop="reviewerId">
          <el-select v-model="assignForm.reviewerId" placeholder="请选择评审人" style="width: 100%" filterable>
            <el-option
              v-for="reviewer in reviewerOptions"
              :key="reviewer.id"
              :label="`${reviewer.name} (${reviewer.studentId || reviewer.id})`"
              :value="reviewer.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确定</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, View, Download } from '@element-plus/icons-vue'
import { getAdminCompetitions } from '@/api/competition'
import { assignReview, getReviewScores, getWorkReviewTasks, getEligibleReviewers } from '@/api/review'

const loading = ref(false)
const selectedCompetition = ref(null)
const competitionOptions = ref([])
const scores = ref([])
const reviewerOptions = ref([])
const assignDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignFormRef = ref(null)
const currentDetail = ref(null)

const assignForm = reactive({
  workId: '',
  reviewerId: ''
})

const assignRules = {
  workId: [{ required: true, message: '请选择作品', trigger: 'change' }],
  reviewerId: [{ required: true, message: '请选择评审人', trigger: 'change' }]
}

onMounted(() => {
  fetchCompetitions()
  fetchReviewerOptions()
})

const fetchCompetitions = async () => {
  try {
    const res = await getAdminCompetitions({ page: 1, size: 100 })
    competitionOptions.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const fetchReviewerOptions = async () => {
  try {
    const res = await getEligibleReviewers()
    if (res.data) {
      reviewerOptions.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const handleCompetitionChange = (val) => {
  if (val) {
    fetchScores(val)
  } else {
    scores.value = []
  }
}

const fetchScores = async (competitionId) => {
  loading.value = true
  try {
    const res = await getReviewScores(competitionId)
    scores.value = res.data || []
  } catch (error) {
    ElMessage.error('获取评审数据失败')
    console.error(error)
    scores.value = []
  } finally {
    loading.value = false
  }
}

const showAssignDialog = () => {
  resetAssignForm()
  assignDialogVisible.value = true
}

const showReviewDetail = async (row) => {
  currentDetail.value = {
    ...row,
    reviewers: [],
    comments: [],
    reviewCount: 0,
    completedCount: 0,
    workFileUrl: row.fileUrl || '',
    workFileName: row.fileName || ''
  }
  detailDialogVisible.value = true
  try {
    const res = await getWorkReviewTasks(row.workId)
    if (res.data) {
      const workTasks = res.data
      currentDetail.value.reviewCount = workTasks.length
      currentDetail.value.completedCount = workTasks.filter(t => t.status === 'COMPLETED').length

      const reviewerIds = [...new Set(workTasks.map(t => t.reviewerId))]
      if (reviewerIds.length > 0) {
        const reviewerRes = await getEligibleReviewers()
        const allUsers = reviewerRes.data || []
        const userMap = {}
        allUsers.forEach(u => { userMap[u.id] = u.name })
        currentDetail.value.reviewers = workTasks.map(t => ({
          reviewerId: t.reviewerId,
          reviewerName: userMap[t.reviewerId] || `用户${t.reviewerId}`,
          score: t.score,
          status: t.status,
          comment: t.comment
        }))
        const comments = workTasks.filter(t => t.comment).map(t => ({
          reviewerName: userMap[t.reviewerId] || `用户${t.reviewerId}`,
          comment: t.comment
        }))
        if (comments.length > 0) {
          currentDetail.value.comments = comments
        }
      }
    }
  } catch (error) {
    console.error('获取评审详情失败', error)
  }
}

const downloadWorkFile = () => {
  if (currentDetail.value?.workFileUrl) {
    window.open(currentDetail.value.workFileUrl, '_blank')
  }
}

const handleAssign = async () => {
  if (!assignFormRef.value) return

  await assignFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await assignReview({
          workId: assignForm.workId,
          reviewerId: assignForm.reviewerId,
          competitionId: selectedCompetition.value
        })
        ElMessage.success('分配成功')
        assignDialogVisible.value = false
        fetchScores(selectedCompetition.value)
      } catch (error) {
        ElMessage.error('分配失败')
        console.error(error)
      }
    }
  })
}

const resetAssignForm = () => {
  assignFormRef.value?.resetFields()
  Object.assign(assignForm, {
    workId: '',
    reviewerId: ''
  })
}
</script>

<style scoped>
.reviews-page {
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
  background: #4facfe;
  top: -50px;
  right: -30px;
}

.deco-circle-2 {
  width: 100px;
  height: 100px;
  background: #43e97b;
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
}
</style>
