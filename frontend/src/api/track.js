import request from '@/utils/request'

/**
 * 创建赛道
 */
export const createTrack = (data) => {
  return request({
    url: '/track',
    method: 'post',
    data
  })
}

/**
 * 更新赛道
 */
export const updateTrack = (data) => {
  return request({
    url: '/track',
    method: 'put',
    data
  })
}

/**
 * 根据竞赛ID查询赛道列表
 */
export const getTracksByCompetitionId = (competitionId) => {
  return request({
    url: `/track/competition/${competitionId}`,
    method: 'get'
  })
}

/**
 * 根据ID查询赛道详情
 */
export const getTrackById = (id) => {
  return request({
    url: `/track/${id}`,
    method: 'get'
  })
}

/**
 * 删除赛道
 */
export const deleteTrack = (id) => {
  return request({
    url: `/track/${id}`,
    method: 'delete'
  })
}