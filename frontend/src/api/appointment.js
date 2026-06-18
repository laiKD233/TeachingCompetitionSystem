import request from '@/utils/request'

// 创建预约
export function createAppointment(data) {
  return request({
    url: '/appointment',
    method: 'post',
    data
  })
}

// 批准预约
export function approveAppointment(id) {
  return request({
    url: `/appointment/${id}/approve`,
    method: 'put'
  })
}

// 拒绝预约
export function rejectAppointment(id, reason) {
  return request({
    url: `/appointment/${id}/reject`,
    method: 'put',
    params: { reason }
  })
}

// 取消预约
export function cancelAppointment(id) {
  return request({
    url: `/appointment/${id}/cancel`,
    method: 'put'
  })
}

// 完成预约
export function completeAppointment(id) {
  return request({
    url: `/appointment/${id}/complete`,
    method: 'put'
  })
}

// 获取我的预约
export function getMyAppointments() {
  return request({
    url: '/appointment/my',
    method: 'get'
  })
}

// 获取指导老师的预约
export function getAdvisorAppointments(status) {
  return request({
    url: '/appointment/advisor',
    method: 'get',
    params: { status }
  })
}

// 获取指定老师当月日程
export function getAdvisorMonthSchedules(advisorId, year, month) {
  return request({
    url: `/appointment/advisor/${advisorId}/schedules`,
    method: 'get',
    params: { year, month }
  })
}
