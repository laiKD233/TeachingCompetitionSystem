import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}
//退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'get'
  })
}
// 注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}
//获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}
//更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}
//更新密码
export function updatePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}
