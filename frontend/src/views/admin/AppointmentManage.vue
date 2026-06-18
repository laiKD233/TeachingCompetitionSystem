<template>
  <div class="appointment-manage-page">
    <h1>预约管理</h1>

    <el-tabs v-model="activeTab" @tab-change="fetchAppointments">
      <el-tab-pane label="全部" name=""></el-tab-pane>
      <el-tab-pane label="待确认" name="PENDING"></el-tab-pane>
      <el-tab-pane label="已确认" name="APPROVED"></el-tab-pane>
      <el-tab-pane label="已完成" name="COMPLETED"></el-tab-pane>
    </el-tabs>

    <el-table :data="appointments" v-loading="loading">
      <el-table-column prop="title" label="预约主题" min-width="150">
        <template #default="{ row }">
          {{ row.title || '未填写主题' }}
        </template>
      </el-table-column>
      <el-table-column prop="studentName" label="学生" width="120">
        <template #default="{ row }">
          {{ row.studentName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="advisorName" label="指导老师" width="120">
        <template #default="{ row }">
          {{ row.advisorName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="appointmentDate" label="预约时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.appointmentDate) }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长" width="80">
        <template #default="{ row }">
          {{ row.duration }}分钟
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text type="success" v-if="row.status === 'PENDING'" @click="handleApprove(row.id)">
            确认
          </el-button>
          <el-button text type="danger" v-if="row.status === 'PENDING'" @click="showRejectDialog(row)">
            拒绝
          </el-button>
          <el-button text type="primary" v-if="row.status === 'APPROVED'" @click="handleComplete(row.id)">
            完成
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 拒绝对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝预约" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdvisorAppointments, approveAppointment, rejectAppointment, completeAppointment } from '@/api/appointment'
import dayjs from 'dayjs'

const loading = ref(false)
const appointments = ref([])
const activeTab = ref('')

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRejectId = ref(null)

const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getAdvisorAppointments(activeTab.value)
    appointments.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    await approveAppointment(id)
    ElMessage.success('预约已确认')
    fetchAppointments()
  } catch (error) {
    console.error(error)
  }
}

const showRejectDialog = (appointment) => {
  currentRejectId.value = appointment.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  try {
    await rejectAppointment(currentRejectId.value, rejectReason.value)
    ElMessage.success('预约已拒绝')
    rejectDialogVisible.value = false
    fetchAppointments()
  } catch (error) {
    console.error(error)
  }
}

const handleComplete = async (id) => {
  try {
    await completeAppointment(id)
    ElMessage.success('预约已完成')
    fetchAppointments()
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    COMPLETED: 'info',
    CANCELLED: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    PENDING: '待确认',
    APPROVED: '已确认',
    REJECTED: '已拒绝',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

onMounted(() => {
  fetchAppointments()
})
</script>

<style scoped>
.appointment-manage-page {
  padding: 20px;
}

.appointment-manage-page h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
}
</style>
