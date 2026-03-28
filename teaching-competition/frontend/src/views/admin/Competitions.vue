<template>
  <div class="competitions-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">竞赛管理</h1>
      <el-button type="primary" @click="handleCreate">
        <el-icon><Plus /></el-icon>
        <span>创建竞赛</span>
      </el-button>
    </div>
    
    <!-- 竞赛列表 -->
    <el-card class="table-card">
      <el-table :data="competitions" v-loading="loading" stripe>
        <el-table-column prop="name" label="竞赛名称" width="200" />
        
        <el-table-column prop="type" label="类型" width="120" />
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="registrationEnd" label="报名截止" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.registrationEnd) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="submissionDeadline" label="作品截止" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.submissionDeadline) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">
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
    
    <!-- 创建/编辑竞赛对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="竞赛名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入竞赛名称" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="竞赛类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型">
                <el-option label="程序设计" value="程序设计" />
                <el-option label="数学建模" value="数学建模" />
                <el-option label="创新创业" value="创新创业" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="竞赛主题" prop="theme">
          <el-input v-model="form.theme" placeholder="请输入竞赛主题" />
        </el-form-item>
        
        <el-form-item label="竞赛描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入竞赛描述"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报名开始" prop="registrationStart">
              <el-date-picker
                v-model="form.registrationStart"
                type="datetime"
                placeholder="选择报名开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="报名截止" prop="registrationEnd">
              <el-date-picker
                v-model="form.registrationEnd"
                type="datetime"
                placeholder="选择报名截止时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作品截止" prop="submissionDeadline">
              <el-date-picker
                v-model="form.submissionDeadline"
                type="datetime"
                placeholder="选择作品提交截止时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="评审开始" prop="reviewStart">
              <el-date-picker
                v-model="form.reviewStart"
                type="datetime"
                placeholder="选择评审开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评审结束" prop="reviewEnd">
              <el-date-picker
                v-model="form.reviewEnd"
                type="datetime"
                placeholder="选择评审结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="初始状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option label="草稿" value="DRAFT" />
                <el-option label="已发布" value="PUBLISHED" />
                <el-option label="报名中" value="REGISTRATION" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="竞赛规则" prop="rules">
          <el-input
            v-model="form.rules"
            type="textarea"
            :rows="3"
            placeholder="请输入竞赛规则"
          />
        </el-form-item>
        
        <el-form-item label="提交要求" prop="submissionRequirements">
          <el-input
            v-model="form.submissionRequirements"
            type="textarea"
            :rows="3"
            placeholder="请输入作品提交要求"
          />
        </el-form-item>
        
        <el-form-item label="封面图片">
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
          >
            <el-button size="small">选择图片</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传 jpg/png 文件，且不超过 2MB</div>
            </template>
          </el-upload>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCompetitions, createCompetition, updateCompetition, deleteCompetition } from '@/api/competition'
import { uploadFile } from '@/api/file'

const router = useRouter()

const loading = ref(false)
const competitions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('创建竞赛')
const formRef = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  type: '',
  theme: '',
  description: '',
  registrationStart: '',
  registrationEnd: '',
  submissionDeadline: '',
  reviewStart: '',
  reviewEnd: '',
  status: 'DRAFT',
  rulesText: '',
  submissionRequirements: '',
  coverImage: ''
})

const rules = {
  name: [{ required: true, message: '请输入竞赛名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择竞赛类型', trigger: 'change' }]
}

onMounted(() => {
  fetchCompetitions()
})

const fetchCompetitions = async () => {
  loading.value = true
  try {
    const res = await getAdminCompetitions({
      page: pagination.page,
      size: pagination.size
    })
    competitions.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取竞赛列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'PUBLISHED': 'primary',
    'REGISTRATION': 'success',
    'ONGOING': 'warning',
    'REVIEWED': 'info',
    'ANNOUNCED': 'primary',
    'ENDED': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'REGISTRATION': '报名中',
    'ONGOING': '进行中',
    'REVIEWED': '已评审',
    'ANNOUNCED': '已公布',
    'ENDED': '已结束'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleCreate = () => {
  dialogTitle.value = '创建竞赛'
  dialogVisible.value = true
  resetForm()
}

const handleView = (row) => {
  router.push(`/guest/competition/${row.id}`)
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑竞赛'
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    type: row.type,
    theme: row.theme || '',
    description: row.description || '',
    registrationStart: row.registrationStart || '',
    registrationEnd: row.registrationEnd || '',
    submissionDeadline: row.submissionDeadline || '',
    reviewStart: row.reviewStart || '',
    reviewEnd: row.reviewEnd || '',
    status: row.status || 'DRAFT',
    rulesText: row.rules || '',
    submissionRequirements: row.submissionRequirements || '',
    coverImage: row.coverImage || ''
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除竞赛"${row.name}"吗？此操作不可恢复！`, '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCompetition(row.id)
    ElMessage.success('删除成功')
    fetchCompetitions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

const formatDateTime = (date) => {
  if (!date) return null
  const d = new Date(date)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const buildSubmitData = (coverUrl) => {
  return {
    name: form.name,
    type: form.type,
    theme: form.theme,
    description: form.description,
    coverImage: coverUrl || null,
    status: form.status,
    rules: form.rulesText,
    submissionRequirements: form.submissionRequirements,
    registrationStart: formatDateTime(form.registrationStart),
    registrationEnd: formatDateTime(form.registrationEnd),
    submissionDeadline: formatDateTime(form.submissionDeadline),
    reviewStart: formatDateTime(form.reviewStart),
    reviewEnd: formatDateTime(form.reviewEnd)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let coverUrl = form.coverImage
        // 如果是 base64 格式的图片，先上传
        if (coverUrl && coverUrl.startsWith('data:')) {
          const blob = await fetch(coverUrl).then(r => r.blob())
          const file = new File([blob], 'cover.jpg', { type: 'image/jpeg' })
          const uploadRes = await uploadFile(file)
          coverUrl = uploadRes.data
        }

        const submitData = buildSubmitData(coverUrl)

        if (form.id) {
          await updateCompetition(form.id, submitData)
        } else {
          await createCompetition(submitData)
        }
        ElMessage.success(form.id ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchCompetitions()
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


const handleFileChange = (file) => {
  if (file && file.raw) {
    const isJPG = file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
    const isLt2M = file.raw.size / 1024 / 1024 < 2
    
    if (!isJPG) {
      ElMessage.error('只能上传 JPG/PNG 格式的图片!')
      return
    }
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB!')
      return
    }
    
    // 预览图片
    const reader = new FileReader()
    reader.onload = (e) => {
      form.coverImage = e.target.result
    }
    reader.readAsDataURL(file.raw)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    type: '',
    theme: '',
    description: '',
    registrationStart: '',
    registrationEnd: '',
    submissionDeadline: '',
    reviewStart: '',
    reviewEnd: '',
    status: 'DRAFT',
    rulesText: '',
    submissionRequirements: '',
    coverImage: ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const handleUploadSuccess = (response, file) => {
  form.coverImage = response.url
  ElMessage.success('图片上传成功')
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchCompetitions()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchCompetitions()
}
</script>

<style scoped>
.competitions-page {
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

/* 上传组件 */
.upload-demo {
  width: 100%;
}

.el-upload__tip {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 8px;
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
  
  .table-card :deep(.el-table) {
    font-size: 12px;
  }
}
</style>
