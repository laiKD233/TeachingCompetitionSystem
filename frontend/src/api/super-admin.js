import request from '@/utils/request'

//
// 超级管理员模块接口
//

//获取用户列表（支持按角色、状态、关键词筛选）
export function getUserList(params) {
  return request({
    url: '/super-admin/users',
    method: 'get',
    params
  })
}

//创建用户
export function createUser(data) {
  return request({
    url: '/super-admin/users',
    method: 'post',
    data
  })
}

//更新用户信息
export function updateUser(id, data) {
  return request({
    url: `/super-admin/users/${id}`,
    method: 'put',
    data
  })
}

//删除用户
export function deleteUser(id) {
  return request({
    url: `/super-admin/users/${id}`,
    method: 'delete'
  })
}

//冻结用户
export function freezeUser(id) {
  return request({
    url: `/super-admin/users/${id}/freeze`,
    method: 'put'
  })
}

//解冻用户
export function unfreezeUser(id) {
  return request({
    url: `/super-admin/users/${id}/unfreeze`,
    method: 'put'
  })
}

//重置用户密码
export function resetUserPassword(id) {
  return request({
    url: `/super-admin/users/${id}/reset-password`,
    method: 'put'
  })
}

//获取操作日志列表
export function getOperationLogs(params) {
  return request({
    url: '/super-admin/logs',
    method: 'get',
    params
  })
}

//获取仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

//获取用户增长趋势
export function getUserTrend(days = 7) {
  return request({
    url: '/dashboard/user-trend',
    method: 'get',
    params: { days }
  })
}

//获取竞赛统计数据
export function getCompetitionStats() {
  return request({
    url: '/dashboard/competition-stats',
    method: 'get'
  })
}

//分配竞赛给管理员
export function assignCompetition(data) {
  return request({
    url: '/competition-admin/assign',
    method: 'post',
    data
  })
}

//移除管理员的竞赛分配
export function removeCompetition(data) {
  return request({
    url: '/competition-admin/remove',
    method: 'post',
    data
  })
}

//获取管理员管理的竞赛列表
export function getAdminCompetitions(adminId) {
  return request({
    url: `/competition-admin/list/${adminId}`,
    method: 'get'
  })
}

//获取所有竞赛列表
export function getAllCompetitions() {
  return request({
    url: '/competition/list',
    method: 'get'
  })
}
