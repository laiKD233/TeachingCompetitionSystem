<template>
  <div class="schedule-page">
    <h1>日程安排</h1>
    <div class="calendar-container">
      <div class="calendar-header">
        <el-button-group>
          <el-button @click="prevMonth">上一月</el-button>
          <el-button @click="today">今天</el-button>
          <el-button @click="nextMonth">下一月</el-button>
        </el-button-group>
        <span class="current-month">{{ formatCurrentMonth() }}</span>
        <el-button type="primary" @click="showCreateDialog">添加日程</el-button>
      </div>
      <el-calendar v-model="currentDate">
        <template #date-cell="{ data }">
          <div class="calendar-day">
            <div class="day-number">{{ data.day.split('-').slice(2).join('-') }}</div>
            <div class="day-events">
              <div
                v-for="event in getEventsForDate(data.day)"
                :key="event.id"
                class="event-item"
                :style="{ background: event.color || '#409eff' }"
                @click.stop="showEventDetail(event)"
              >
                {{ event.title }}
              </div>
            </div>
          </div>
        </template>
      </el-calendar>
    </div>

    <!-- 创建/编辑日程对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日程' : '添加日程'" width="500px">
      <el-form :model="scheduleForm" :rules="scheduleRules" ref="scheduleFormRef" label-width="100px">
        <el-form-item label="日程标题" prop="title">
          <el-input v-model="scheduleForm.title" placeholder="请输入日程标题" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="scheduleForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="scheduleForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="scheduleForm.location" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="scheduleForm.color" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="scheduleForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="display: flex; justify-content: space-between;">
          <el-button v-if="isEdit" type="danger" @click="handleDelete">删除</el-button>
          <div style="flex: 1"></div>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '保存' : '确定' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMonthSchedules, createSchedule, updateSchedule, deleteSchedule } from '@/api/schedule'
import dayjs from 'dayjs'

const currentDate = ref(new Date())
const schedules = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editScheduleId = ref(null)

const scheduleFormRef = ref(null)
const scheduleForm = ref({
  title: '',
  startTime: null,
  endTime: null,
  location: '',
  color: '#409eff',
  description: ''
})
const scheduleRules = {
  title: [{ required: true, message: '请输入日程标题', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const fetchSchedules = async () => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth() + 1
  try {
    const res = await getMonthSchedules(year, month)
    schedules.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const getEventsForDate = (dateStr) => {
  return schedules.value.filter(s => {
    const startDate = dayjs(s.startTime).format('YYYY-MM-DD')
    return startDate === dateStr
  })
}

const showCreateDialog = () => {
  isEdit.value = false
  editScheduleId.value = null
  scheduleForm.value = {
    title: '',
    startTime: null,
    endTime: null,
    location: '',
    color: '#409eff',
    description: ''
  }
  dialogVisible.value = true
}

const showEventDetail = (event) => {
  isEdit.value = true
  editScheduleId.value = event.id
  scheduleForm.value = {
    title: event.title,
    startTime: event.startTime,
    endTime: event.endTime,
    location: event.location || '',
    color: event.color || '#409eff',
    description: event.description || ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await scheduleFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 转换日期格式为后端期望的格式
    const formData = {
      ...scheduleForm.value,
      startTime: dayjs(scheduleForm.value.startTime).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(scheduleForm.value.endTime).format('YYYY-MM-DD HH:mm:ss')
    }
    
    if (isEdit.value) {
      await updateSchedule(editScheduleId.value, formData)
      ElMessage.success('日程更新成功')
    } else {
      await createSchedule(formData)
      ElMessage.success('日程添加成功')
    }
    
    dialogVisible.value = false
    fetchSchedules()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该日程吗？', '提示', {
      type: 'warning'
    })
    await deleteSchedule(editScheduleId.value)
    ElMessage.success('日程已删除')
    dialogVisible.value = false
    fetchSchedules()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const formatCurrentMonth = () => {
  return dayjs(currentDate.value).format('YYYY年MM月')
}

const prevMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1)
}

const today = () => {
  currentDate.value = new Date()
}

watch(currentDate, () => {
  fetchSchedules()
})

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
.schedule-page {
  padding: 20px;
}

.schedule-page h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 24px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
}

.current-month {
  font-size: 18px;
  font-weight: 600;
}

.calendar-day {
  height: 100%;
  padding: 4px;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.event-item {
  font-size: 12px;
  padding: 2px 4px;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
