import request from '@/utils/request'

//
// 评审模块接口
//

//分配评审任务
export function assignReview(data) {
  return request({
    url: '/review/assign',
    method: 'post',
    data
  })
}

//提交评审打分（评审人使用）
export function submitScore(data) {
  return request({
    url: '/review/score',
    method: 'post',
    data
  })
}

//管理员提交评审打分
export function adminSubmitScore(data) {
  return request({
    url: '/review/admin-score',
    method: 'post',
    data
  })
}

//获取我的评审任务列表
export function getMyReviewTasks() {
  return request({
    url: '/review/my-tasks',
    method: 'get'
  })
}

//获取某竞赛的所有评审成绩
export function getReviewScores(competitionId) {
  return request({
    url: `/review/scores/${competitionId}`,
    method: 'get'
  })
}

//获取可分配的评审人列表（TEACHER + ADMIN）
export function getEligibleReviewers() {
  return request({
    url: '/review/reviewers',
    method: 'get'
  })
}

//获取某作品的所有评审任务
export function getWorkReviewTasks(workId) {
  return request({
    url: `/review/work-tasks/${workId}`,
    method: 'get'
  })
}

//获取我的评审任务（含作品文件详情）
export function getMyReviewTaskDetails() {
  return request({
    url: '/review/my-tasks-detail',
    method: 'get'
  })
}
