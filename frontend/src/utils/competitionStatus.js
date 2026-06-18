export const competitionStatusOptions = [
  { label: '已发布', value: 'PUBLISHED' },
  { label: '报名中', value: 'REGISTRATION' },
  { label: '作品提交中', value: 'SUBMISSION' },
  { label: '评审中', value: 'REVIEW' },
  { label: '公示中', value: 'ANNOUNCEMENT' },
  { label: '已结束', value: 'FINISHED' }
]

const legacyStatusMap = {
  ONGOING: 'SUBMISSION',
  REVIEWED: 'REVIEW',
  ANNOUNCED: 'ANNOUNCEMENT',
  ENDED: 'FINISHED'
}

const statusTextMap = {
  DRAFT: '未发布',
  PUBLISHED: '已发布',
  REGISTRATION: '报名中',
  SUBMISSION: '作品提交中',
  REVIEW: '评审中',
  ANNOUNCEMENT: '公示中',
  FINISHED: '已结束'
}

const statusTypeMap = {
  DRAFT: 'info',
  PUBLISHED: 'primary',
  REGISTRATION: 'success',
  SUBMISSION: 'warning',
  REVIEW: 'warning',
  ANNOUNCEMENT: 'primary',
  FINISHED: 'info'
}

export const normalizeCompetitionStatus = (status) => legacyStatusMap[status] || status

export const getCompetitionStatusText = (status) => {
  const normalized = normalizeCompetitionStatus(status)
  return statusTextMap[normalized] || status || '-'
}

export const getCompetitionStatusType = (status) => {
  const normalized = normalizeCompetitionStatus(status)
  return statusTypeMap[normalized] || 'info'
}

export const isCompetitionResultVisible = (status) => {
  const normalized = normalizeCompetitionStatus(status)
  return normalized === 'ANNOUNCEMENT' || normalized === 'FINISHED'
}
