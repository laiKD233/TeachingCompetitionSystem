<template>
  <div class="team-list-page">
    <div class="page-header">
      <h1>{{ pageTitle }}</h1>
      <div class="header-actions">
        <el-button :disabled="isStudent && hasTeam" @click="joinDialogVisible = true">
          <el-icon><Link /></el-icon>
          加入团队
        </el-button>
        <el-button type="primary" :disabled="isStudent && hasTeam" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          创建团队
        </el-button>
      </div>
    </div>
    <el-alert
      v-if="isStudent && hasTeam"
      title="你已加入团队，当前每位参赛者仅支持一个团队。"
      type="info"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <div class="team-grid" v-loading="loading">
      <el-empty v-if="teams.length === 0" :description="isStudent ? '暂无团队' : '暂无团队数据'">
        <el-button type="primary" @click="showCreateDialog">创建第一个团队</el-button>
      </el-empty>

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
          <div class="meta-item" v-if="team.advisorName">
            <el-icon><Avatar /></el-icon>
            <span>指导老师：{{ team.advisorName }}</span>
          </div>
        </div>
        <div class="team-footer">
          <span class="leader">队长：{{ team.leaderName }}</span>
          <el-button text type="primary" @click.stop="goToTeam(team.id)">
            进入团队 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 加入团队对话框 -->
    <el-dialog v-model="joinDialogVisible" title="加入团队" width="400px">
      <el-form :model="joinForm" label-width="80px">
        <el-form-item label="团队ID">
          <el-input v-model="joinForm.teamId" placeholder="请输入团队ID" />
        </el-form-item>
        <el-form-item label="邀请码">
          <el-input v-model="joinForm.inviteCode" placeholder="请输入邀请码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="joinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleJoinTeam">加入</el-button>
      </template>
    </el-dialog>

    <!-- 创建团队对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建团队" width="500px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入团队名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="所属竞赛" prop="competitionId">
          <el-select v-model="createForm.competitionId" placeholder="请选择团队赛竞赛" filterable style="width: 100%">
            <el-option
              v-for="competition in competitions"
              :key="competition.id"
              :label="competition.name"
              :value="competition.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="团队描述">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入团队描述" />
        </el-form-item>
        <el-form-item label="指导老师">
          <el-select v-model="createForm.advisorId" placeholder="请选择指导老师" clearable filterable multiple style="width: 100%">
            <el-option
              v-for="advisor in advisors"
              :key="advisor.id"
              :label="advisor.name"
              :value="advisor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="createForm.maxMembers" :min="2" :max="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateTeam" :loading="submitting">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { getMyTeams, createTeam, joinTeam, getAvailableAdvisors } from '@/api/team'
import { getPublicCompetitions } from '@/api/competition'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const teams = ref([])
const advisors = ref([])
const competitions = ref([])
const hasTeam = ref(false)
const isStudent = ref(false)
const pageTitle = computed(() => {
  if (userStore.user?.role === 'STUDENT') return '我的团队'
  if (userStore.user?.role === 'ADVISOR') return '指导团队'
  return '团队列表'
})

const joinDialogVisible = ref(false)
const joinForm = ref({ teamId: '', inviteCode: '' })

const createDialogVisible = ref(false)
const createFormRef = ref(null)
const createForm = ref({
  name: '',
  competitionId: null,
  description: '',
  advisorId: [],
  maxMembers: 5
})
const createRules = {
  name: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
  competitionId: [{ required: true, message: '请选择所属竞赛', trigger: 'change' }]
}

const fetchTeams = async () => {
  loading.value = true
  try {
    const res = await getMyTeams()
    teams.value = res.data || []
    hasTeam.value = teams.value.some(team => team.status === 'ACTIVE')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
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

const fetchCompetitions = async () => {
  try {
    const res = await getPublicCompetitions({ page: 1, size: 100 })
    const records = res?.data?.records || []
    competitions.value = records.filter(item => item.participationType === 'TEAM')
  } catch (error) {
    console.error(error)
  }
}

const showCreateDialog = () => {
  if (competitions.value.length === 0) {
    ElMessage.warning('当前暂无可用于建队的团队赛竞赛')
    return
  }
  createForm.value = { name: '', competitionId: null, description: '', advisorId: [], maxMembers: 5 }
  createDialogVisible.value = true
}

const handleCreateTeam = async () => {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createTeam(createForm.value)
    ElMessage.success('团队创建成功')
    createDialogVisible.value = false
    fetchTeams()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleJoinTeam = async () => {
  if (!joinForm.value.teamId || !joinForm.value.inviteCode) {
    ElMessage.warning('请输入团队ID和邀请码')
    return
  }
  try {
    await joinTeam(joinForm.value.teamId, joinForm.value.inviteCode)
    ElMessage.success('加入团队成功')
    joinDialogVisible.value = false
    fetchTeams()
  } catch (error) {
    console.error(error)
  }
}

const goToTeam = (id) => {
  router.push(`/participant/team/${id}`)
}

onMounted(() => {
  isStudent.value = userStore.user?.role === 'STUDENT'
  fetchTeams()
  fetchAdvisors()
  fetchCompetitions()
})
</script>

<style scoped>
.team-list-page {
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

.header-actions {
  display: flex;
  gap: 12px;
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

.leader {
  color: #666;
  font-size: 13px;
}
</style>
