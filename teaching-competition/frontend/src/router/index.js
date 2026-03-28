import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/guest/competitions',
    children: [
      {
        path: '/guest/competitions',
        name: 'GuestCompetitions',
        component: () => import('@/views/guest/Competitions.vue'),
        meta: { title: '竞赛列表', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN'] }
      },
      {
        path: '/guest/competition/:id',
        name: 'CompetitionDetail',
        component: () => import('@/views/guest/CompetitionDetail.vue'),
        meta: { title: '竞赛详情', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN'] }
      },
      {
        path: '/guest/awards',
        name: 'Awards',
        component: () => import('@/views/guest/Awards.vue'),
        meta: { title: '往届成果', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN'] }
      },
      {
        path: '/participant/dashboard',
        name: 'ParticipantDashboard',
        component: () => import('@/views/participant/Dashboard.vue'),
        meta: { title: '参赛人员首页', role: ['STUDENT'] }
      },
      {
        path: '/participant/registrations',
        name: 'MyRegistrations',
        component: () => import('@/views/participant/Registrations.vue'),
        meta: { title: '我的报名', role: ['STUDENT'] }
      },
      {
        path: '/participant/works',
        name: 'MyWorks',
        component: () => import('@/views/participant/Works.vue'),
        meta: { title: '作品管理', role: ['STUDENT'] }
      },
      {
        path: '/participant/scores',
        name: 'MyScores',
        component: () => import('@/views/participant/Scores.vue'),
        meta: { title: '成绩查询', role: ['STUDENT'] }
      },
      {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '竞赛管理员首页', role: ['TEACHER', 'ADMIN'] }
      },
      {
        path: '/admin/competitions',
        name: 'AdminCompetitions',
        component: () => import('@/views/admin/Competitions.vue'),
        meta: { title: '竞赛管理', role: ['TEACHER', 'ADMIN'] }
      },
      {
        path: '/admin/registrations',
        name: 'AdminRegistrations',
        component: () => import('@/views/admin/Registrations.vue'),
        meta: { title: '报名审核', role: ['TEACHER', 'ADMIN'] }
      },
      {
        path: '/admin/reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/Reviews.vue'),
        meta: { title: '评审分配', role: ['TEACHER', 'ADMIN'] }
      },
      {
        path: '/admin/results',
        name: 'AdminResults',
        component: () => import('@/views/admin/Results.vue'),
        meta: { title: '成绩公示', role: ['TEACHER', 'ADMIN'] }
      },
      {
        path: '/super-admin/dashboard',
        name: 'SuperAdminDashboard',
        component: () => import('@/views/super-admin/Dashboard.vue'),
        meta: { title: '超级管理员首页', role: ['ADMIN'] }
      },
      {
        path: '/super-admin/users',
        name: 'UserManagement',
        component: () => import('@/views/super-admin/Users.vue'),
        meta: { title: '用户管理', role: ['ADMIN'] }
      },
      {
        path: '/super-admin/logs',
        name: 'OperationLogs',
        component: () => import('@/views/super-admin/Logs.vue'),
        meta: { title: '操作日志', role: ['ADMIN'] }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 不需要认证的路由直接放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  
  const token = localStorage.getItem('token')
  
  // 如果没有token，跳转到登录页
  if (!token) {
    // 如果已经在登录页，直接放行
    if (to.path === '/login') {
      next()
      return
    }
    console.log('没有token，跳转到登录页', { to, from })
    next('/login')
    return
  }
  
  // 如果有token但没有用户信息，尝试从localStorage恢复
  if (!userStore.user) {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        userStore.setUser(JSON.parse(userInfo), token)
      } catch (error) {
        console.error('恢复用户信息失败', error)
      }
    }
  }
  
  const userRole = userStore.user?.role
  
  // 检查角色权限
  if (to.meta.role) {
    if (!userRole) {
      console.log('用户角色为空，跳转到403', { to, userRole })
      next('/403')
      return
    }
    if (!to.meta.role.includes(userRole)) {
      console.log('用户角色权限不足', { to, userRole, requiredRoles: to.meta.role })
      next('/403')
      return
    }
  }
  
  console.log('路由守卫通过', { to, userRole })
  next()
})

export default router
