import request from '@/utils/request'

// 获取公开的竞赛列表
export function getPublicCompetitions(params) {
  return request({
    url: '/competition/public/list',
    method: 'get',
    params
  })
}
//获取管理员竞赛列表
export function getAdminCompetitions(params) {
  return request({
    url: '/competition/admin/list',
    method: 'get',
    params
  })
}
// 获取竞赛详情
export function getCompetition(id) {
  return request({
    url: `/competition/public/detail/${id}`,
    method: 'get'
  })
}
//增
export function createCompetition(data) {
  return request({
    url: '/competition',
    method: 'post',
    data
  })
}
//改
export function updateCompetition(id, data) {
  return request({
    url: `/competition/${id}`,
    method: 'put',
    data
  })
}
//删除
export function deleteCompetition(id) {
  return request({
    url: `/competition/${id}`,
    method: 'delete'
  })
}
