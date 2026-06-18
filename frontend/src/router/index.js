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
    path: '/guest',
    component: () => import('@/views/layout/GuestLayout.vue'),
    redirect: '/guest/home',
    meta: { requiresAuth: false },
    children: [
      {
        path: '/guest/home',
        name: 'GuestHome',
        component: () => import('@/views/guest/Home.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: '/guest/competitions',
        name: 'GuestCompetitions',
        component: () => import('@/views/guest/Competitions.vue'),
        meta: { title: '竞赛列表', requiresAuth: false }
      },
      {
        path: '/guest/competition/:id',
        name: 'CompetitionDetail',
        component: () => import('@/views/guest/CompetitionDetail.vue'),
        meta: { title: '竞赛详情', requiresAuth: false }
      },
      {
        path: '/guest/awards',
        name: 'Awards',
        component: () => import('@/views/guest/Awards.vue'),
        meta: { title: '往届成果', requiresAuth: false }
      }
    ]
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    redirect: '/guest/home',
    children: [
      { path: '/competitions', name: 'Competitions', component: () => import('@/views/guest/Competitions.vue'), meta: { title: '竞赛列表', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/competition/:id', name: 'CompetitionDetailLoggedIn', component: () => import('@/views/guest/CompetitionDetail.vue'), meta: { title: '竞赛详情', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/awards', name: 'AwardsLoggedIn', component: () => import('@/views/guest/Awards.vue'), meta: { title: '往届成果', role: ['GUEST', 'STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] } },
      {
        path: '/participant/dashboard',
        name: 'ParticipantDashboard',
        component: () => import('@/views/participant/Dashboard.vue'),
        meta: { title: '参赛人员首页', role: ['STUDENT'] }
      },
      {
        path: '/participant/competitions',
        name: 'ParticipantCompetitions',
        component: () => import('@/views/participant/Competitions.vue'),
        meta: { title: '竞赛列表', role: ['STUDENT'] }
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
        path: '/participant/teams',
        name: 'MyTeams',
        component: () => import('@/views/participant/TeamList.vue'),
        meta: { title: '我的团队', role: ['STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] }
      },
      {
        path: '/participant/team/:id',
        name: 'TeamDetail',
        component: () => import('@/views/participant/TeamDetail.vue'),
        meta: { title: '团队详情', role: ['STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] }
      },
      {
        path: '/participant/todos',
        name: 'MyTodos',
        component: () => import('@/views/participant/TodoList.vue'),
        meta: { title: '个人待办', role: ['STUDENT'] }
      },
      {
        path: '/participant/appointments',
        name: 'MyAppointments',
        component: () => import('@/views/participant/AppointmentList.vue'),
        meta: { title: '预约指导', role: ['STUDENT'] }
      },
      { path: '/admin/dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue'), meta: { title: '竞赛管理员首页', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/admin/competitions', name: 'AdminCompetitions', component: () => import('@/views/admin/Competitions.vue'), meta: { title: '竞赛管理', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      {
        path: '/admin/registrations',
        name: 'AdminRegistrations',
        component: () => import('@/views/admin/Registrations.vue'),
        meta: { title: '报名审核', role: ['TEACHER', 'ADMIN', 'ADVISOR'] }
      },
      {
        path: '/admin/reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/Reviews.vue'),
        meta: { title: '评审分配', role: ['TEACHER', 'ADMIN'] }
      },
      { path: '/admin/my-reviews', name: 'MyReviews', component: () => import('@/views/admin/MyReviews.vue'), meta: { title: '我的评审', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/admin/results', name: 'AdminResults', component: () => import('@/views/admin/Results.vue'), meta: { title: '成绩公示', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/admin/team-guide', name: 'TeamGuide', component: () => import('@/views/admin/TeamGuide.vue'), meta: { title: '团队指导', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/admin/schedule', name: 'AdvisorSchedule', component: () => import('@/views/admin/Schedule.vue'), meta: { title: '日程安排', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
      { path: '/admin/appointment-manage', name: 'AppointmentManage', component: () => import('@/views/admin/AppointmentManage.vue'), meta: { title: '预约管理', role: ['TEACHER', 'ADMIN', 'ADVISOR'] } },
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
      },
      { path: '/profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心', role: ['STUDENT', 'TEACHER', 'ADMIN', 'ADVISOR'] } }
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
    // 已登录用户访问游客首页，重定向到对应后台页面
    if ((to.path === '/guest' || to.path === '/guest/home') && localStorage.getItem('token')) {
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        try {
          const user = JSON.parse(userInfo)
          if (user.role === 'STUDENT') {
            next('/participant/registrations')
            return
          } else if (user.role === 'TEACHER' || user.role === 'ADMIN' || user.role === 'ADVISOR') {
            next('/admin/dashboard')
            return
          }
        } catch (error) {
          console.error('解析用户信息失败', error)
        }
      }
    }
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
    next('/login')
    return
  }
  
  // 已登录用户访问游客页面，重定向到对应的后台页面
  if (to.path.startsWith('/guest')) {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        // 将游客路径转换为后台路径
        if (to.path === '/guest/competitions') {
          next('/competitions')
          return
        } else if (to.path.startsWith('/guest/competition/')) {
          const competitionId = to.path.split('/').pop()
          next(`/competition/${competitionId}`)
          return
        } else if (to.path === '/guest/awards') {
          next('/awards')
          return
        } else if (user.role === 'STUDENT') {
          next('/participant/registrations')
          return
        } else if (user.role === 'TEACHER' || user.role === 'ADMIN' || user.role === 'ADVISOR') {
          next('/admin/dashboard')
          return
        }
      } catch (error) {
        console.error('解析用户信息失败', error)
      }
    }
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
      next('/403')
      return
    }
    if (!to.meta.role.includes(userRole)) {
      next('/403')
      return
    }
  }

  next()
})

export default router
