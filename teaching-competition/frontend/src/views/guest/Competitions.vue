<template>
  <div class="competitions-page">
    <!-- 页面标题 -->
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
        <div class="deco-circle deco-circle-3"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">竞赛列表</h1>
          <p class="page-subtitle">探索当前可报名的竞赛，展示你的才华</p>
        </div>
      </div>
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
  position: relative;
}

.page-header-wrapper {
  position: relative;
  padding: 48px 32px 36px;
  margin-bottom: 32px;
  border-radius: var(--radius-2xl);
  background: linear-gradient(135deg, var(--slate-800) 0%, var(--primary-700) 50%, var(--slate-800) 100%);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.header-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  filter: blur(1px);
}

.deco-circle-1 {
  width: 240px;
  height: 240px;
  background: var(--secondary-500);
  top: -80px;
  right: -60px;
}

.deco-circle-2 {
  width: 160px;
  height: 160px;
  background: var(--success-500);
  bottom: -40px;
  left: 8%;
}

.deco-circle-3 {
  width: 100px;
  height: 100px;
  background: var(--primary-500);
  top: 25%;
  right: 25%;
  opacity: 0.1;
}

.page-header {
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: var(--text-white);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
  font-weight: 500;
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 36px;
  flex-wrap: wrap;
  background: var(--bg-secondary);
  padding: 20px;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.search-input {
  flex: 1;
  min-width: 300px;
  max-width: 500px;
}

.filter-select {
  width: 180px;
}

/* 卡片网格 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.competition-card {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  border: 1px solid var(--border-light);
}

.competition-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: var(--shadow-2xl);
  border-color: var(--primary-300);
}

.competition-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-500), var(--secondary-500));
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.competition-card:hover::before {
  opacity: 1;
}

/* 封面 */
.card-cover {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: var(--bg-secondary);
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.competition-card:hover .cover-img {
  transform: scale(1.08);
}

.status-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  color: var(--text-white);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: var(--shadow-sm);
  z-index: 1;
}

.status-badge.status-REGISTRATION { background: var(--success-500); }
.status-badge.status-PUBLISHED { background: var(--primary-500); }
.status-badge.status-ONGOING { background: var(--warning-500); }
.status-badge.status-REVIEWED { background: var(--slate-500); }
.status-badge.status-ANNOUNCED,
.status-badge.status-ANNOUNCEMENT { background: var(--secondary-500); }
.status-badge.status-ENDED { background: var(--slate-600); }
.status-badge.status-DRAFT { background: var(--slate-500); }

/* 内容 */
.card-body {
  padding: 24px;
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color var(--transition-fast);
}

.competition-card:hover .card-title {
  color: var(--primary-600);
}

.card-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: 500;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 13px;
  color: var(--text-tertiary);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item .el-icon {
  font-size: 16px;
  transition: transform var(--transition-fast);
}

.meta-item:hover .el-icon {
  transform: scale(1.2);
  color: var(--primary-500);
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 40px 0 20px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .cards-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-title { font-size: 28px; }
  .filter-section { flex-direction: column; padding: 16px; }
  .search-input, .filter-select { width: 100%; max-width: none; }
  .cards-grid { grid-template-columns: 1fr; gap: 20px; }
  .card-cover { height: 180px; }
  .page-header-wrapper { padding: 32px 24px; }
}
</style>
