<template>
  <div class="appointment-page">
    <div class="page-header">
      <h1>预约指导</h1>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        预约老师
      </el-button>
    </div>

    <el-table :data="appointments" v-loading="loading">
      <el-table-column prop="title" label="预约主题" min-width="150">
        <template #default="{ row }">
          {{ row.title || '未填写主题' }}
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
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button text type="danger" v-if="row.status === 'PENDING'" @click="handleCancel(row.id)">
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建预约对话框 -->
    <el-dialog v-model="dialogVisible" title="预约指导老师" width="500px">
      <el-form :model="appointmentForm" :rules="appointmentRules" ref="appointmentFormRef" label-width="100px">
        <el-form-item label="指导老师" prop="advisorId">
          <el-select v-model="appointmentForm.advisorId" placeholder="请选择指导老师" filterable style="width: 100%">
            <el-option
              v-for="advisor in advisors"
              :key="advisor.id"
              :label="advisor.name"
              :value="advisor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="老师日程">
          <div class="schedule-hint-box" v-loading="scheduleLoading">
            <el-empty v-if="!appointmentForm.advisorId" description="请先选择指导老师" :image-size="48" />
            <el-empty v-else-if="advisorSchedules.length === 0" description="该老师本月暂无可用日程" :image-size="48" />
            <div v-else class="schedule-list">
              <div class="schedule-item" v-for="item in advisorSchedules" :key="item.id">
                <div class="schedule-title">{{ item.title || '日程' }}</div>
                <div class="schedule-time">{{ formatScheduleRange(item.startTime, item.endTime) }}</div>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="预约主题" prop="title">
          <el-input v-model="appointmentForm.title" placeholder="请输入预约主题" maxlength="200" />
        </el-form-item>
        <el-form-item label="预约说明">
          <el-input v-model="appointmentForm.description" type="textarea" :rows="3" placeholder="请输入预约说明" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentDate">
          <el-date-picker
            v-model="appointmentForm.appointmentDate"
            type="datetime"
            placeholder="选择预约时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时长（分钟）">
          <el-input-number v-model="appointmentForm.duration" :min="30" :max="180" :step="30" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="appointmentForm.location" placeholder="请输入地点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAppointments, createAppointment, cancelAppointment, getAdvisorMonthSchedules } from '@/api/appointment'
import { getAvailableAdvisors } from '@/api/team'
import dayjs from 'dayjs'

const loading = ref(false)
const submitting = ref(false)
const appointments = ref([])
const advisors = ref([])
const advisorSchedules = ref([])
const scheduleLoading = ref(false)

const dialogVisible = ref(false)
const appointmentFormRef = ref(null)
const appointmentForm = ref({
  advisorId: null,
  title: '',
  description: '',
  appointmentDate: null,
  duration: 60,
  location: ''
})
const appointmentRules = {
  advisorId: [{ required: true, message: '请选择指导老师', trigger: 'change' }],
  title: [{ required: true, message: '请输入预约主题', trigger: 'blur' }],
  appointmentDate: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

const fetchAppointments = async () => {
  loading.value = true
  try {
    const res = await getMyAppointments()
    appointments.value = res.data || []
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

const fetchAdvisorSchedules = async (advisorId, date) => {
  if (!advisorId) {
    advisorSchedules.value = []
    return
  }
  const target = date ? dayjs(date) : dayjs()
  scheduleLoading.value = true
  try {
    const res = await getAdvisorMonthSchedules(advisorId, target.year(), target.month() + 1)
    advisorSchedules.value = (res.data || []).sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
  } catch (error) {
    advisorSchedules.value = []
    console.error(error)
  } finally {
    scheduleLoading.value = false
  }
}

const showCreateDialog = () => {
  appointmentForm.value = {
    advisorId: null,
    title: '',
    description: '',
    appointmentDate: null,
    duration: 60,
    location: ''
  }
  advisorSchedules.value = []
  dialogVisible.value = true
}

const isWithinAdvisorSchedule = (appointmentDate, duration) => {
  if (!appointmentDate || advisorSchedules.value.length === 0) return false
  const start = dayjs(appointmentDate)
  const end = start.add(duration, 'minute')
  return advisorSchedules.value.some((s) => {
    const scheduleStart = dayjs(s.startTime)
    const scheduleEnd = dayjs(s.endTime)
    return (start.isAfter(scheduleStart) || start.isSame(scheduleStart)) &&
      (end.isBefore(scheduleEnd) || end.isSame(scheduleEnd))
  })
}

const handleSubmit = async () => {
  const valid = await appointmentFormRef.value.validate().catch(() => false)
  if (!valid) return
  if (!isWithinAdvisorSchedule(appointmentForm.value.appointmentDate, appointmentForm.value.duration || 60)) {
    ElMessage.warning('预约时间不在老师日程范围内，请选择老师已安排的时间段')
    return
  }

  submitting.value = true
  try {
    // 转换日期格式
    const formData = {
      ...appointmentForm.value,
      appointmentDate: dayjs(appointmentForm.value.appointmentDate).format('YYYY-MM-DD HH:mm:ss')
    }
    await createAppointment(formData)
    ElMessage.success('预约提交成功，等待老师确认')
    dialogVisible.value = false
    fetchAppointments()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', { type: 'warning' })
    await cancelAppointment(id)
    ElMessage.success('预约已取消')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
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

const formatScheduleRange = (start, end) => {
  return `${dayjs(start).format('MM-DD HH:mm')} ~ ${dayjs(end).format('MM-DD HH:mm')}`
}

const onAdvisorOrDateChange = async () => {
  await fetchAdvisorSchedules(appointmentForm.value.advisorId, appointmentForm.value.appointmentDate)
}

watch(() => appointmentForm.value.advisorId, onAdvisorOrDateChange)
watch(() => appointmentForm.value.appointmentDate, onAdvisorOrDateChange)

onMounted(() => {
  fetchAppointments()
  fetchAdvisors()
})
</script>

<style scoped>
.appointment-page {
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

.schedule-hint-box {
  width: 100%;
  min-height: 110px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 10px;
  background: #fafafa;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 180px;
  overflow-y: auto;
}

.schedule-item {
  background: #fff;
  border-radius: 6px;
  border: 1px solid #eef2ff;
  padding: 8px 10px;
}

.schedule-title {
  font-size: 13px;
  color: #1f2937;
  font-weight: 600;
}

.schedule-time {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}
</style>
