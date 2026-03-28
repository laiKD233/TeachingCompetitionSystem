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

//提交评审打分
export function submitScore(data) {
  return request({
    url: '/review/score',
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
