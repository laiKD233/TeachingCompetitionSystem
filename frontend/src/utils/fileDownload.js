import { ElMessage } from 'element-plus'
import { downloadFileByUrl } from '@/api/file'

export async function downloadWorkFile(fileUrl, fallbackName = '作品文件') {
  if (!fileUrl) {
    ElMessage.warning('文件路径无效')
    return
  }

  const response = await downloadFileByUrl(fileUrl)
  const blob = response.data
  const filename = getFilenameFromResponse(response, fallbackName)
  const objectUrl = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = objectUrl
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(objectUrl)
}

function getFilenameFromResponse(response, fallbackName) {
  const disposition = response.headers?.['content-disposition'] || ''
  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1])
  }

  const filenameMatch = disposition.match(/filename="?([^";]+)"?/i)
  if (filenameMatch?.[1]) {
    return decodeURIComponent(filenameMatch[1])
  }

  return fallbackName || '作品文件'
}
