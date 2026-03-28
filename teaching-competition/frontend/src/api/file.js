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
    url: `/file/download/${filename}`,
    method: 'get',
    responseType: 'blob'  // 重要：指定响应类型为二进制流
  })
}
