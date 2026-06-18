import request from '@/utils/request'

//
// 奖项模块接口
//

//发布奖项
export function publishAward(data) {
  return request({
    url: '/award/publish',
    method: 'post',
    data
  })
}

//公示竞赛结果
export function publishAnnouncement(competitionId) {
  return request({
    url: `/award/announcement/${competitionId}`,
    method: 'post'
  })
}

//获取某竞赛的奖项列表
export function getAwardList(competitionId) {
  return request({
    url: `/award/list/${competitionId}`,
    method: 'get'
  })
}

//获取某竞赛的成绩结果（需登录）
export function getCompetitionResults(competitionId) {
  return request({
    url: `/award/results/${competitionId}`,
    method: 'get'
  })
}

//获取某竞赛的成绩结果（公开，游客可见）
export function getPublicResults(competitionId) {
  return request({
    url: `/award/public/results/${competitionId}`,
    method: 'get'
  })
}

//获取某竞赛的往届获奖成果（游客可见）
export function getFinishedAwards(competitionId) {
  return request({
    url: `/award/finished/${competitionId}`,
    method: 'get'
  })
}
