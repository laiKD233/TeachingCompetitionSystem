<template>
  <div class="works-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">作品管理</h1>
      <el-button type="primary" @click="handleOpenUpload">上传作品</el-button>
    </div>

    <!-- 作品列表 -->
    <el-card class="table-card">
      <el-table :data="works" v-loading="loading" stripe>
        <el-table-column prop="title" label="作品名称" min-width="180">
          <template #default="scope">
            <span>{{ scope.row.title || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="competitionName" label="所属竞赛" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.competitionName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="fileUrl" label="文件" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.fileUrl"
              type="primary"
              link
              size="small"
              @click="handleDownload(scope.row)"
            >
              下载文件
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              type="primary"
              link
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              @click="handleReupload(scope.row)"
            >
              重新上传
            </el-button>
            <el-button
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
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 上传作品对话框 -->
    <el-dialog v-model="uploadDialogVisible" :title="dialogTitle" width="500px" @close="resetUploadForm">
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="竞赛" prop="competitionId">
          <el-select v-model="uploadForm.competitionId" placeholder="请选择竞赛" style="width: 100%">
            <el-option
              v-for="item in competitionOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="作品名称" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入作品名称" />
        </el-form-item>

        <el-form-item label="作品描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入作品描述"
          />
        </el-form-item>

        <el-form-item label="作品文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button size="small" type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">请上传作品文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyWorks, uploadWork, updateWork, deleteWork } from '@/api/work'
import { getRegistrations } from '@/api/registration'

const loading = ref(false)
const uploading = ref(false)
const works = ref([])
const competitionOptions = ref([])
const uploadDialogVisible = ref(false)
const uploadFormRef = ref(null)
const uploadRef = ref(null)
const selectedFile = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const uploadForm = reactive({
  competitionId: '',
  title: '',
  description: '',
  isEdit: false,
  isReupload: false,
  reuploadWorkId: null
})

const uploadRules = {
  competitionId: [{ required: true, message: '请选择竞赛', trigger: 'change' }],
  title: [{ required: true, message: '请输入作品名称', trigger: 'blur' }]
}

onMounted(() => {
  fetchWorks()
  fetchCompetitionOptions()
})

const fetchWorks = async () => {
  loading.value = true
  try {
    const res = await getMyWorks({
      page: pagination.page,
      size: pagination.size
    })
    works.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取作品列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fetchCompetitionOptions = async () => {
  try {
    const res = await getRegistrations({
      status: 'APPROVED',
      page: 1,
      size: 100
    })
    // 从已通过的报名记录中提取竞赛列表（去重）
    const records = res.data.records || []
    const competitionMap = new Map()
    records.forEach(item => {
      if (item.competitionId && !competitionMap.has(item.competitionId)) {
        competitionMap.set(item.competitionId, {
          id: item.competitionId,
          name: item.competitionName
        })
      }
    })
    competitionOptions.value = Array.from(competitionMap.values())
  } catch (error) {
    console.error(error)
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleFileRemove = () => {
  selectedFile.value = null
}

const dialogTitle = computed(() => {
  if (uploadForm.isEdit) return '编辑作品'
  if (uploadForm.isReupload) return '重新上传作品'
  return '上传作品'
})

const handleOpenUpload = () => {
  uploadForm.isEdit = false
  uploadForm.isReupload = false
  uploadForm.reuploadWorkId = null
  uploadDialogVisible.value = true
}

const handleEdit = (row) => {
  uploadForm.isEdit = true
  uploadForm.isReupload = false
  uploadForm.reuploadWorkId = row.id
  uploadForm.competitionId = row.competitionId
  uploadForm.title = row.title
  uploadForm.description = row.description || ''
  uploadDialogVisible.value = true
}

const handleReupload = (row) => {
  uploadForm.isEdit = false
  uploadForm.isReupload = true
  uploadForm.reuploadWorkId = row.id
  uploadForm.competitionId = row.competitionId
  uploadForm.title = row.title
  uploadForm.description = row.description || ''
  uploadDialogVisible.value = true
}

const handleUpload = async () => {
  if (!uploadFormRef.value) return

  await uploadFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!uploadForm.isEdit && !selectedFile.value) {
        ElMessage.warning('请选择作品文件')
        return
      }

      uploading.value = true
      try {
        if (uploadForm.isEdit) {
          const formData = new FormData()
          formData.append('title', uploadForm.title)
          formData.append('description', uploadForm.description)
          formData.append('competitionId', uploadForm.competitionId)
          if (selectedFile.value) {
            formData.append('file', selectedFile.value)
          }
          await updateWork(uploadForm.reuploadWorkId, formData)
          ElMessage.success('作品更新成功')
        } else if (uploadForm.isReupload) {
          const formData = new FormData()
          formData.append('file', selectedFile.value)
          formData.append('title', uploadForm.title)
          formData.append('description', uploadForm.description)
          formData.append('competitionId', uploadForm.competitionId)
          await updateWork(uploadForm.reuploadWorkId, formData)
          ElMessage.success('作品更新成功')
        } else {
          const formData = new FormData()
          formData.append('file', selectedFile.value)
          formData.append('title', uploadForm.title)
          formData.append('description', uploadForm.description)
          formData.append('competitionId', uploadForm.competitionId)
          await uploadWork(formData)
          ElMessage.success('作品上传成功')
        }
        uploadDialogVisible.value = false
        fetchWorks()
      } catch (error) {
        ElMessage.error((uploadForm.isEdit || uploadForm.isReupload) ? '更新失败' : '上传失败')
        console.error(error)
      } finally {
        uploading.value = false
      }
    }
  })
}

const handleDownload = (row) => {
  if (!row.fileUrl) {
    ElMessage.warning('文件路径无效')
    return
  }
  window.open(row.fileUrl, '_blank')//直接打开新窗口来下载文件
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该作品吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteWork(row.id)
    ElMessage.success('删除成功')
    fetchWorks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error(error)
    }
  }
}

const resetUploadForm = () => {
  selectedFile.value = null
  uploadFormRef.value?.resetFields()
  Object.assign(uploadForm, {
    competitionId: '',
    title: '',
    description: '',
    isEdit: false,
    isReupload: false,
    reuploadWorkId: null
  })
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过'
  }
  return textMap[status] || status || '已提交'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchWorks()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchWorks()
}
</script>

<style scoped>
.works-page {
  padding: 0;
}

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

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid var(--border-light);
}

.el-upload__tip {
  font-size: 12px;
  color: var(--text-placeholder);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>
