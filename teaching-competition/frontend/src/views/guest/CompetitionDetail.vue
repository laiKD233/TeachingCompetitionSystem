<template>
  <div class="competition-detail-page" v-loading="loading">
    <el-card v-if="competition" class="detail-card">
      <template #header>
        <div class="header-content">
          <h1 class="competition-title">{{ competition.name }}</h1>
          <el-tag :type="getStatusType(competition.status)" size="large" effect="plain">
            {{ getStatusText(competition.status) }}
          </el-tag>
        </div>
      </template>

      <div class="detail-content">
        <!-- 封面图 -->
        <div class="cover-section" v-if="competition.coverImage">
          <img :src="competition.coverImage" :alt="competition.name" class="cover-image" />
        </div>

        <!-- 基本信息 -->
        <div class="info-section">
          <h3>竞赛信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">竞赛类型：</span>
              <span class="value">{{ competition.type }}</span>
            </div>
            <div class="info-item">
              <span class="label">竞赛主题：</span>
              <span class="value">{{ competition.theme || '未设置' }}</span>
            </div>
          </div>
        </div>

        <!-- 时间信息 -->
        <div class="time-section">
          <h3>时间安排</h3>
          <el-timeline>
            <el-timeline-item timestamp="报名开始时间" placement="top">
              <span>{{ formatDate(competition.registrationStart) }}</span>
            </el-timeline-item>
            <el-timeline-item timestamp="报名截止时间" placement="top">
              <span>{{ formatDate(competition.registrationEnd) }}</span>
            </el-timeline-item>
            <el-timeline-item timestamp="作品提交截止" placement="top">
              <span>{{ formatDate(competition.submissionDeadline) }}</span>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 竞赛描述 -->
        <div class="description-section">
          <h3>竞赛描述</h3>
          <p class="description-text">{{ competition.description || '暂无描述' }}</p>
        </div>

        <!-- 竞赛规则 -->
        <div class="rules-section" v-if="competition.rules">
          <h3>竞赛规则</h3>
          <div class="rules-content" v-html="competition.rules"></div>
        </div>

        <!-- 报名按钮 -->
        <div class="action-section" v-if="competition.status === 'REGISTRATION'">
          <el-button type="primary" size="large" @click="showRegisterDialog = true">
            <el-icon><Edit /></el-icon>
            立即报名
          </el-button>
        </div>
      </div>
    </el-card>

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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompetition } from '@/api/competition'
import { createRegistration } from '@/api/registration'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const competition = ref(null)
const showRegisterDialog = ref(false)

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
  } catch (error) {
    ElMessage.error('获取竞赛详情失败')
    console.error(error)
  } finally {
    loading.value = false
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
        'REGISTRATION': '已报名',
        'ONGOING': '进行中',
        'REVIEWED': '已评审',
        'ANNOUNCEMENT': '公示中',
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
</script>

<style scoped>
.competition-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-card {
  border-radius: 12px;
  overflow: hidden;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.competition-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
}

.detail-content {
  padding: 20px 0;
}

.cover-section {
  margin-bottom: 30px;
  text-align: center;
}

.cover-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  box-shadow: var(--shadow-md);
}

.info-section,
.time-section,
.description-section,
.rules-section {
  margin-bottom: 30px;
}

h3 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: var(--bg-light);
  border-radius: 8px;
}

.label {
  font-weight: 600;
  color: var(--text-secondary);
  margin-right: 8px;
}

.value {
  color: var(--text-primary);
}

.description-text {
  line-height: 1.8;
  color: var(--text-secondary);
  white-space: pre-wrap;
}

.rules-content {
  background: var(--bg-light);
  padding: 20px;
  border-radius: 8px;
  line-height: 1.8;
}

.action-section {
  text-align: center;
  padding: 40px 0;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 12px;
}

.register-form {
  padding: 20px 0;
}

@media (max-width: 768px) {
  .competition-detail-page {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .competition-title {
    font-size: 20px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
