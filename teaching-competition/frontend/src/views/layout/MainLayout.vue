<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="layout-aside">
      <div class="sidebar-header">
        <div class="logo-mini">
          <el-icon :size="32" color="#fff"><Trophy /></el-icon>
        </div>
        <div class="brand">
          <h3 class="brand-name">教学竞赛</h3>
          <p class="brand-sub">管理系统</p>
        </div>
      </div>
      
      <!--router表示使用vue-router，配合index属性使用-->
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
        :collapse="isCollapse"
      >
        <el-menu-item index="/guest/competitions">
          <el-icon><List /></el-icon>
          <template #title>
            <span>竞赛列表</span>
          </template>
        </el-menu-item>
        
        <template v-if="userStore.user?.role === 'STUDENT'">
          <el-menu-item index="/participant/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>
              <span>我的首页</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/participant/registrations">
            <el-icon><Document /></el-icon>
            <template #title>
              <span>我的报名</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/participant/works">
            <el-icon><Upload /></el-icon>
            <template #title>
              <span>作品管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/participant/scores">
            <el-icon><TrendCharts /></el-icon>
            <template #title>
              <span>成绩查询</span>
            </template>
          </el-menu-item>
        </template>
        
        <template v-if="userStore.user?.role === 'TEACHER' || userStore.user?.role === 'ADMIN'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>
              <span>管理首页</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/admin/competitions">
            <el-icon><Setting /></el-icon>
            <template #title>
              <span>竞赛管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/admin/registrations">
            <el-icon><Document /></el-icon>
            <template #title>
              <span>报名审核</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/admin/reviews">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>
              <span>评审分配</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/admin/results">
            <el-icon><Trophy /></el-icon>
            <template #title>
              <span>成绩公示</span>
            </template>
          </el-menu-item>
        </template>
        
        <template v-if="userStore.user?.role === 'ADMIN'">
          <el-menu-item index="/super-admin/users">
            <el-icon><User /></el-icon>
            <template #title>
              <span>用户管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/super-admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>
              <span>操作日志</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-button 
            text 
            @click="toggleSidebar"
            class="collapse-btn"
          >
            <el-icon :size="20"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/guest/competitions' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <div class="header-actions">
            <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
              <el-button text class="action-btn">
                <el-icon :size="20"><Bell /></el-icon>
              </el-button>
            </el-badge>
            <el-button text class="action-btn" @click="toggleFullscreen">
              <el-icon :size="20"><FullScreen /></el-icon>
            </el-button>
          </div>
          
          <el-dropdown class="user-dropdown" @command="handleCommand">
            <div class="user-avatar-section">
              <el-avatar :size="40" class="user-avatar">
                {{ userStore.user?.name?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-info">
                <p class="username">{{ userStore.user?.name || '未知用户' }}</p>
                <p class="role">{{ getRoleName(userStore.user?.role) }}</p>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
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
          <router-view /><!--配合前面的router属性还有index使用-->
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

const route = useRoute()//获取当前路由对象  
const router = useRouter()//获取路由实例对象
const userStore = useUserStore()

const isCollapse = ref(false)
const notificationCount = ref(0)

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {//是一个数组，数组中每个元素是一个对象，对象中包含title和path属性
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
    '/admin/results': { title: '成绩公示', path: '/admin/results' },
    '/super-admin/users': { title: '用户管理', path: '/super-admin/users' },
    '/super-admin/logs': { title: '操作日志', path: '/super-admin/logs' }
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
      ElMessage.info('个人中心功能开发中')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中')
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
.layout-container {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.layout-aside {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  transition: width 0.3s ease;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 8px;
}

.logo-mini {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  background: var(--primary-gradient);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.brand {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
  white-space: nowrap;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  line-height: 1.2;
}

.brand-sub {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.7);
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: var(--primary-gradient);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 顶部导航栏 */
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  /* 或使用 margin-left: auto 方案 */
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  height: 60px;
  /* 确保高度一致，便于垂直居中 */
}

.header-left {
  display: flex;
  align-items: center;
  /* 让左侧内部元素也垂直居中，非必须但推荐 */
  gap: 12px;
  /* 按钮和面包屑之间的间距 */
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  margin-right: 16px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-right: 24px;
}

.action-btn {
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: var(--bg-light);
}

.notification-badge :deep(.el-badge__content) {
  transform: translateY(-8px) translateX(8px);
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-avatar-section:hover {
  background: var(--bg-light);
}

.user-avatar {
  background: var(--primary-gradient);
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  overflow: hidden;
}

.user-info .username {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info .role {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.dropdown-icon {
  color: var(--text-secondary);
  font-size: 14px;
}

/* 主要内容区 */
.layout-main {
  background: var(--bg-light);
  padding: 24px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-aside {
    position: absolute;
    z-index: 1000;
    height: 100%;
  }
  
  .layout-aside:not(.collapsed) {
    width: 240px !important;
  }
  
  .layout-aside.collapsed {
    width: 0 !important;
    overflow: hidden;
  }
  
  .layout-header {
    padding: 0 16px;
  }
  
  .header-actions {
    margin-right: 12px;
  }
  
  .user-info {
    display: none;
  }
}
</style>
