<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '72px' : '260px'" class="layout-aside">
      <!-- Logo区域 -->
      <div class="sidebar-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <el-icon :size="26" color="#fff"><Trophy /></el-icon>
          </div>
          <transition name="fade-text">
            <div class="brand" v-show="!isCollapse">
              <h3 class="brand-name">教学竞赛</h3>
              <p class="brand-sub">管理系统</p>
            </div>
          </transition>
        </div>
      </div>

      <!-- 菜单区域 -->
      <div class="sidebar-body">
        <!-- 导航菜单 -->
        <el-menu
          :default-active="activeMenu"
          router
          class="sidebar-menu"
          :collapse="isCollapse"
        >
          <!-- 公共菜单 -->
          <div class="menu-section" v-if="userStore.user?.role !== 'GUEST'">
            <span class="menu-section-label" v-show="!isCollapse">主导航</span>
            <el-menu-item :index="userStore.user?.role === 'STUDENT' ? '/participant/dashboard' : '/admin/dashboard'">
              <el-icon><DataAnalysis /></el-icon>
              <template #title>
                <span>{{ userStore.user?.role === 'STUDENT' ? '我的首页' : '管理首页' }}</span>
              </template>
            </el-menu-item>
          </div>

          <!-- 参赛者菜单 -->
          <template v-if="userStore.user?.role === 'STUDENT'">
            <div class="menu-section">
              <span class="menu-section-label" v-show="!isCollapse">竞赛参与</span>
              <el-menu-item index="/participant/registrations">
                <el-icon><Document /></el-icon>
                <template #title><span>我的报名</span></template>
              </el-menu-item>
              <el-menu-item index="/participant/works">
                <el-icon><Upload /></el-icon>
                <template #title><span>作品管理</span></template>
              </el-menu-item>
              <el-menu-item index="/participant/scores">
                <el-icon><TrendCharts /></el-icon>
                <template #title><span>成绩查询</span></template>
              </el-menu-item>
            </div>
          </template>

          <!-- 管理员菜单 -->
          <template v-if="userStore.user?.role === 'TEACHER' || userStore.user?.role === 'ADMIN'">
            <div class="menu-section">
              <span class="menu-section-label" v-show="!isCollapse">竞赛管理</span>
              <el-menu-item index="/admin/competitions">
                <el-icon><Setting /></el-icon>
                <template #title><span>竞赛管理</span></template>
              </el-menu-item>
              <el-menu-item index="/admin/registrations">
                <el-icon><Document /></el-icon>
                <template #title><span>报名审核</span></template>
              </el-menu-item>
              <el-menu-item index="/admin/reviews">
                <el-icon><ChatDotRound /></el-icon>
                <template #title><span>评审分配</span></template>
              </el-menu-item>
              <el-menu-item index="/admin/my-reviews">
                <el-icon><EditPen /></el-icon>
                <template #title><span>我的评审</span></template>
              </el-menu-item>
              <el-menu-item index="/admin/results">
                <el-icon><Trophy /></el-icon>
                <template #title><span>成绩公示</span></template>
              </el-menu-item>
            </div>
          </template>

          <!-- 超级管理员菜单 -->
          <template v-if="userStore.user?.role === 'ADMIN'">
            <div class="menu-section">
              <span class="menu-section-label" v-show="!isCollapse">系统设置</span>
              <el-menu-item index="/super-admin/users">
                <el-icon><User /></el-icon>
                <template #title><span>用户管理</span></template>
              </el-menu-item>
              <el-menu-item index="/super-admin/logs">
                <el-icon><Document /></el-icon>
                <template #title><span>操作日志</span></template>
              </el-menu-item>
            </div>
          </template>

          <!-- 访客菜单 -->
          <template v-if="!userStore.user?.role || userStore.user?.role === 'GUEST'">
            <div class="menu-section">
              <span class="menu-section-label" v-show="!isCollapse">浏览</span>
              <el-menu-item index="/guest/competitions">
                <el-icon><List /></el-icon>
                <template #title><span>竞赛列表</span></template>
              </el-menu-item>
            </div>
          </template>
        </el-menu>
      </div>

      <!-- 底部用户信息 -->
      <div class="sidebar-footer" v-show="!isCollapse">
        <div class="user-card">
          <el-avatar :size="36" class="user-card-avatar">
            {{ userStore.user?.name?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="user-card-info">
            <p class="user-card-name">{{ userStore.user?.name || '未知用户' }}</p>
            <p class="user-card-role">{{ getRoleName(userStore.user?.role) }}</p>
          </div>
        </div>
      </div>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <button class="collapse-btn" @click="toggleSidebar">
            <el-icon :size="18">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
          </button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/guest/competitions' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <div class="header-actions">
            <button class="icon-btn" @click="toggleFullscreen">
              <el-icon :size="18"><FullScreen /></el-icon>
            </button>
            <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
              <button class="icon-btn">
                <el-icon :size="18"><Bell /></el-icon>
              </button>
            </el-badge>
          </div>

          <el-dropdown class="user-dropdown" @command="handleCommand">
            <div class="user-avatar-section">
              <el-avatar :size="34" class="user-avatar">
                {{ userStore.user?.name?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-info">
                <p class="username">{{ userStore.user?.name || '未知用户' }}</p>
                <p class="role">{{ getRoleName(userStore.user?.role) }}</p>
              </div>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容区 -->
      <el-main class="layout-main">
        <transition name="fade" mode="out-in">
          <router-view />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import { logout } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const notificationCount = ref(0)

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  const routes = []
  const path = route.path
  const routeMap = {
    '/participant/dashboard': { title: '我的首页', path: '/participant/dashboard' },
    '/participant/registrations': { title: '我的报名', path: '/participant/registrations' },
    '/participant/works': { title: '作品管理', path: '/participant/works' },
    '/participant/scores': { title: '成绩查询', path: '/participant/scores' },
    '/admin/dashboard': { title: '管理首页', path: '/admin/dashboard' },
    '/admin/competitions': { title: '竞赛管理', path: '/admin/competitions' },
    '/admin/registrations': { title: '报名审核', path: '/admin/registrations' },
    '/admin/reviews': { title: '评审分配', path: '/admin/reviews' },
    '/admin/my-reviews': { title: '我的评审', path: '/admin/my-reviews' },
    '/admin/results': { title: '成绩公示', path: '/admin/results' },
    '/super-admin/users': { title: '用户管理', path: '/super-admin/users' },
    '/super-admin/logs': { title: '操作日志', path: '/super-admin/logs' },
    '/profile': { title: '个人中心', path: '/profile' }
  }
  if (routeMap[path]) {
    routes.push(routeMap[path])
  }
  return routes
})

const getRoleName = (role) => {
  const roleMap = {
    'ADMIN': '超级管理员',
    'TEACHER': '竞赛管理员',
    'STUDENT': '参赛人员'
  }
  return roleMap[role] || role
}

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      if (userStore.user?.role === 'ADMIN') {
        router.push('/super-admin/users')
      } else {
        ElMessage.info('系统设置仅管理员可用')
      }
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
/* ========== Design Tokens ========== */
.layout-container {
  --sidebar-bg: linear-gradient(180deg, #0F172A 0%, #1E293B 100%);
  --sidebar-surface: rgba(15, 23, 42, 0.6);
  --header-bg: rgba(255, 255, 255, 0.85);
  --header-border: rgba(226, 232, 240, 0.6);
  --main-bg: var(--bg-primary);
  --text-dark: var(--text-primary);
  --text-muted: var(--text-secondary);
  --ease: var(--transition-normal);

  height: 100vh;
  overflow: hidden;
  font-family: 'Inter', 'Noto Sans SC', sans-serif;
  background: var(--main-bg);
}

/* ========== 侧边栏 ========== */
.layout-aside {
  background: var(--sidebar-bg);
  border-right: 1px solid var(--border-light);
  transition: width var(--transition-normal);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  backdrop-filter: blur(10px);
  box-shadow: var(--shadow-lg);
}

/* Logo区 */
.sidebar-header {
  padding: 18px 18px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  flex-shrink: 0;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--primary-600), var(--primary-500));
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: var(--shadow-md);
  transition: transform var(--transition-fast);
}

.logo-icon:hover {
  transform: scale(1.05);
}

.brand {
  overflow: hidden;
  white-space: nowrap;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-white);
  margin: 0;
  line-height: 1.2;
  letter-spacing: -0.2px;
}

.brand-sub {
  font-size: 11px;
  font-weight: 500;
  color: var(--primary-400);
  margin: 3px 0 0;
  opacity: 0.8;
}

/* 菜单区域 */
.sidebar-body {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 10px 0;
}

.sidebar-body::-webkit-scrollbar {
  width: 0;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

/* 菜单分组 */
.menu-section {
  margin-bottom: 2px;
}

.menu-section-label {
  display: block;
  padding: 14px 22px 6px;
  font-size: 10px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.2);
  text-transform: uppercase;
  letter-spacing: 1.5px;
}

/* 菜单项 */
.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.7);
  margin: 4px 12px;
  border-radius: var(--radius-lg);
  height: 44px;
  line-height: 44px;
  transition: all var(--transition-normal);
  position: relative;
  cursor: pointer;
  border: 1px solid transparent;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 10px;
  transition: color var(--ease);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: rgba(255, 255, 255, 0.85);
  background: rgba(255, 255, 255, 0.05);
}

.sidebar-menu :deep(.el-menu-item:hover .el-icon) {
  color: rgba(255, 255, 255, 0.8);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: var(--text-white);
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(4px);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: var(--text-white);
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.2), transparent);
  border-color: var(--primary-500);
  box-shadow: var(--shadow-sm);
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: var(--primary-400);
}

.sidebar-menu :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: -1px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  border-radius: 0 4px 4px 0;
  background: linear-gradient(180deg, var(--gold), var(--blue-light));
}

/* 折叠状态 */
.sidebar-menu :deep(.el-menu--collapse .el-menu-item) {
  margin: 2px 10px;
  padding: 0 !important;
  justify-content: center;
}

.sidebar-menu :deep(.el-menu--collapse .el-menu-item .el-icon) {
  margin-right: 0;
}

/* ========== 侧边栏底部用户卡片 ========== */
.sidebar-footer {
  flex-shrink: 0;
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: var(--radius-lg);
  background: var(--sidebar-surface);
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all var(--transition-normal);
}

.user-card:hover {
  background: rgba(255, 255, 255, 0.05);
  transform: translateY(-2px);
}

.user-card-avatar {
  background: linear-gradient(135deg, var(--primary-700), var(--primary-500));
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.user-card-info {
  flex: 1;
  overflow: hidden;
}

.user-card-name {
  font-size: 13px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-card-role {
  font-size: 11px;
  font-weight: 500;
  color: var(--gold-light);
  margin: 2px 0 0;
  opacity: 0.7;
}

/* ========== 顶部导航栏 ========== */
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--header-bg);
  backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid var(--header-border);
  padding: 0 24px;
  height: 56px;
  box-shadow: var(--shadow-sm);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  transition: all var(--transition-normal);
}

.collapse-btn:hover {
  background: var(--bg-secondary);
  color: var(--primary-500);
  transform: scale(1.1);
}

.collapse-btn:focus-visible {
  outline: 2px solid var(--blue);
  outline-offset: 2px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-right: 16px;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  transition: background var(--ease), color var(--ease);
}

.icon-btn:hover {
  background: #EFF6FF;
  color: var(--blue);
}

.icon-btn:focus-visible {
  outline: 2px solid var(--blue);
  outline-offset: 2px;
}

.notification-badge :deep(.el-badge__content) {
  transform: translateY(-8px) translateX(8px);
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 14px 5px 5px;
  border-radius: 10px;
  transition: background var(--ease), border-color var(--ease);
  border: 1px solid transparent;
}

.user-avatar-section:hover {
  background: #EFF6FF;
  border-color: rgba(59, 130, 246, 0.1);
}

.user-avatar {
  background: linear-gradient(135deg, var(--navy), var(--blue));
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  overflow: hidden;
}

.user-info .username {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-dark);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.3;
}

.user-info .role {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
  margin: 0;
  line-height: 1.3;
}

.dropdown-arrow {
  color: var(--text-muted);
  font-size: 12px;
  transition: transform var(--ease);
}

.user-avatar-section:hover .dropdown-arrow {
  transform: rotate(180deg);
  color: var(--blue);
}

/* ========== 主要内容区 ========== */
.layout-main {
  background: var(--main-bg);
  padding: 24px;
  overflow-y: auto;
  position: relative;
}

.layout-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(180deg, rgba(59, 130, 246, 0.03) 0%, transparent 100%);
  pointer-events: none;
  z-index: 0;
}

/* ========== 过渡动画 ========== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from { opacity: 0; }
.fade-leave-to { opacity: 0; }

.fade-text-enter-active,
.fade-text-leave-active {
  transition: opacity 0.15s ease;
}

.fade-text-enter-from,
.fade-text-leave-to {
  opacity: 0;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .layout-aside {
    position: absolute;
    z-index: 1000;
    height: 100%;
  }

  .layout-header { padding: 0 16px; }
  .header-actions { margin-right: 8px; }
  .user-info { display: none; }
  .sidebar-footer { display: none; }
}
</style>
