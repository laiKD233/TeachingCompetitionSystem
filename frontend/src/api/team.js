import request from '@/utils/request'

// ========== 团队管理 ==========

// 创建团队
export function createTeam(data) {
  return request({
    url: '/team',
    method: 'post',
    data
  })
}

// 更新团队
export function updateTeam(id, data) {
  return request({
    url: `/team/${id}`,
    method: 'put',
    data
  })
}

// 解散团队
export function deleteTeam(id) {
  return request({
    url: `/team/${id}`,
    method: 'delete'
  })
}

// 获取团队详情
export function getTeamDetail(id) {
  return request({
    url: `/team/${id}`,
    method: 'get'
  })
}

// 获取我的团队列表
export function getMyTeams() {
  return request({
    url: '/team/my',
    method: 'get'
  })
}

// 获取我指导的团队列表
export function getAdvisedTeams() {
  return request({
    url: '/team/advised',
    method: 'get'
  })
}

// 获取所有团队列表
export function getAllTeams(params) {
  return request({
    url: '/team/list',
    method: 'get',
    params
  })
}

// 获取可选指导老师列表
export function getAvailableAdvisors() {
  return request({
    url: '/team/advisors',
    method: 'get'
  })
}

// ========== 成员管理 ==========

// 加入团队
export function joinTeam(id, inviteCode) {
  return request({
    url: `/team/${id}/join`,
    method: 'post',
    params: { inviteCode }
  })
}

// 退出团队
export function leaveTeam(id) {
  return request({
    url: `/team/${id}/leave`,
    method: 'post'
  })
}

// 移除成员
export function removeMember(teamId, memberId) {
  return request({
    url: `/team/${teamId}/member/${memberId}`,
    method: 'delete'
  })
}

// 获取团队成员
export function getTeamMembers(id) {
  return request({
    url: `/team/${id}/members`,
    method: 'get'
  })
}

// 获取邀请码
export function getInviteCode(id) {
  return request({
    url: `/team/${id}/invite-code`,
    method: 'get'
  })
}

// ========== 任务管理 ==========

// 创建任务
export function createTask(teamId, data) {
  return request({
    url: `/team/${teamId}/tasks`,
    method: 'post',
    data
  })
}

// 更新任务
export function updateTask(taskId, data) {
  return request({
    url: `/team/tasks/${taskId}`,
    method: 'put',
    data
  })
}

// 删除任务
export function deleteTask(taskId) {
  return request({
    url: `/team/tasks/${taskId}`,
    method: 'delete'
  })
}

// 更新任务状态
export function updateTaskStatus(taskId, status) {
  return request({
    url: `/team/tasks/${taskId}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取团队任务
export function getTeamTasks(teamId) {
  return request({
    url: `/team/${teamId}/tasks`,
    method: 'get'
  })
}

// ========== 消息管理 ==========

// 发送消息
export function sendMessage(teamId, data) {
  return request({
    url: `/team/${teamId}/messages`,
    method: 'post',
    data
  })
}

// 获取团队消息
export function getTeamMessages(teamId, limit = 100) {
  return request({
    url: `/team/${teamId}/messages`,
    method: 'get',
    params: { limit }
  })
}

// ========== 指导老师审核 ==========

// 发起指导老师审核申请
export function applyAdvisorAudit(teamId, advisorId, reason) {
  return request({
    url: '/team/advisor/audit/apply',
    method: 'post',
    params: { teamId, advisorId, reason }
  })
}

// 指导老师同意申请
export function approveAdvisorAudit(auditId) {
  return request({
    url: `/team/advisor/audit/${auditId}/approve`,
    method: 'post'
  })
}

// 指导老师拒绝申请
export function rejectAdvisorAudit(auditId, reason) {
  return request({
    url: `/team/advisor/audit/${auditId}/reject`,
    method: 'post',
    params: { reason }
  })
}

// 获取指导老师待审核的申请列表
export function getPendingAdvisorAudits() {
  return request({
    url: '/team/advisor/audit/pending',
    method: 'get'
  })
}

// 获取团队的审核记录
export function getTeamAudits(teamId) {
  return request({
    url: `/team/advisor/audit/team/${teamId}`,
    method: 'get'
  })
}

// 获取我发起的审核申请
export function getMyAdvisorAudits() {
  return request({
    url: '/team/advisor/audit/my',
    method: 'get'
  })
}
