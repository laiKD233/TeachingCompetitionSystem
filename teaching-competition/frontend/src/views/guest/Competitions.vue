<template>
  <div class="competitions-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">竞赛列表</h1>
      <p class="page-subtitle">查看当前可报名的竞赛</p>
    </div>
    
    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索竞赛名称..."
        class="search-input"
        clearable
        prefix-icon="Search"
      />
      
      <el-select
        v-model="filterStatus"
        placeholder="竞赛状态"
        class="filter-select"
        clearable
      >
        <el-option label="全部状态" value="" />
        <el-option label="报名中" value="REGISTRATION" />
        <el-option label="进行中" value="ONGOING" />
        <el-option label="已评审" value="REVIEWED" />
        <el-option label="公示中" value="ANNOUNCED" />
        <el-option label="已结束" value="ENDED" />
      </el-select>
      
      <el-select
        v-model="filterType"
        placeholder="竞赛类型"
        class="filter-select"
        clearable
      >
        <el-option label="全部类型" value="" />
        <el-option label="程序设计" value="程序设计" />
        <el-option label="数学建模" value="数学建模" />
        <el-option label="创新创业" value="创新创业" />
      </el-select>
    </div>

    <!-- 竞赛卡片列表 -->
    <div v-loading="loading" class="cards-grid" v-if="competitions.length">
      <div
        v-for="item in competitions"
        :key="item.id"
        class="competition-card"
        @click="viewDetail(item.id)"
      >
        <div class="card-cover">
          <img
            :src="item.coverImage || defaultCover"
            :alt="item.name"
            class="cover-img"
            @error="handleImgError($event)"
          />
          <span class="status-badge" :class="'status-' + item.status">
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="card-body">
          <h3 class="card-title">{{ item.name }}</h3>
          <p class="card-desc">{{ item.description || '暂无描述' }}</p>
          <div class="card-meta">
            <span class="meta-item">
              <el-icon><Collection /></el-icon>
              {{ item.type || '未分类' }}
            </span>
            <span class="meta-item" v-if="item.registrationEnd">
              <el-icon><Clock /></el-icon>
              截止 {{ formatDate(item.registrationEnd) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && competitions.length === 0" description="暂无竞赛数据" />

    <!-- 分页 -->
    <div class="pagination-section" v-if="competitions.length">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[9, 18, 27, 36]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Collection, Clock } from '@element-plus/icons-vue'
import { getPublicCompetitions } from '@/api/competition'

const router = useRouter()
const loading = ref(false)
const competitions = ref([])
const searchKeyword = ref('')
const filterStatus = ref('')
const filterType = ref('')
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

const defaultCover = `data:image/svg+xml,${encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="400" height="200" fill="none"><rect width="400" height="200" rx="12" fill="#e8eaf6"/><text x="200" y="95" text-anchor="middle" font-family="sans-serif" font-size="40" fill="#9fa8da">&#127942;</text><text x="200" y="130" text-anchor="middle" font-family="sans-serif" font-size="14" fill="#7986cb">&#26242;&#26080;&#23553;&#38754;</text></svg>')}`

const handleImgError = (e) => {
  e.target.src = defaultCover
}

onMounted(() => {
  fetchCompetitions()
})

const fetchCompetitions = async () => {
  loading.value = true
  try {
    const res = await getPublicCompetitions({
      keyword: searchKeyword.value,
      status: filterStatus.value,
      type: filterType.value,
      page: currentPage.value,
      size: pageSize.value
    })
    competitions.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取竞赛列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const textMap = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'REGISTRATION': '报名中',
    'ONGOING': '进行中',
    'REVIEWED': '已评审',
    'ANNOUNCED': '公示中',
    'ANNOUNCEMENT': '公示中',
    'ENDED': '已结束'
  }
  return textMap[status] || status
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const viewDetail = (id) => {
  router.push(`/guest/competition/${id}`)
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchCompetitions()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchCompetitions()
}

watch([searchKeyword, filterStatus, filterType], () => {
  currentPage.value = 1
  fetchCompetitions()
})
</script>

<style scoped>
.competitions-page {
  padding: 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
}

.filter-select {
  width: 160px;
}

/* 卡片网格 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.competition-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.competition-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 封面 */
.card-cover {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f0f2f5;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.competition-card:hover .cover-img {
  transform: scale(1.05);
}

.status-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  backdrop-filter: blur(4px);
}

.status-badge.status-REGISTRATION { background: rgba(103, 194, 58, 0.9); }
.status-badge.status-PUBLISHED { background: rgba(64, 158, 255, 0.9); }
.status-badge.status-ONGOING { background: rgba(230, 162, 60, 0.9); }
.status-badge.status-REVIEWED { background: rgba(144, 147, 153, 0.9); }
.status-badge.status-ANNOUNCED,
.status-badge.status-ANNOUNCEMENT { background: rgba(64, 158, 255, 0.9); }
.status-badge.status-ENDED { background: rgba(144, 147, 153, 0.9); }
.status-badge.status-DRAFT { background: rgba(144, 147, 153, 0.7); }

/* 内容 */
.card-body {
  padding: 16px 20px 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item .el-icon {
  font-size: 14px;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 30px 0 10px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .cards-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-title { font-size: 24px; }
  .filter-section { flex-direction: column; }
  .search-input, .filter-select { width: 100%; max-width: none; }
  .cards-grid { grid-template-columns: 1fr; gap: 16px; }
  .card-cover { height: 160px; }
}
</style>
