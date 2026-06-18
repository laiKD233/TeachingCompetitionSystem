<template>
  <div class="team-detail-page" v-loading="loading">
    <div class="page-header" v-if="team">
      <div class="header-left">
        <el-button text @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h1>{{ team.name }}</h1>
        <el-tag :type="team.status === 'ACTIVE' ? 'success' : 'info'" size="small">
          {{ team.status === 'ACTIVE' ? '进行中' : '已解散' }}
        </el-tag>
      </div>
      <div class="header-right">
        <el-button @click="showInviteDialog" v-if="isLeader">
          <el-icon><Link /></el-icon>
          邀请成员
        </el-button>
        <el-button type="primary" @click="showEditDialog" v-if="isLeader">
          <el-icon><Edit /></el-icon>
          编辑团队
        </el-button>
        <el-button type="danger" @click="handleDeleteTeam" v-if="isLeader">
          <el-icon><Delete /></el-icon>
          解散团队
        </el-button>
      </div>
    </div>

    <div class="team-content" v-if="team">
      <!-- 团队信息卡片 -->
      <el-row :gutter="20">
        <el-col :span="16">
          <!-- 标签页 -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="任务安排" name="tasks">
              <TeamTasks :teamId="teamId" :members="members" :isLeader="isLeader" />
            </el-tab-pane>
            <el-tab-pane label="团队交流" name="messages">
              <TeamMessages :teamId="teamId" :members="members" :currentUserId="userStore.user?.id" />
            </el-tab-pane>
          </el-tabs>
        </el-col>

        <el-col :span="8">
          <!-- 团队成员 -->
          <el-card class="members-card">
            <template #header>
              <div class="card-header">
                <span>团队成员 ({{ members.length }}/{{ team.maxMembers }})</span>
              </div>
            </template>
            <div class="member-list">
              <div v-for="member in members" :key="member.id" class="member-item">
                <el-avatar :size="36">{{ member.userName?.charAt(0) }}</el-avatar>
                <div class="member-info">
                  <span class="member-name">{{ member.userName }}</span>
                  <el-tag size="small" :type="member.role === 'LEADER' ? 'warning' : ''">
                    {{ member.role === 'LEADER' ? '队长' : '成员' }}
                  </el-tag>
                </div>
                <el-button
                  v-if="isLeader && member.role !== 'LEADER'"
                  text
                  type="danger"
                  size="small"
                  @click="handleRemoveMember(member.userId)"
                >
                  移除
                </el-button>
              </div>
            </div>
          </el-card>

          <!-- 团队信息 -->
          <el-card class="info-card">
            <template #header>
              <span>团队信息</span>
            </template>
            <div class="info-list">
              <div class="info-item">
                <span class="label">队长</span>
                <span class="value">{{ team.leaderName }}</span>
              </div>
              <div class="info-item" v-if="team.advisorName">
                <span class="label">指导老师</span>
                <span class="value">{{ team.advisorName }}</span>
              </div>
              <div class="info-item" v-if="pendingAudits.length > 0">
                <span class="label">待审核指导老师</span>
                <div class="audit-list">
                  <div v-for="audit in pendingAudits" :key="audit.id" class="audit-item">
                    <span>{{ audit.advisorName }}</span>
                    <el-tag type="warning" size="small">审核中</el-tag>
                  </div>
                </div>
              </div>
              <div class="info-item">
                <span class="label">创建时间</span>
                <span class="value">{{ formatDate(team.createdAt) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 邀请对话框 -->
    <el-dialog v-model="inviteDialogVisible" title="邀请成员" width="400px">
      <div class="invite-content">
        <p>分享以下信息给队友，让他们加入团队：</p>
        <div class="invite-info">
          <div class="invite-item">
            <span class="label">团队ID：</span>
            <span class="value">{{ teamId }}</span>
            <el-button text type="primary" @click="copyText(teamId)">复制</el-button>
          </div>
          <div class="invite-item">
            <span class="label">邀请码：</span>
            <span class="value">{{ inviteCode }}</span>
            <el-button text type="primary" @click="copyText(inviteCode)">复制</el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑团队对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑团队" width="500px">
      <el-form :model="editForm" :rules="createRules" ref="editFormRef" label-width="100px">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入团队名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="团队描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入团队描述" />
        </el-form-item>
        <el-form-item label="指导老师">
          <el-select v-model="editForm.advisorId" placeholder="请选择指导老师" clearable filterable multiple style="width: 100%">
            <el-option
              v-for="advisor in advisors"
              :key="advisor.id"
              :label="advisor.name"
              :value="advisor.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateTeam" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getTeamDetail, getTeamMembers, getInviteCode, updateTeam, removeMember, getAvailableAdvisors, getTeamAudits, deleteTeam } from '@/api/team'
import TeamTasks from '@/views/participant/components/TeamTasks.vue'
import TeamMessages from '@/views/participant/components/TeamMessages.vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const teamId = computed(() => route.params.id)
const loading = ref(false)
const submitting = ref(false)
const team = ref(null)
const members = ref([])
const advisors = ref([])
const inviteCode = ref('')
const activeTab = ref('tasks')
const pendingAudits = ref([])

const inviteDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  name: '',
  description: '',
  advisorId: []
})
const createRules = {
  name: [{ required: true, message: '请输入团队名称', trigger: 'blur' }]
}

const isLeader = computed(() => team.value?.leaderId === userStore.user?.id)

const fetchTeamDetail = async () => {
  loading.value = true
  try {
    const res = await getTeamDetail(teamId.value)
    team.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchMembers = async () => {
  try {
    const res = await getTeamMembers(teamId.value)
    members.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const fetchAdvisors = async () => {
  try {
    const res = await getAvailableAdvisors()
    advisors.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const fetchAudits = async () => {
  try {
    const res = await getTeamAudits(teamId.value)
    pendingAudits.value = (res.data || []).filter(a => a.status === 'PENDING')
  } catch (error) {
    console.error(error)
  }
}

const showInviteDialog = async () => {
  try {
    const res = await getInviteCode(teamId.value)
    inviteCode.value = res.data
    inviteDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const showEditDialog = () => {
  editForm.value = {
    name: team.value.name,
    description: team.value.description || '',
    advisorId: team.value.advisorIds || []
  }
  editDialogVisible.value = true
}

const handleUpdateTeam = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await updateTeam(teamId.value, editForm.value)
    ElMessage.success('团队信息更新成功')
    editDialogVisible.value = false
    fetchTeamDetail()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleRemoveMember = async (memberId) => {
  try {
    await ElMessageBox.confirm('确定要移除该成员吗？', '提示', {
      type: 'warning'
    })
    await removeMember(teamId.value, memberId)
    ElMessage.success('成员已移除')
    fetchMembers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleDeleteTeam = async () => {
  try {
    await ElMessageBox.confirm('确定要解散团队吗？此操作不可撤销！', '警告', {
      type: 'warning',
      confirmButtonText: '确定解散',
      cancelButtonText: '取消'
    })
    await deleteTeam(teamId.value)
    ElMessage.success('团队已解散')
    router.push('/participant/teams')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('操作失败')
    }
  }
}

const copyText = (text) => {
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制到剪贴板')
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
}

watch(teamId, () => {
  if (teamId.value) {
    fetchTeamDetail()
    fetchMembers()
    fetchAdvisors()
    fetchAudits()
  }
}, { immediate: true })
</script>

<style scoped>
.team-detail-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.header-right {
  display: flex;
  gap: 12px;
}

.members-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.member-name {
  font-size: 14px;
}

.info-card {
  margin-bottom: 20px;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.info-item .label {
  color: #666;
  font-size: 14px;
}

.info-item .value {
  font-size: 14px;
}

.invite-content {
  text-align: center;
}

.invite-info {
  margin-top: 20px;
}

.invite-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}

.invite-item .label {
  color: #666;
}

.invite-item .value {
  font-weight: 600;
  font-size: 16px;
}

.audit-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.audit-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
