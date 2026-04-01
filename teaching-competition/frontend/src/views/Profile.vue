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
            <el-icon><User /></el-icon>
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

      <!-- 修改密码 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <el-icon><Lock /></el-icon>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCurrentUser, updateUserInfo, updatePassword } from '@/api/auth'

const userStore = useUserStore()

const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)

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

const profileRules = {
  name: [{ required: true, message: '姓名不能为空', trigger: 'blur' }]
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
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
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
