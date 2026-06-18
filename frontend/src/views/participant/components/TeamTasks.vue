<template>
  <div class="team-tasks">
    <div class="tasks-header">
      <el-button type="primary" @click="showCreateDialog" v-if="isLeader">
        <el-icon><Plus /></el-icon>
        新建任务
      </el-button>
      <el-radio-group v-model="filterStatus" size="small">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待处理</el-radio-button>
        <el-radio-button label="IN_PROGRESS">进行中</el-radio-button>
        <el-radio-button label="COMPLETED">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <div class="tasks-list" v-loading="loading">
      <el-empty v-if="filteredTasks.length === 0" description="暂无任务" />

      <div v-for="task in filteredTasks" :key="task.id" class="task-item">
        <div class="task-main">
          <div class="task-left">
            <el-checkbox
              :model-value="task.status === 'COMPLETED'"
              @change="(val) => handleStatusChange(task, val)"
            />
            <div class="task-info">
              <h4 class="task-title" :class="{ completed: task.status === 'COMPLETED' }">
                {{ task.title }}
              </h4>
              <p class="task-desc" v-if="task.description">{{ task.description }}</p>
            </div>
          </div>
          <div class="task-right">
            <el-tag :type="getPriorityType(task.priority)" size="small">
              {{ getPriorityText(task.priority) }}
            </el-tag>
            <el-tag :type="getStatusType(task.status)" size="small">
              {{ getStatusText(task.status) }}
            </el-tag>
            <span class="assignee" v-if="task.assigneeName">
              <el-icon><User /></el-icon>
              {{ task.assigneeName }}
            </span>
            <span class="due-date" v-if="task.dueDate">
              <el-icon><Clock /></el-icon>
              {{ formatDate(task.dueDate) }}
            </span>
          </div>
        </div>
        <div class="task-actions" v-if="isLeader">
          <el-button text size="small" @click="showEditDialog(task)">编辑</el-button>
          <el-button text size="small" type="danger" @click="handleDelete(task.id)">删除</el-button>
        </div>
      </div>
    </div>

    <!-- 创建/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新建任务'" width="500px">
      <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="80px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="taskForm.assigneeId" placeholder="请选择负责人" clearable style="width: 100%">
            <el-option
              v-for="member in members"
              :key="member.userId"
              :label="member.userName"
              :value="member.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="taskForm.priority">
            <el-radio label="LOW">低</el-radio>
            <el-radio label="MEDIUM">中</el-radio>
            <el-radio label="HIGH">高</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="taskForm.dueDate"
            type="datetime"
            placeholder="选择截止日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTeamTasks, createTask, updateTask, deleteTask, updateTaskStatus } from '@/api/team'
import dayjs from 'dayjs'

const props = defineProps({
  teamId: [String, Number],
  members: { type: Array, default: () => [] },
  isLeader: Boolean
})

const loading = ref(false)
const submitting = ref(false)
const tasks = ref([])
const filterStatus = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const editTaskId = ref(null)
const taskFormRef = ref(null)
const taskForm = ref({
  title: '',
  description: '',
  assigneeId: null,
  priority: 'MEDIUM',
  dueDate: null
})
const taskRules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
}

const filteredTasks = computed(() => {
  if (!filterStatus.value) return tasks.value
  return tasks.value.filter(t => t.status === filterStatus.value)
})

const fetchTasks = async () => {
  loading.value = true
  try {
    const res = await getTeamTasks(props.teamId)
    tasks.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editTaskId.value = null
  taskForm.value = {
    title: '',
    description: '',
    assigneeId: null,
    priority: 'MEDIUM',
    dueDate: null
  }
  dialogVisible.value = true
}

const showEditDialog = (task) => {
  isEdit.value = true
  editTaskId.value = task.id
  taskForm.value = {
    title: task.title,
    description: task.description || '',
    assigneeId: task.assigneeId,
    priority: task.priority,
    dueDate: task.dueDate
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await taskFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 转换日期格式
    const formData = {
      ...taskForm.value,
      dueDate: taskForm.value.dueDate ? dayjs(taskForm.value.dueDate).format('YYYY-MM-DD HH:mm:ss') : null
    }
    if (isEdit.value) {
      await updateTask(editTaskId.value, formData)
      ElMessage.success('任务更新成功')
    } else {
      await createTask(props.teamId, formData)
      ElMessage.success('任务创建成功')
    }
    dialogVisible.value = false
    fetchTasks()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleStatusChange = async (task, completed) => {
  const newStatus = completed ? 'COMPLETED' : 'PENDING'
  try {
    await updateTaskStatus(task.id, newStatus)
    task.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (taskId) => {
  try {
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', { type: 'warning' })
    await deleteTask(taskId)
    ElMessage.success('任务已删除')
    fetchTasks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const getPriorityType = (priority) => {
  const map = { LOW: 'info', MEDIUM: 'warning', HIGH: 'danger' }
  return map[priority] || 'info'
}

const getPriorityText = (priority) => {
  const map = { LOW: '低', MEDIUM: '中', HIGH: '高' }
  return map[priority] || priority
}

const getStatusType = (status) => {
  const map = { PENDING: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待处理', IN_PROGRESS: '进行中', COMPLETED: '已完成' }
  return map[status] || status
}

const formatDate = (date) => {
  return date ? dayjs(date).format('MM-DD HH:mm') : ''
}

watch(() => props.teamId, () => {
  if (props.teamId) fetchTasks()
}, { immediate: true })
</script>

<style scoped>
.team-tasks {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.tasks-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
}

.task-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.task-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.task-left {
  display: flex;
  gap: 12px;
  flex: 1;
}

.task-info {
  flex: 1;
}

.task-title {
  font-size: 15px;
  font-weight: 500;
  margin: 0 0 4px;
}

.task-title.completed {
  text-decoration: line-through;
  color: #999;
}

.task-desc {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.task-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.assignee, .due-date {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
}

.task-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
  text-align: right;
}
</style>
