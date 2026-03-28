<template>
  <div class="users-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>添加用户</span>
      </el-button>
    </div>
    
    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、姓名或学号"
            prefix-icon="Search"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        
        <el-form-item label="角色">
          <el-select
            v-model="searchForm.role"
            placeholder="选择角色"
            clearable
            style="width: 160px"
          >
            <el-option label="全部角色" value="" />
            <el-option label="超级管理员" value="ADMIN" />
            <el-option label="竞赛管理员" value="TEACHER" />
            <el-option label="参赛人员" value="STUDENT" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            style="width: 160px"
          >
            <el-option label="全部状态" value="" />
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        
        <el-table-column prop="name" label="姓名" width="100" />
        
        <el-table-column prop="studentId" label="学号" width="120">
          <template #default="scope">
            <span>{{ scope.row.studentId || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="college" label="学院" width="150">
          <template #default="scope">
            <span>{{ scope.row.college || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)" effect="plain">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" effect="plain">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            
            <el-button
              v-if="scope.row.status === 1"
              type="warning"
              link
              size="small"
              @click="handleFreeze(scope.row)"
            >
              禁用
            </el-button>
            
            <el-button
              v-else
              type="success"
              link
              size="small"
              @click="handleUnfreeze(scope.row)"
            >
              启用
            </el-button>
            
            <el-button
              type="info"
              link
              size="small"
              @click="handleResetPassword(scope.row)"
            >
              重置密码
            </el-button>
            
            <el-button
              v-if="scope.row.id !== userStore.user?.id"
              type="danger"
              link
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" />
        </el-form-item>
        
        <el-form-item label="学院" prop="college">
          <el-input v-model="form.college" placeholder="请输入学院" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="超级管理员" value="ADMIN" />
            <el-option label="竞赛管理员" value="TEACHER" />
            <el-option label="参赛人员" value="STUDENT" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserList, createUser, updateUser, deleteUser,
  freezeUser, unfreezeUser, resetUserPassword
} from '@/api/super-admin'

const userStore = useUserStore()

const loading = ref(false)
const users = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  role: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  studentId: '',
  college: '',
  phone: '',
  email: '',
  role: 'STUDENT',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(() => {
  fetchUsers()
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.role) params.role = searchForm.role
    if (searchForm.status !== '' && searchForm.status !== undefined) params.status = searchForm.status

    const res = await getUserList(params)
    users.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getRoleType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return typeMap[role] || 'info'
}

const getRoleText = (role) => {
  const textMap = {
    'ADMIN': '超级管理员',
    'TEACHER': '竞赛管理员',
    'STUDENT': '参赛人员'
  }
  return textMap[role] || role
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    role: '',
    status: ''
  })
  pagination.page = 1
  fetchUsers()
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username,
    name: row.name,
    studentId: row.studentId,
    college: row.college,
    phone: row.phone,
    email: row.email,
    role: row.role,
    status: row.status
  })
}

const handleFreeze = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要禁用用户"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await freezeUser(row.id)
    ElMessage.success('禁用成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用失败')
      console.error(error)
    }
  }
}

const handleUnfreeze = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要启用用户"${row.name}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unfreezeUser(row.id)
    ElMessage.success('启用成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用失败')
      console.error(error)
    }
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户"${row.name}"的密码为"123456"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await resetUserPassword(row.id)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置失败')
      console.error(error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.name}"吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          username: form.username,
          password: form.password,
          name: form.name,
          studentId: form.studentId,
          college: form.college,
          phone: form.phone,
          email: form.email,
          role: form.role,
          status: form.status
        }
        if (form.id) {
          await updateUser(form.id, data)
        } else {
          await createUser(data)
        }
        ElMessage.success(form.id ? '更新成功' : '添加成功')
        dialogVisible.value = false
        fetchUsers()
      } catch (error) {
        ElMessage.error('操作失败')
        console.error(error)
      }
    }
  })
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    name: '',
    studentId: '',
    college: '',
    phone: '',
    email: '',
    role: 'STUDENT',
    status: 1
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchUsers()
}
</script>

<style scoped>
.users-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 表格卡片 */
.table-card {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.table-card :deep(.el-table) {
  border-radius: var(--radius-lg);
}

.table-card :deep(.el-table th) {
  background-color: var(--bg-light);
  font-weight: 600;
  color: var(--text-primary);
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid var(--border-light);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .filter-form {
    flex-direction: column;
  }
  
  .filter-form :deep(.el-form-item) {
    width: 100%;
  }
  
  .filter-form :deep(.el-input),
  .filter-form :deep(.el-select) {
    width: 100% !important;
  }
}
</style>
