<template>
  <div class="works-page">
    <!-- 页面头部 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">作品管理</h1>
          <p class="page-subtitle">管理您提交的所有竞赛作品</p>
        </div>
        <el-button type="primary" size="large" class="upload-btn" @click="handleOpenUpload">
          <el-icon style="margin-right: 6px"><Upload /></el-icon>
          上传作品
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="works-stats">
      <div class="stat-chip">
        <el-icon><Document /></el-icon>
        <span class="stat-chip-value">{{ works.length }}</span>
        <span class="stat-chip-label">全部作品</span>
      </div>
      <div class="stat-chip chip-pending">
        <el-icon><Clock /></el-icon>
        <span class="stat-chip-value">{{ works.filter(w => w.status === 'PENDING').length }}</span>
        <span class="stat-chip-label">待审核</span>
      </div>
      <div class="stat-chip chip-approved">
        <el-icon><CircleCheck /></el-icon>
        <span class="stat-chip-value">{{ works.filter(w => w.status === 'APPROVED').length }}</span>
        <span class="stat-chip-label">已通过</span>
      </div>
    </div>

    <!-- 作品列表 - 卡片视图 -->
    <div class="works-grid" v-loading="loading">
      <transition-group name="card-list" appear>
        <div
          v-for="(item, index) in works"
          :key="item.id"
          class="work-card"
          :style="{ animationDelay: `${index * 0.06}s` }"
        >
          <!-- 卡片顶部状态条 -->
          <div class="card-accent" :class="`accent-${item.status?.toLowerCase() || 'default'}`"></div>

          <div class="card-body">
            <!-- 作品信息 -->
            <div class="card-main">
              <div class="work-icon" :class="`icon-${item.status?.toLowerCase() || 'default'}`">
                <el-icon :size="22">
                  <DocumentChecked v-if="item.status === 'APPROVED'" />
                  <Clock v-else-if="item.status === 'PENDING'" />
                  <CircleClose v-else-if="item.status === 'REJECTED'" />
                  <Document v-else />
                </el-icon>
              </div>
              <div class="work-info">
                <h3 class="work-title">{{ item.title || '未命名作品' }}</h3>
                <p class="work-competition">
                  <el-icon style="vertical-align: -2px; margin-right: 4px"><Trophy /></el-icon>
                  {{ item.competitionName || '未知竞赛' }}
                </p>
              </div>
            </div>

            <!-- 状态标签 -->
            <div class="card-status">
              <span class="status-pill" :class="`pill-${item.status?.toLowerCase() || 'default'}`">
                <span class="pill-dot"></span>
                {{ getStatusText(item.status) }}
              </span>
            </div>

            <!-- 时间 -->
            <div class="card-time">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(item.createdAt) }}</span>
            </div>

            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button
                v-if="item.fileUrl"
                class="action-btn-download"
                size="small"
                @click="handleDownload(item)"
              >
                <el-icon style="margin-right: 4px"><Download /></el-icon>
                下载
              </el-button>
              <el-button
                class="action-btn-edit"
                size="small"
                @click="handleEdit(item)"
              >
                <el-icon style="margin-right: 4px"><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                class="action-btn-reupload"
                size="small"
                @click="handleReupload(item)"
              >
                <el-icon style="margin-right: 4px"><RefreshRight /></el-icon>
                重传
              </el-button>
              <el-button
                class="action-btn-delete"
                size="small"
                @click="handleDelete(item)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </transition-group>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && works.length === 0" class="empty-state">
      <div class="empty-illustration">
        <div class="empty-icon-ring">
          <el-icon :size="48"><FolderOpened /></el-icon>
        </div>
      </div>
      <h3 class="empty-title">还没有提交作品</h3>
      <p class="empty-desc">点击上方按钮上传您的第一个竞赛作品</p>
      <el-button type="primary" class="empty-action" @click="handleOpenUpload">
        <el-icon style="margin-right: 6px"><Upload /></el-icon>
        立即上传
      </el-button>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="pagination.total > pagination.size">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        background
      />
    </div>

    <!-- 上传作品对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      :title="dialogTitle"
      width="520px"
      @close="resetUploadForm"
      class="upload-dialog"
      :close-on-click-modal="false"
    >
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-position="top">
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
          <el-input v-model="uploadForm.title" placeholder="请输入作品名称" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="作品描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入作品描述（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="作品文件" prop="file">
          <div class="upload-zone" :class="{ 'is-active': selectedFile }">
            <el-upload
              ref="uploadRef"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              drag
              class="inner-upload"
            >
              <div class="upload-placeholder">
                <el-icon :size="36" class="upload-icon"><UploadFilled /></el-icon>
                <p class="upload-text">拖拽文件到此处或 <em>点击上传</em></p>
                <p class="upload-hint">请上传作品文件</p>
              </div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadDialogVisible = false" class="btn-cancel">取消</el-button>
          <el-button type="primary" :loading="uploading" @click="handleUpload" class="btn-submit">
            {{ uploading ? '提交中...' : '确认提交' }}
          </el-button>
        </div>
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
          if (!selectedFile.value) {
            ElMessage.warning('请选择要重新上传的文件')
            uploading.value = false
            return
          }
          const formData = new FormData()
          formData.append('file', selectedFile.value)
          formData.append('title', uploadForm.title)
          formData.append('description', uploadForm.description)
          formData.append('competitionId', uploadForm.competitionId)
          await updateWork(uploadForm.reuploadWorkId, formData)
          ElMessage.success('作品重新上传成功')
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
  if (row.fileUrl.startsWith('http://') || row.fileUrl.startsWith('https://')) {
    window.open(row.fileUrl, '_blank')
  } else {
    ElMessage.warning('该文件为旧格式，暂不支持下载，请重新上传作品')
  }
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

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回',
    'DELETED': '已删除',
    'REUPLOADED': '已重新上传',
    'SUBMITTED': '已提交'
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
  max-width: 960px;
  margin: 0 auto;
}

/* ============================
   页面头部 - 渐变装饰
   ============================ */
.page-header-wrapper {
  position: relative;
  padding: 36px 32px 28px;
  margin-bottom: 24px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #1e293b 100%);
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.deco-circle-1 {
  width: 200px;
  height: 200px;
  background: var(--primary-color);
  top: -60px;
  right: -40px;
}

.deco-circle-2 {
  width: 120px;
  height: 120px;
  background: #f093fb;
  bottom: -30px;
  left: 10%;
}

.deco-circle-3 {
  width: 80px;
  height: 80px;
  background: #00f2fe;
  top: 20%;
  right: 25%;
  opacity: 0.08;
}

.page-header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 6px 0;
  letter-spacing: -0.3px;
}

.page-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);
  margin: 0;
}

.upload-btn {
  flex-shrink: 0;
  padding: 10px 24px;
  border-radius: var(--radius-md);
  font-weight: 600;
  font-size: 14px;
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.55) !important;
}

/* ============================
   统计概览
   ============================ */
.works-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stat-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: var(--bg-white);
  border-radius: 40px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.stat-chip:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.stat-chip .el-icon {
  font-size: 16px;
  color: var(--primary-color);
}

.chip-pending .el-icon {
  color: var(--warning-color);
}

.chip-approved .el-icon {
  color: var(--success-color);
}

.stat-chip-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-chip-label {
  font-size: 13px;
  color: var(--text-secondary);
}

/* ============================
   作品卡片网格
   ============================ */
.works-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.work-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  animation: cardEnter 0.4s ease both;
}

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.work-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

/* 顶部状态条 */
.card-accent {
  height: 3px;
  transition: height 0.3s ease;
}

.work-card:hover .card-accent {
  height: 4px;
}

.accent-pending { background: linear-gradient(90deg, #f6d365 0%, #fda085 100%); }
.accent-approved { background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%); }
.accent-rejected { background: linear-gradient(90deg, #f5576c 0%, #ff6b6b 100%); }
.accent-deleted { background: linear-gradient(90deg, #a8b8c8 0%, #c8d6e5 100%); }
.accent-submitted { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); }
.accent-reuploaded { background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%); }
.accent-default { background: linear-gradient(90deg, #a8b8c8 0%, #bcc6d0 100%); }

.card-body {
  padding: 20px 24px;
}

.card-main {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 12px;
}

.work-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #fff;
  transition: transform 0.3s ease;
}

.work-card:hover .work-icon {
  transform: scale(1.08);
}

.icon-pending { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); }
.icon-approved { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.icon-rejected { background: linear-gradient(135deg, #f5576c 0%, #ff6b6b 100%); }
.icon-deleted { background: linear-gradient(135deg, #a8b8c8 0%, #c8d6e5 100%); }
.icon-submitted { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.icon-reuploaded { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.icon-default { background: linear-gradient(135deg, #a8b8c8 0%, #bcc6d0 100%); }

.work-info {
  flex: 1;
  min-width: 0;
}

.work-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-competition {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  display: flex;
  align-items: center;
}

.work-competition .el-icon {
  color: var(--warning-color);
  font-size: 14px;
}

/* 状态胶囊 */
.card-status {
  margin-bottom: 12px;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.pill-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.pill-pending {
  background: rgba(230, 162, 60, 0.12);
  color: #d48806;
}
.pill-pending .pill-dot { background: #e6a23c; }

.pill-approved {
  background: rgba(103, 194, 58, 0.12);
  color: #389e0d;
}
.pill-approved .pill-dot { background: #67c23a; }

.pill-rejected {
  background: rgba(245, 108, 108, 0.12);
  color: #cf1322;
}
.pill-rejected .pill-dot { background: #f56c6c; }

.pill-deleted {
  background: rgba(144, 147, 153, 0.1);
  color: #8c8c8c;
}
.pill-deleted .pill-dot { background: #909399; }

.pill-submitted {
  background: rgba(102, 126, 234, 0.1);
  color: #5b6abf;
}
.pill-submitted .pill-dot { background: #667eea; }

.pill-reuploaded {
  background: rgba(79, 172, 254, 0.1);
  color: #2b8fd9;
}
.pill-reuploaded .pill-dot { background: #4facfe; }

.pill-default {
  background: rgba(144, 147, 153, 0.1);
  color: #8c8c8c;
}
.pill-default .pill-dot { background: #909399; }

/* 时间 */
.card-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-placeholder);
  margin-bottom: 16px;
}

.card-time .el-icon {
  font-size: 14px;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 14px;
  border-top: 1px solid var(--border-light);
}

.card-actions .el-button {
  border-radius: var(--radius-sm) !important;
  font-size: 13px !important;
  font-weight: 500;
  padding: 6px 14px !important;
  border: none !important;
  transition: all 0.25s ease !important;
}

.action-btn-download {
  background: rgba(102, 126, 234, 0.08) !important;
  color: #5b6abf !important;
}
.action-btn-download:hover {
  background: rgba(102, 126, 234, 0.16) !important;
  transform: translateY(-1px);
}

.action-btn-edit {
  background: rgba(79, 172, 254, 0.08) !important;
  color: #2b8fd9 !important;
}
.action-btn-edit:hover {
  background: rgba(79, 172, 254, 0.16) !important;
  transform: translateY(-1px);
}

.action-btn-reupload {
  background: rgba(67, 233, 123, 0.08) !important;
  color: #389e0d !important;
}
.action-btn-reupload:hover {
  background: rgba(67, 233, 123, 0.16) !important;
  transform: translateY(-1px);
}

.action-btn-delete {
  background: rgba(245, 108, 108, 0.06) !important;
  color: #cf1322 !important;
  margin-left: auto;
}
.action-btn-delete:hover {
  background: rgba(245, 108, 108, 0.16) !important;
  transform: translateY(-1px);
}

/* ============================
   空状态
   ============================ */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px 40px;
  text-align: center;
}

.empty-illustration {
  margin-bottom: 20px;
}

.empty-icon-ring {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.08));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
  animation: emptyFloat 3s ease-in-out infinite;
}

@keyframes emptyFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 14px;
  color: var(--text-placeholder);
  margin: 0 0 24px 0;
}

.empty-action {
  padding: 10px 28px;
  border-radius: var(--radius-md);
  font-weight: 600;
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.empty-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.45) !important;
}

/* ============================
   分页
   ============================ */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px 0 8px;
}

/* ============================
   对话框样式
   ============================ */
.upload-dialog :deep(.el-dialog) {
  border-radius: var(--radius-xl);
  overflow: hidden;
}

.upload-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 0;
  margin-right: 0;
}

.upload-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.upload-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.upload-dialog :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--text-primary);
  padding-bottom: 6px;
}

/* 上传拖拽区域 */
.upload-zone {
  border: 2px dashed var(--border-color);
  border-radius: var(--radius-lg);
  transition: all 0.3s ease;
  overflow: hidden;
}

.upload-zone.is-active {
  border-color: var(--primary-color);
  background: rgba(102, 126, 234, 0.03);
}

.upload-zone:hover {
  border-color: var(--primary-color);
}

.inner-upload {
  width: 100%;
}

.inner-upload :deep(.el-upload-dragger) {
  border: none !important;
  background: transparent !important;
  border-radius: var(--radius-lg);
  padding: 20px;
  width: 100%;
  height: auto;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-icon {
  color: var(--primary-color);
  margin-bottom: 12px;
  opacity: 0.7;
}

.upload-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0 0 4px;
}

.upload-text em {
  color: var(--primary-color);
  font-style: normal;
  font-weight: 600;
}

.upload-hint {
  font-size: 12px;
  color: var(--text-placeholder);
  margin: 0;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  padding: 10px 24px;
  border-radius: var(--radius-md);
  font-weight: 500;
}

.btn-submit {
  padding: 10px 28px;
  border-radius: var(--radius-md);
  font-weight: 600;
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.btn-submit:hover {
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.45) !important;
  transform: translateY(-1px);
}

/* ============================
   列表动画
   ============================ */
.card-list-enter-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-list-leave-active {
  transition: all 0.3s ease;
}

.card-list-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.card-list-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* ============================
   响应式
   ============================ */
@media (max-width: 768px) {
  .page-header-wrapper {
    padding: 24px 20px 20px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-title {
    font-size: 22px;
  }

  .works-stats {
    gap: 8px;
  }

  .stat-chip {
    padding: 8px 14px;
    font-size: 12px;
  }

  .card-body {
    padding: 16px;
  }

  .card-actions {
    flex-wrap: wrap;
  }

  .action-btn-delete {
    margin-left: 0;
  }
}
</style>
