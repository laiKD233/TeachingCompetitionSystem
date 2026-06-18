<template>
  <div class="profile-page">
    <div class="page-header-wrapper">
      <div class="header-decoration">
        <div class="deco-circle deco-circle-1"></div>
        <div class="deco-circle deco-circle-2"></div>
      </div>
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">个人中心</h1>
          <p class="page-subtitle">查看和修改您的个人信息</p>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <!-- 基本信息 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <el-icon :size="18"><User /></el-icon>
            <span>基本信息</span>
          </div>
        </template>
        <el-form :model="profileForm" label-width="100px" :rules="profileRules" ref="profileFormRef">
          <el-form-item label="用户名">
            <el-input :value="profileForm.username" disabled />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="profileForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="角色">
            <el-tag>{{ getRoleName(profileForm.role) }}</el-tag>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSaveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 详细信息 -->
      <el-card class="info-card" v-if="profileForm.role">
        <template #header>
          <div class="card-header">
            <el-icon :size="18"><Document /></el-icon>
            <span>详细信息</span>
          </div>
        </template>

        <el-form :model="detailForm" label-width="100px" :rules="detailRules" ref="detailFormRef">
          <el-form-item label="工号/学号" prop="no">
            <el-input v-model="detailForm.no" placeholder="请输入工号/学号" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="detailForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="detailForm.idCard" placeholder="请输入身份证号" maxlength="18" />
          </el-form-item>

          <template v-if="isTeacher">
            <el-form-item label="学历" prop="education">
              <el-select v-model="detailForm.education" placeholder="请选择学历" style="width: 100%">
                <el-option label="小学" :value="1" />
                <el-option label="初中" :value="2" />
                <el-option label="高中" :value="3" />
                <el-option label="专科" :value="4" />
                <el-option label="本科" :value="5" />
                <el-option label="硕士" :value="6" />
                <el-option label="博士" :value="7" />
              </el-select>
            </el-form-item>
            <el-form-item label="学位" prop="degree">
              <el-select v-model="detailForm.degree" placeholder="请选择学位" style="width: 100%">
                <el-option label="学士" :value="1" />
                <el-option label="硕士" :value="2" />
                <el-option label="博士" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="detailForm.major" placeholder="请输入专业" />
            </el-form-item>
          </template>

          <template v-if="isStudent">
            <el-form-item label="入学年份" prop="entryYear">
              <el-input-number v-model="detailForm.entryYear" :min="2000" :max="2030" style="width: 100%" />
            </el-form-item>
            <el-form-item label="班级" prop="className">
              <el-input v-model="detailForm.className" placeholder="请输入班级" />
            </el-form-item>
            <el-form-item label="专业" prop="major">
              <el-input v-model="detailForm.major" placeholder="请输入专业" />
            </el-form-item>
          </template>

          <el-form-item label="学院名称" prop="collegeName">
            <el-select v-model="detailForm.collegeName" placeholder="请选择学院" style="width: 100%">
              <el-option label="计算机学院" :value="'计算机学院'" />
              <el-option label="信息工程学院" :value="'信息工程学院'" />
              <el-option label="电子工程学院" :value="'电子工程学院'" />
              <el-option label="自动化学院" :value="'自动化学院'" />
              <el-option label="机械工程学院" :value="'机械工程学院'" />
              <el-option label="材料科学与工程学院" :value="'材料科学与工程学院'" />
              <el-option label="土木工程学院" :value="'土木工程学院'" />
              <el-option label="建筑学院" :value="'建筑学院'" />
              <el-option label="水利工程学院" :value="'水利工程学院'" />
              <el-option label="环境科学与工程学院" :value="'环境科学与工程学院'" />
              <el-option label="化学工程学院" :value="'化学工程学院'" />
              <el-option label="生命科学学院" :value="'生命科学学院'" />
              <el-option label="医学院" :value="'医学院'" />
              <el-option label="药学院" :value="'药学院'" />
              <el-option label="经济管理学院" :value="'经济管理学院'" />
              <el-option label="商学院" :value="'商学院'" />
              <el-option label="法学院" :value="'法学院'" />
              <el-option label="文学院" :value="'文学院'" />
              <el-option label="外国语学院" :value="'外国语学院'" />
              <el-option label="艺术学院" :value="'艺术学院'" />
              <el-option label="体育学院" :value="'体育学院'" />
              <el-option label="教育学院" :value="'教育学院'" />
              <el-option label="马克思主义学院" :value="'马克思主义学院'" />
              <el-option label="新闻传播学院" :value="'新闻传播学院'" />
              <el-option label="公共管理学院" :value="'公共管理学院'" />
              <el-option label="国际关系学院" :value="'国际关系学院'" />
              <el-option label="数学科学学院" :value="'数学科学学院'" />
              <el-option label="物理学院" :value="'物理学院'" />
              <el-option label="化学学院" :value="'化学学院'" />
              <el-option label="历史学院" :value="'历史学院'" />
              <el-option label="哲学学院" :value="'哲学学院'" />
              <el-option label="考古文博学院" :value="'考古文博学院'" />
              <el-option label="社会学院" :value="'社会学院'" />
              <el-option label="心理学院" :value="'心理学院'" />
              <el-option label="地球科学学院" :value="'地球科学学院'" />
              <el-option label="能源与动力工程学院" :value="'能源与动力工程学院'" />
              <el-option label="电气工程学院" :value="'电气工程学院'" />
              <el-option label="控制科学与工程学院" :value="'控制科学与工程学院'" />
              <el-option label="交通运输学院" :value="'交通运输学院'" />
              <el-option label="船舶海洋与建筑工程学院" :value="'船舶海洋与建筑工程学院'" />
              <el-option label="农业与生物学院" :value="'农业与生物学院'" />
              <el-option label="食品科学与工程学院" :value="'食品科学与工程学院'" />
            </el-select>
          </el-form-item>

          <el-form-item label="学校名称" prop="school">
            <el-input v-model="detailForm.school" placeholder="请输入学校名称" />
          </el-form-item>

          <el-form-item label="是否校外" prop="isExternal">
            <el-switch v-model="detailForm.isExternal" />
          </el-form-item>

          <el-form-item label="个人简介" prop="bio">
            <el-input v-model="detailForm.bio" type="textarea" :rows="3" placeholder="请输入个人简介" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="savingDetail" @click="handleSaveDetail">保存详细信息</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 修改密码 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <el-icon :size="18"><Lock /></el-icon>
            <span>修改密码</span>
          </div>
        </template>
        <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="changingPassword" @click="handleChangePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCurrentUser, updateUserInfo, updatePassword } from '@/api/auth'
import { getTeacherProfile, saveTeacherProfile, getStudentProfile, saveStudentProfile } from '@/api/profile'

const userStore = useUserStore()

const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const detailFormRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)
const savingDetail = ref(false)

const profileForm = reactive({
  username: '',
  name: '',
  email: '',
  phone: '',
  role: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const detailForm = reactive({
  no: '',
  realName: '',
  idCard: '',
  education: null,
  degree: null,
  major: '',
  entryYear: null,
  className: '',
  collegeName: '',
  school: '',
  isExternal: false,
  bio: ''
})

const isTeacher = computed(() => {
  return ['ADMIN', 'TEACHER', 'ADVISOR'].includes(profileForm.role)
})

const isStudent = computed(() => {
  return profileForm.role === 'STUDENT'
})

const profileRules = {
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }]
}

const detailRules = {
  no: [{ required: true, message: '工号/学号不能为空', trigger: 'blur' }],
  realName: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
  idCard: [
    { required: true, message: '身份证号不能为空', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const getRoleName = (role) => {
  const roleMap = {
    'ADMIN': '超级管理员',
    'TEACHER': '竞赛管理员',
    'ADVISOR': '指导老师',
    'STUDENT': '参赛人员',
    'GUEST': '访客'
  }
  return roleMap[role] || role
}

const fetchProfile = async () => {
  try {
    const res = await getCurrentUser()
    const user = res.data
    profileForm.username = user.username || ''
    profileForm.name = user.name || ''
    profileForm.email = user.email || ''
    profileForm.phone = user.phone || ''
    profileForm.role = user.role || ''
    
    await fetchDetailProfile()
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const fetchDetailProfile = async () => {
  try {
    if (isTeacher.value) {
      const res = await getTeacherProfile()
      const teacher = res.data
      if (teacher) {
        detailForm.no = teacher.teacherNo || ''
        detailForm.realName = teacher.realName || ''
        detailForm.idCard = teacher.idCard || ''
        detailForm.education = teacher.education
        detailForm.degree = teacher.degree
        detailForm.major = teacher.major || ''
        detailForm.collegeName = teacher.collegeName || ''
        detailForm.school = teacher.school || ''
        detailForm.isExternal = teacher.isExternal || false
        detailForm.bio = teacher.bio || ''
      }
    } else if (isStudent.value) {
      const res = await getStudentProfile()
      const student = res.data
      if (student) {
        detailForm.no = student.studentNo || ''
        detailForm.realName = student.realName || ''
        detailForm.idCard = student.idCard || ''
        detailForm.entryYear = student.entryYear
        detailForm.className = student.className || ''
        detailForm.major = student.major || ''
        detailForm.collegeName = student.collegeName || ''
        detailForm.school = student.school || ''
        detailForm.isExternal = student.isExternal || false
        detailForm.bio = student.bio || ''
      }
    }
  } catch (error) {
    console.error('获取详细信息失败', error)
  }
}

const handleSaveDetail = async () => {
  if (!detailFormRef.value) return
  await detailFormRef.value.validate(async (valid) => {
    if (valid) {
      savingDetail.value = true
      try {
        if (isTeacher.value) {
          await saveTeacherProfile({
            teacherNo: detailForm.no,
            realName: detailForm.realName,
            idCard: detailForm.idCard,
            education: detailForm.education,
            degree: detailForm.degree,
            major: detailForm.major,
            collegeName: detailForm.collegeName,
            school: detailForm.school,
            isExternal: detailForm.isExternal,
            bio: detailForm.bio
          })
        } else if (isStudent.value) {
          await saveStudentProfile({
            studentNo: detailForm.no,
            realName: detailForm.realName,
            idCard: detailForm.idCard,
            entryYear: detailForm.entryYear,
            className: detailForm.className,
            major: detailForm.major,
            collegeName: detailForm.collegeName,
            school: detailForm.school,
            isExternal: detailForm.isExternal,
            bio: detailForm.bio
          })
        }
        ElMessage.success('详细信息保存成功')
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        savingDetail.value = false
      }
    }
  })
}

const handleSaveProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await updateUserInfo({
          name: profileForm.name,
          email: profileForm.email,
          phone: profileForm.phone
        })
        // 更新本地用户信息
        const userInfo = localStorage.getItem('userInfo')
        if (userInfo) {
          const parsed = JSON.parse(userInfo)
          parsed.name = profileForm.name
          parsed.email = profileForm.email
          parsed.phone = profileForm.phone
          localStorage.setItem('userInfo', JSON.stringify(parsed))
          userStore.setUser(parsed, localStorage.getItem('token'))
        }
        ElMessage.success('信息更新成功')
      } catch (error) {
        ElMessage.error('更新失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        userStore.logout()
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-page {
  padding: 0;
  max-width: 800px;
  margin: 0 auto;
}

.page-header-wrapper {
  position: relative;
  padding: 32px 32px 24px;
  margin-bottom: 24px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #1e293b 100%);
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.deco-circle-1 {
  width: 160px;
  height: 160px;
  background: var(--primary-color);
  top: -50px;
  right: -30px;
}

.deco-circle-2 {
  width: 100px;
  height: 100px;
  background: #60A5FA;
  bottom: -20px;
  left: 15%;
}

.page-header {
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-card {
  border-radius: var(--radius-lg);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
  color: var(--text-primary);
}
</style>
