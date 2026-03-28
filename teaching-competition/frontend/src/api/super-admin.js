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
