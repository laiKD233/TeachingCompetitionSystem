import request from '@/utils/request'

//
//作品模块接口
//

//查看我的作品列表
export function getMyWorks(params) {
  return request({
    url: '/work/my',
    method: 'get',
    params
  })
}
//上传作品
export function uploadWork(data) {
  return request({
    url: '/work',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
// 查看单个作品详情
export function getWorkDetail(id) {
  return request({
    url: `/work/${id}`,
    method: 'get'
  })
}

// 更新作品（重新上传）
export function updateWork(id, data) {
  return request({
    url: `/work/${id}`,
    method: 'put',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除作品
export function deleteWork(id) {
  return request({
    url: `/work/${id}`,
    method: 'delete'
  })
}

// 管理员：查看某竞赛的作品列表
export function getAdminWorks(params) {
  return request({
    url: '/work/admin/list',
    method: 'get',
    params
  })
}