<template>
  <div class="registrations-page">
    <!-- 页面标题 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">报名审核</h1>
          <p class="page-subtitle">审核参赛者的报名申请</p>
        </div>
      </div>
    </div>

    <!-- 筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="竞赛">
          <el-select
            v-model="searchForm.competitionId"
            placeholder="选择竞赛"
            clearable
            style="width: 240px"
          >
            <el-option
              v-for="item in competitionOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>

        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索姓名或学号"
            prefix-icon="Search"
            clearable
            style="width: 200px"
          />
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

    <!-- 报名列表 -->
    <el-card class="table-card">
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column prop="participantName" label="参赛者" width="100">
          <template #default="scope">
            <span>{{ scope.row.participantName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="studentId" label="学号" width="120">
          <template #default="scope">
            <span>{{ scope.row.studentId || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="college" label="学院" width="130">
          <template #default="scope">
            <span>{{ scope.row.college || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="competitionName" label="竞赛名称" min-width="150">
          <template #default="scope">
            <span>{{ scope.row.competitionName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="projectName" label="项目名称" min-width="130">
          <template #default="scope">
            <span>{{ scope.row.projectName || '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="plain">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="报名时间" width="170">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <template v-if="scope.row.status === 'PENDING'">
              <el-button type="success" link size="small" @click="handleApprove(scope.row)">
                通过
              </el-button>
              <el-button type="danger" link size="small" @click="handleReject(scope.row)">
                驳回
              </el-button>
            </template>
            <el-tooltip v-else-if="scope.row.status === 'REJECTED' && scope.row.rejectReason" :content="scope.row.rejectReason" placement="top">
              <span class="text-muted">已驳回（查看原因）</span>
            </el-tooltip>
            <span v-else class="text-muted">已处理</span>
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

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回报名" width="500px" @close="rejectReason = ''">
      <el-form label-width="80px">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectReason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRegistrationList, approveRegistration, rejectRegistration } from '@/api/registration'
import { getAdminCompetitions } from '@/api/competition'

const loading = ref(false)
const registrations = ref([])
const competitionOptions = ref([])
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRejectId = ref(null)

const searchForm = reactive({
  competitionId: null,
  status: '',
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  fetchCompetitions()
  fetchRegistrations()
})

const fetchCompetitions = async () => {
  try {
    const res = await getAdminCompetitions({ page: 1, size: 100 })
    competitionOptions.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const fetchRegistrations = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (searchForm.competitionId) params.competitionId = searchForm.competitionId
    if (searchForm.status) params.status = searchForm.status
    if (searchForm.keyword) params.keyword = searchForm.keyword

    const res = await getRegistrationList(params)
    registrations.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取报名列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchRegistrations()
}

const handleReset = () => {
  searchForm.competitionId = null
  searchForm.status = ''
  searchForm.keyword = ''
  pagination.page = 1
  fetchRegistrations()
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定通过"${row.participantName}"的报名申请吗？`, '确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveRegistration(row.id)
    ElMessage.success('已通过')
    fetchRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error(error)
    }
  }
}

const handleReject = (row) => {
  currentRejectId.value = row.id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await rejectRegistration(currentRejectId.value, rejectReason.value)
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    fetchRegistrations()
  } catch (error) {
    ElMessage.error('操作失败')
    console.error(error)
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已驳回'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchRegistrations()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchRegistrations()
}
</script>

<style scoped>
.registrations-page {
  padding: 0;
}

.page-header-wrapper {
  position: relative;
  padding: 32px 32px 24px;
  margin-bottom: 24px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #1e293b 100%);
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.deco-circle-1 {
  width: 160px;
  height: 160px;
  background: var(--primary-color);
  top: -50px;
  right: -30px;
}

.deco-circle-2 {
  width: 100px;
  height: 100px;
  background: #60A5FA;
  bottom: -20px;
  left: 15%;
}

.page-header {
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 4px;
}

.page-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

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

.text-muted {
  font-size: 13px;
  color: var(--text-placeholder);
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 20px;
  border-top: 1px solid var(--border-light);
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

  .filter-form {
    flex-direction: column;
  }
}
</style>
