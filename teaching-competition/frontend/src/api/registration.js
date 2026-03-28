import request from '@/utils/request'

//
// 报名模块接口
//

//获取我的报名列表
export function getRegistrations(params) {
  return request({
    url: '/registration/my',
    method: 'get',
    params
  })
}
//提交新报名
export function createRegistration(data) {
  return request({
    url: '/registration',
    method: 'post',
    data
  })
}

//管理员：查看对应竞赛的报名列表（需传竞赛的id competitionId）
export function getRegistrationList(params) {
  return request({
    url: '/registration/admin/list',
    method: 'get',
    params
  })
}
//管理员：通过报名审核
export function approveRegistration(id) {
  return request({
    url: `/registration/${id}/approve`,
    method: 'put',
  })
}
//管理员：拒绝报名
export function rejectRegistration(id,rejectReason) {
  return request({
    url: `/registration/reject`,
    method: 'put',
    data: { 
      registrationId: id,//拒绝的报名的id
      rejectReason: rejectReason
    }
  })
}
