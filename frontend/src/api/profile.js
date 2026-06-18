import request from '@/utils/request'

export function getTeacherProfile() {
  return request({
    url: '/profile/teacher',
    method: 'get'
  })
}

export function saveTeacherProfile(data) {
  return request({
    url: '/profile/teacher',
    method: 'post',
    data
  })
}

export function getStudentProfile() {
  return request({
    url: '/profile/student',
    method: 'get'
  })
}

export function saveStudentProfile(data) {
  return request({
    url: '/profile/student',
    method: 'post',
    data
  })
}