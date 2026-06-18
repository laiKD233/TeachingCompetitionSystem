import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const apiBaseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const request = axios.create({
  baseURL: apiBaseURL,// 请求的基地址,方便统一管理
  timeout: 10000// 请求超时时间，10000毫秒
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    // 公开接口不需要 token
    const isPublicApi = config.url.includes('/public/')
    if (token && !isPublicApi) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    if (response.config.responseType === 'blob') {
      return response
    }

    const res = response.data

    if (res.code !== 200) {
      if (res.code === 400) {
        ElMessage.warning(res.message || '操作未完成，请检查填写内容')
      } else {
        ElMessage.error(res.message || '请求失败')
      }

      if (res.code === 401 || res.code === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else if (status === 403) {
      ElMessage.error('没有权限访问该资源')
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
