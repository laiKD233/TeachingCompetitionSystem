import request from '@/utils/request'

// 创建待办
export function createTodo(data) {
  return request({
    url: '/todo',
    method: 'post',
    data
  })
}

// 更新待办
export function updateTodo(id, data) {
  return request({
    url: `/todo/${id}`,
    method: 'put',
    data
  })
}

// 删除待办
export function deleteTodo(id) {
  return request({
    url: `/todo/${id}`,
    method: 'delete'
  })
}

// 更新待办状态
export function updateTodoStatus(id, status) {
  return request({
    url: `/todo/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取我的待办
export function getMyTodos(status) {
  return request({
    url: '/todo/my',
    method: 'get',
    params: { status }
  })
}
