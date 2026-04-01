<template>
  <div class="my-reviews-page">
    <!-- 页面标题 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">我的评审</h1>
          <p class="page-subtitle">查看并完成分配给您的评审任务</p>
        </div>
        <div class="header-stats">
          <div class="header-stat">
            <span class="header-stat-value">{{ pendingCount }}</span>
            <span class="header-stat-label">待评审</span>
          </div>
          <div class="header-stat">
            <span class="header-stat-value">{{ completedCount }}</span>
            <span class="header-stat-label">已完成</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选标签 -->
    <el-card class="filter-card">
      <el-radio-group v-model="statusFilter" @change="fetchTasks">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待评审</el-radio-button>
        <el-radio-button label="COMPLETED">已完成</el-radio-button>
      </el-radio-group>
    </el-card>

    <!-- 评审任务列表 -->
    <el-card class="table-card">
      <el-table :data="filteredTasks" v-loading="loading" stripe>
        <el-table-column prop="competitionName" label="竞赛" width="140" />
        <el-table-column prop="participantName" label="参赛者" width="100">
          <template #default="scope">
            <span>{{ scope.row.participantName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="workTitle" label="作品名称" min-width="160" />

        <el-table-column label="作品文件" width="120">
          <template #default="scope">
            <el-button
              v-if="scope.row.workFileUrl"
              type="primary"
              size="small"
              text
              @click="downloadFile(scope.row)"
            >
              <el-icon><Download /></el-icon>
              <span>下载</span>
            </el-button>
            <span v-else class="text-muted">无文件</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
              {{ scope.row.status === 'COMPLETED' ? '已评审' : '待评审' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="我的评分" width="100">
          <template #default="scope">
            <span v-if="scope.row.score !== null" class="score-value">
              {{ Number(scope.row.score).toFixed(1) }}
            </span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="openScoreDialog(scope.row)"
            >
              <el-icon><EditPen /></el-icon>
              <span>打分</span>
            </el-button>
            <el-button
              v-else
              type="info"
              size="small"
              plain
              @click="viewScore(scope.row)"
            >
              <el-icon><View /></el-icon>
              <span>查看</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-if="!loading && filteredTasks.length === 0" description="暂无评审任务" />

    <!-- 打分对话框 -->
    <el-dialog v-model="scoreDialogVisible" title="提交评审打分" width="520px" @close="resetScoreForm">
      <div class="score-dialog-info" v-if="currentTask">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="竞赛">{{ currentTask.competitionName }}</el-descriptions-item>
          <el-descriptions-item label="作品">{{ currentTask.workTitle }}</el-descriptions-item>
          <el-descriptions-item label="参赛者">{{ currentTask.participantName || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div class="download-section" v-if="currentTask.workFileUrl">
          <el-button type="primary" @click="downloadFile(currentTask)" style="margin-top: 12px">
            <el-icon><Download /></el-icon>
            <span>下载作品文件：{{ currentTask.workFileName }}</span>
          </el-button>
        </div>
      </div>

      <el-divider />

      <el-form :model="scoreForm" :rules="scoreRules" ref="scoreFormRef" label-width="80px">
        <el-form-item label="评分" prop="score">
          <el-input-number v-model="scoreForm.score" :min="0" :max="100" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input
            v-model="scoreForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入评审评语（选填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitScore" :loading="submitting">提交评分</el-button>
      </template>
    </el-dialog>

    <!-- 查看评分对话框 -->
    <el-dialog v-model="viewDialogVisible" title="评审详情" width="500px">
      <el-descriptions :column="1" border v-if="viewTask">
        <el-descriptions-item label="竞赛">{{ viewTask.competitionName }}</el-descriptions-item>
        <el-descriptions-item label="作品">{{ viewTask.workTitle }}</el-descriptions-item>
        <el-descriptions-item label="参赛者">{{ viewTask.participantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <span class="score-value">{{ Number(viewTask.score).toFixed(1) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="评语">
          <span>{{ viewTask.comment || '无' }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { EditPen, View, Download } from '@element-plus/icons-vue'
import { submitScore, getMyReviewTaskDetails } from '@/api/review'

const loading = ref(false)
const submitting = ref(false)
const tasks = ref([])
const statusFilter = ref('')
const scoreDialogVisible = ref(false)
const viewDialogVisible = ref(false)
const scoreFormRef = ref(null)
const currentTask = ref(null)
const viewTask = ref(null)

const scoreForm = reactive({
  taskId: null,
  score: 80,
  comment: ''
})

const scoreRules = {
  score: [{ required: true, message: '请输入评分', trigger: 'blur' }]
}

const pendingCount = computed(() => tasks.value.filter(t => t.status === 'PENDING').length)
const completedCount = computed(() => tasks.value.filter(t => t.status === 'COMPLETED').length)

const filteredTasks = computed(() => {
  if (!statusFilter.value) return tasks.value
  return tasks.value.filter(t => t.status === statusFilter.value)
})

onMounted(() => {
  fetchTasks()
})

const fetchTasks = async () => {
  loading.value = true
  try {
    const res = await getMyReviewTaskDetails()
    tasks.value = res.data || []
  } catch (error) {
    ElMessage.error('获取评审任务失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const downloadFile = (row) => {
  if (row.workFileUrl) {
    window.open(row.workFileUrl, '_blank')
  }
}

const openScoreDialog = (row) => {
  currentTask.value = row
  scoreForm.taskId = row.id
  scoreForm.score = 80
  scoreForm.comment = ''
  scoreDialogVisible.value = true
}

const viewScore = (row) => {
  viewTask.value = row
  viewDialogVisible.value = true
}

const handleSubmitScore = async () => {
  if (!scoreFormRef.value) return

  await scoreFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await submitScore({
          taskId: scoreForm.taskId,
          score: scoreForm.score,
          comment: scoreForm.comment
        })
        ElMessage.success('评分提交成功')
        scoreDialogVisible.value = false
        fetchTasks()
      } catch (error) {
        ElMessage.error('评分提交失败')
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetScoreForm = () => {
  scoreFormRef.value?.resetFields()
  Object.assign(scoreForm, {
    taskId: null,
    score: 80,
    comment: ''
  })
}
</script>

<style scoped>
.my-reviews-page {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-stats {
  display: flex;
  gap: 24px;
}

.header-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.header-stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #ffffff;
}

.header-stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 2px;
}

.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
}

.table-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.score-value {
  font-size: 16px;
  font-weight: 700;
  color: var(--el-color-primary);
}

.text-muted {
  color: var(--text-tertiary);
  font-size: 13px;
}

.download-section {
  text-align: center;
}

.score-dialog-info {
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>
