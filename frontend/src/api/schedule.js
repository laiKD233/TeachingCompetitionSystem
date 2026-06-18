import request from '@/utils/request'

// 创建日程
export function createSchedule(data) {
  return request({
    url: '/schedule',
    method: 'post',
    data
  })
}

// 更新日程
export function updateSchedule(id, data) {
  return request({
    url: `/schedule/${id}`,
    method: 'put',
    data
  })
}

// 删除日程
export function deleteSchedule(id) {
  return request({
    url: `/schedule/${id}`,
    method: 'delete'
  })
}

// 获取月度日程
export function getMonthSchedules(year, month) {
  return request({
    url: '/schedule/month',
    method: 'get',
    params: { year, month }
  })
}
