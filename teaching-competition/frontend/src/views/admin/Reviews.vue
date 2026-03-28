<template>
  <div class="reviews-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">评审分配</h1>
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

        <el-table-column prop="score" label="得分" width="100">
          <template #default="scope">
            <span v-if="scope.row.score" class="score-value">{{ scope.row.score }}</span>
            <el-tag v-else type="warning" size="small" effect="plain">待评审</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="reviewerName" label="评审人" width="120">
          <template #default="scope">
            <span>{{ scope.row.reviewerName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="comment" label="评语" min-width="200">
          <template #default="scope">
            <span>{{ scope.row.comment || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="!selectedCompetition" description="请选择竞赛查看评审情况" />
    <el-empty v-if="selectedCompetition && scores.length === 0 && !loading" description="暂无评审数据" />

    <!-- 分配评审对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配评审任务" width="500px" @close="resetAssignForm">
      <el-form :model="assignForm" :rules="assignRules" ref="assignFormRef" label-width="100px">
        <el-form-item label="参赛作品" prop="workId">
          <el-select v-model="assignForm.workId" placeholder="请选择作品" style="width: 100%">
            <el-option
              v-for="item in scores.filter(s => !s.score)"
              :key="item.workId || item.id"
              :label="item.workTitle || item.title"
              :value="item.workId || item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="评审人" prop="reviewerId">
          <el-input v-model="assignForm.reviewerId" placeholder="请输入评审人ID" />
        </el-form-item>

        <el-form-item label="评分" prop="score">
          <el-input-number v-model="assignForm.score" :min="0" :max="100" style="width: 100%" />
        </el-form-item>

        <el-form-item label="评语" prop="comment">
          <el-input
            v-model="assignForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入评语"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 打分对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="提交评分" width="500px" @close="resetScoreForm">
      <el-form :model="scoreForm" :rules="scoreRules" ref="scoreFormRef" label-width="80px">
        <el-form-item label="评分" prop="score">
          <el-input-number v-model="scoreForm.score" :min="0" :max="100" style="width: 100%" />
        </el-form-item>

        <el-form-item label="评语">
          <el-input
            v-model="scoreForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入评语"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitScore">提交评分</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminCompetitions } from '@/api/competition'
import { assignReview, submitScore, getReviewScores } from '@/api/review'

const loading = ref(false)
const selectedCompetition = ref(null)
const competitionOptions = ref([])
const scores = ref([])
const assignDialogVisible = ref(false)
const scoreDialogVisible = ref(false)
const assignFormRef = ref(null)
const scoreFormRef = ref(null)

const assignForm = reactive({
  workId: '',
  reviewerId: '',
  score: 80,
  comment: ''
})

const scoreForm = reactive({
  taskId: null,
  workId: null,
  score: 80,
  comment: ''
})

const assignRules = {
  workId: [{ required: true, message: '请选择作品', trigger: 'change' }],
  reviewerId: [{ required: true, message: '请输入评审人ID', trigger: 'blur' }]
}

const scoreRules = {
  score: [{ required: true, message: '请输入评分', trigger: 'blur' }]
}

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

const handleSubmitScore = async () => {
  if (!scoreFormRef.value) return

  await scoreFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitScore({
          taskId: scoreForm.taskId,
          workId: scoreForm.workId,
          score: scoreForm.score,
          comment: scoreForm.comment
        })
        ElMessage.success('评分提交成功')
        scoreDialogVisible.value = false
        fetchScores(selectedCompetition.value)
      } catch (error) {
        ElMessage.error('评分提交失败')
        console.error(error)
      }
    }
  })
}

const resetAssignForm = () => {
  assignFormRef.value?.resetFields()
  Object.assign(assignForm, {
    workId: '',
    reviewerId: '',
    score: 80,
    comment: ''
  })
}

const resetScoreForm = () => {
  scoreFormRef.value?.resetFields()
  Object.assign(scoreForm, {
    taskId: null,
    workId: null,
    score: 80,
    comment: ''
  })
}
</script>

<style scoped>
.reviews-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
