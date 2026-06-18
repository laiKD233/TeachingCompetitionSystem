import request from '@/utils/request'

//
// 文件模块接口
//

//上传文件
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

//下载文件
export function downloadFile(filename) {
  return request({
    url: `/file/download/${encodePath(filename)}`,
    method: 'get',
    responseType: 'blob'  // 重要：指定响应类型为二进制流
  })
}

export function downloadFileByUrl(fileUrl) {
  const filePath = extractDownloadPath(fileUrl)
  if (!filePath) {
    return Promise.reject(new Error('文件路径无效'))
  }
  return request({
    url: `/file/download/${encodePath(filePath)}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function extractDownloadPath(fileUrl) {
  if (!fileUrl) return ''

  const normalized = String(fileUrl).trim()
  const downloadPrefix = '/api/file/download/'
  const prefixIndex = normalized.indexOf(downloadPrefix)
  if (prefixIndex !== -1) {
    return normalized.slice(prefixIndex + downloadPrefix.length)
  }

  const trimmed = normalized.startsWith('/') ? normalized.slice(1) : normalized
  if (trimmed.startsWith('works/') || trimmed.startsWith('files/') || trimmed.startsWith('covers/')) {
    return trimmed
  }

  return ''
}

function encodePath(path) {
  return path
    .split('/')
    .filter(Boolean)
    .map(segment => encodeURIComponent(segment))
    .join('/')
}
