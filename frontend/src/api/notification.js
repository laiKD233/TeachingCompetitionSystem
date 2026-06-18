import request from '@/utils/request'

export function getMyNotifications(isRead) {
  const params = {}
  if (typeof isRead === 'boolean') {
    params.isRead = isRead
  }

  return request({
    url: '/notification/my',
    method: 'get',
    params
  })
}

export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}

export function markAsRead(id) {
  return request({
    url: `/notification/${id}/read`,
    method: 'put'
  })
}

export function markAllAsRead() {
  return request({
    url: '/notification/read-all',
    method: 'put'
  })
}
