<template>
  <div class="todo-page">
    <div class="page-header">
      <h1>个人待办</h1>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        添加待办
      </el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="fetchTodos">
      <el-tab-pane label="全部" name=""></el-tab-pane>
      <el-tab-pane label="待完成" name="PENDING"></el-tab-pane>
      <el-tab-pane label="已完成" name="COMPLETED"></el-tab-pane>
    </el-tabs>

    <div class="todo-list" v-loading="loading">
      <el-empty v-if="todos.length === 0" description="暂无待办事项" />

      <el-table :data="todos" style="width: 100%">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox
              :model-value="row.status === 'COMPLETED'"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="任务名称" min-width="200">
          <template #default="{ row }">
            <span :class="{ 'completed-text': row.status === 'COMPLETED' }">
              {{ row.title }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="dueDate" label="截止日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">
              {{ row.status === 'COMPLETED' ? '已完成' : '待完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button text type="success" v-if="row.status === 'PENDING'" @click="handleComplete(row)">完成</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑待办' : '添加待办'" width="500px">
      <el-form :model="todoForm" :rules="todoRules" ref="todoFormRef" label-width="80px">
        <el-form-item label="任务名称" prop="title">
          <el-input v-model="todoForm.title" placeholder="请输入任务名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="todoForm.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="todoForm.dueDate"
            type="datetime"
            placeholder="选择截止日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="todoForm.priority">
            <el-radio label="LOW">低</el-radio>
            <el-radio label="MEDIUM">中</el-radio>
            <el-radio label="HIGH">高</el-radio>
          </el-radio-group>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyTodos, createTodo, updateTodo, deleteTodo, updateTodoStatus } from '@/api/todo'
import dayjs from 'dayjs'

const loading = ref(false)
const submitting = ref(false)
const todos = ref([])
const activeTab = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const editTodoId = ref(null)
const todoFormRef = ref(null)
const todoForm = ref({
  title: '',
  description: '',
  dueDate: null,
  priority: 'MEDIUM'
})
const todoRules = {
  title: [{ required: true, message: '请输入任务名称', trigger: 'blur' }]
}

const fetchTodos = async () => {
  loading.value = true
  try {
    const res = await getMyTodos(activeTab.value)
    todos.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  editTodoId.value = null
  todoForm.value = { title: '', description: '', dueDate: null, priority: 'MEDIUM' }
  dialogVisible.value = true
}

const showEditDialog = (todo) => {
  isEdit.value = true
  editTodoId.value = todo.id
  todoForm.value = {
    title: todo.title,
    description: todo.description || '',
    dueDate: todo.dueDate,
    priority: todo.priority
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await todoFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // 转换日期格式
    const formData = {
      ...todoForm.value,
      dueDate: todoForm.value.dueDate ? dayjs(todoForm.value.dueDate).format('YYYY-MM-DD HH:mm:ss') : null
    }
    if (isEdit.value) {
      await updateTodo(editTodoId.value, formData)
      ElMessage.success('待办更新成功')
    } else {
      await createTodo(formData)
      ElMessage.success('待办创建成功')
    }
    dialogVisible.value = false
    fetchTodos()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleStatusChange = async (todo, completed) => {
  const newStatus = completed ? 'COMPLETED' : 'PENDING'
  try {
    await updateTodoStatus(todo.id, newStatus)
    todo.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error(error)
  }
}

const handleComplete = async (todo) => {
  try {
    await updateTodoStatus(todo.id, 'COMPLETED')
    ElMessage.success('任务已完成')
    fetchTodos()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该待办吗？', '提示', { type: 'warning' })
    await deleteTodo(id)
    ElMessage.success('待办已删除')
    fetchTodos()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

onMounted(() => {
  fetchTodos()
})
</script>

<style scoped>
.todo-page {
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

.todo-list {
  margin-top: 20px;
}

.completed-text {
  text-decoration: line-through;
  color: #999;
}
</style>
