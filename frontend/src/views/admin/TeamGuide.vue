<template>
  <div class="team-guide-page">
    <div class="page-header">
      <h1>团队指导</h1>
      <div class="header-tabs">
        <el-button 
          :type="viewMode === 'teams' ? 'primary' : ''" 
          @click="viewMode = 'teams'"
        >
          我的指导团队
        </el-button>
        <el-button 
          :type="viewMode === 'audits' ? 'primary' : ''" 
          @click="viewMode = 'audits'"
        >
          审核申请
          <el-badge v-if="pendingCount > 0" :value="pendingCount" class="badge" />
        </el-button>
      </div>
    </div>

    <!-- 我的指导团队 -->
    <div v-if="viewMode === 'teams'" class="team-grid" v-loading="loading">
      <el-empty v-if="teams.length === 0" description="暂无指导的团队" />

      <div v-for="team in teams" :key="team.id" class="team-card" @click="goToTeam(team.id)">
        <div class="team-header">
          <h3 class="team-name">{{ team.name }}</h3>
          <el-tag :type="team.status === 'ACTIVE' ? 'success' : 'info'" size="small">
            {{ team.status === 'ACTIVE' ? '进行中' : '已解散' }}
          </el-tag>
        </div>
        <p class="team-desc">{{ team.description || '暂无描述' }}</p>
        <div class="team-meta">
          <div class="meta-item">
            <el-icon><User /></el-icon>
            <span>{{ team.memberCount || 1 }}/{{ team.maxMembers }} 人</span>
          </div>
          <div class="meta-item">
            <el-icon><Avatar /></el-icon>
            <span>队长：{{ team.leaderName }}</span>
          </div>
        </div>
        <div class="team-footer">
          <span class="created">创建于 {{ formatDate(team.createdAt) }}</span>
          <el-button text type="primary" @click.stop="goToTeam(team.id)">
            查看详情 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 审核申请列表 -->
    <div v-if="viewMode === 'audits'" class="audit-list-container" v-loading="loading">
      <el-empty v-if="audits.length === 0" description="暂无审核申请" />
      
      <el-card v-for="audit in audits" :key="audit.id" class="audit-card">
        <div class="audit-header">
          <div class="audit-info">
            <h3 class="team-name">{{ audit.teamName }}</h3>
            <p class="requester">申请人：{{ audit.requesterName }}</p>
          </div>
          <el-tag :type="getStatusType(audit.status)" size="small">
            {{ getStatusText(audit.status) }}
          </el-tag>
        </div>
        <div class="audit-content">
          <p class="reason" v-if="audit.reason">申请理由：{{ audit.reason }}</p>
          <p class="apply-time">申请时间：{{ formatDateTime(audit.createdAt) }}</p>
        </div>
        <div class="audit-actions" v-if="audit.status === 'PENDING'">
          <el-button type="success" @click="handleApprove(audit.id)">
            <el-icon><Check /></el-icon>
            同意
          </el-button>
          <el-button type="danger" @click="showRejectDialog(audit)">
            <el-icon><Close /></el-icon>
            拒绝
          </el-button>
        </div>
        <div class="audit-result" v-else>
          <p v-if="audit.status === 'APPROVED'" class="approved">已同意</p>
          <p v-else-if="audit.status === 'REJECTED'" class="rejected">
            已拒绝：{{ audit.reason || '无理由' }}
          </p>
          <p v-if="audit.reviewedAt">审核时间：{{ formatDateTime(audit.reviewedAt) }}</p>
        </div>
      </el-card>
    </div>

    <!-- 团队详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="currentTeam?.name" width="1000px" top="5vh">
      <div v-if="currentTeam" class="team-detail">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="团队信息" name="info">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-card>
                  <template #header><span>团队成员</span></template>
                  <div class="member-list">
                    <div v-for="member in members" :key="member.id" class="member-item">
                      <el-avatar :size="32">{{ member.userName?.charAt(0) }}</el-avatar>
                      <span class="member-name">{{ member.userName }}</span>
                      <el-tag size="small" :type="member.role === 'LEADER' ? 'warning' : ''">
                        {{ member.role === 'LEADER' ? '队长' : '成员' }}
                      </el-tag>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card>
                  <template #header><span>任务进度</span></template>
                  <div class="task-stats">
                    <div class="stat-item">
                      <span class="stat-label">待处理</span>
                      <span class="stat-value pending">{{ taskStats.pending }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">进行中</span>
                      <span class="stat-value progress">{{ taskStats.inProgress }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">已完成</span>
                      <span class="stat-value completed">{{ taskStats.completed }}</span>
                    </div>
                  </div>
                  <div class="task-list">
                    <div v-for="task in tasks.slice(0, 5)" :key="task.id" class="task-item">
                      <span class="task-title">{{ task.title }}</span>
                      <el-tag :type="getTaskStatusType(task.status)" size="small">
                        {{ getTaskStatusText(task.status) }}
                      </el-tag>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <el-tab-pane label="任务详情" name="tasks">
            <TeamTasks :teamId="currentTeam.id" :members="members" :isLeader="false" />
          </el-tab-pane>
          
          <el-tab-pane label="团队交流" name="messages">
            <TeamMessages :teamId="currentTeam.id" :members="members" :currentUserId="userStore.user?.id" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>

    <!-- 拒绝申请对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝申请" width="400px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝理由">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="3" placeholder="请输入拒绝理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { 
  getAdvisedTeams, 
  getTeamMembers, 
  getTeamTasks,
  getPendingAdvisorAudits,
  approveAdvisorAudit,
  rejectAdvisorAudit
} from '@/api/team'
import TeamTasks from '@/views/participant/components/TeamTasks.vue'
import TeamMessages from '@/views/participant/components/TeamMessages.vue'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const viewMode = ref('teams')
const teams = ref([])
const audits = ref([])
const pendingCount = ref(0)

const detailDialogVisible = ref(false)
const currentTeam = ref(null)
const members = ref([])
const tasks = ref([])
const activeTab = ref('info')

const rejectDialogVisible = ref(false)
const rejectForm = ref({ reason: '' })
const currentAudit = ref(null)

const taskStats = computed(() => {
  const pending = tasks.value.filter(t => t.status === 'PENDING').length
  const inProgress = tasks.value.filter(t => t.status === 'IN_PROGRESS').length
  const completed = tasks.value.filter(t => t.status === 'COMPLETED').length
  return { pending, inProgress, completed }
})

const fetchTeams = async () => {
  loading.value = true
  try {
    const res = await getAdvisedTeams()
    teams.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchAudits = async () => {
  loading.value = true
  try {
    const res = await getPendingAdvisorAudits()
    audits.value = res.data || []
    pendingCount.value = audits.value.filter(a => a.status === 'PENDING').length
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToTeam = async (teamId) => {
  const team = teams.value.find(t => t.id === teamId)
  if (!team) return

  currentTeam.value = team

  try {
    const [membersRes, tasksRes] = await Promise.all([
      getTeamMembers(teamId),
      getTeamTasks(teamId)
    ])
    members.value = membersRes.data || []
    tasks.value = tasksRes.data || []
    detailDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleApprove = async (auditId) => {
  try {
    await approveAdvisorAudit(auditId)
    ElMessage.success('已同意申请')
    fetchAudits()
    fetchTeams()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const showRejectDialog = (audit) => {
  currentAudit.value = audit
  rejectForm.value = { reason: '' }
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  if (!currentAudit.value) return
  
  try {
    await rejectAdvisorAudit(currentAudit.value.id, rejectForm.value.reason)
    ElMessage.success('已拒绝申请')
    rejectDialogVisible.value = false
    fetchAudits()
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败')
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : ''
}

const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}

const getTaskStatusType = (status) => {
  const map = { PENDING: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success' }
  return map[status] || 'info'
}

const getTaskStatusText = (status) => {
  const map = { PENDING: '待处理', IN_PROGRESS: '进行中', COMPLETED: '已完成' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待审核', APPROVED: '已同意', REJECTED: '已拒绝' }
  return map[status] || status
}

watch(viewMode, (newMode) => {
  if (newMode === 'teams') {
    fetchTeams()
  } else if (newMode === 'audits') {
    fetchAudits()
  }
})

onMounted(() => {
  fetchTeams()
})
</script>

<style scoped>
.team-guide-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.header-tabs {
  display: flex;
  gap: 12px;
}

.badge {
  margin-left: 4px;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.team-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.team-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.team-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.team-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
  line-height: 1.5;
}

.team-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #888;
  font-size: 13px;
}

.team-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.created {
  color: #999;
  font-size: 13px;
}

.team-detail {
  min-height: 500px;
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-name {
  flex: 1;
  font-size: 14px;
}

.task-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
}

.stat-value.pending { color: #909399; }
.stat-value.progress { color: #e6a23c; }
.stat-value.completed { color: #67c23a; }

.task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.task-title {
  font-size: 14px;
}

/* 审核申请样式 */
.audit-list-container {
  max-width: 800px;
}

.audit-card {
  margin-bottom: 16px;
}

.audit-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.audit-info {
  flex: 1;
}

.audit-info .team-name {
  font-size: 16px;
  margin-bottom: 4px;
}

.requester {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.audit-content {
  margin-bottom: 16px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.reason {
  font-size: 14px;
  margin: 0 0 8px 0;
}

.apply-time {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.audit-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.audit-result {
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.approved {
  color: #67c23a;
  font-weight: 500;
}

.rejected {
  color: #f56c6c;
}
</style>