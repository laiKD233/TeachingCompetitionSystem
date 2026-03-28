<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    <div class="bg-shape shape-3"></div>
    
    <el-card class="login-card" shadow="always">
      <!-- Logo区域 -->
      <div class="logo-section">
        <div class="logo-icon">
          <el-icon :size="48" color="#fff"><Trophy /></el-icon>
        </div>
        <h1 class="login-title">教学竞赛管理系统</h1>
        <p class="login-subtitle">Teaching Competition Management System</p>
      </div>
      
      <!-- 登录表单 -->
      <el-form :model="loginForm" :rules="rules" ref="formRef" size="large" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password"
            placeholder="请输入密码"
            show-password
            class="custom-input"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading"
            @click="handleLogin"
            class="login-button"
            size="large"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <span class="footer-text">还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
    
    <!-- 版权信息 -->
    <div class="copyright">
      <p>&copy; 2026 教学竞赛管理系统</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, getCurrentUser } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setUser(res.data, res.data.token)
        
        const userInfo = await getCurrentUser()
        userStore.setUser(userInfo.data, res.data.token)
        
        ElMessage.success('登录成功')
        
        const role = userInfo.data.role
        if (role === 'ADMIN') {
          router.push('/super-admin/dashboard')
        } else if (role === 'TEACHER') {
          router.push('/admin/dashboard')
        } else {
          router.push('/participant/dashboard')
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰图形 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 400px;
  height: 400px;
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  bottom: -150px;
  right: -150px;
  animation-delay: 2s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: 10%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 登录卡片 */
.login-card {
  width: 460px;
  padding: 40px;
  border-radius: 20px;
  border: none;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 35px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: var(--primary-gradient);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  margin-bottom: 25px;
}

.custom-input {
  border-radius: 10px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 8px 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.input-icon {
  color: #909399;
  font-size: 16px;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: var(--primary-gradient);
  border: none;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

/* 底部区域 */
.login-footer {
  text-align: center;
  margin-top: 20px;
}

.footer-text {
  color: #606266;
  font-size: 14px;
}

.register-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 分隔线 */
.divider {
  margin: 30px 0;
}

.divider-text {
  color: #909399;
  font-size: 13px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 测试账号 */
.quick-test {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.test-tag {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid;
}

.test-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.test-tag.type-info {
  border-color: #909399;
  color: #606266;
  background: rgba(144, 147, 153, 0.05);
}

.test-tag.type-success {
  border-color: #67c23a;
  color: #67c23a;
  background: rgba(103, 194, 58, 0.05);
}

.test-tag.type-warning {
  border-color: #e6a23c;
  color: #e6a23c;
  background: rgba(230, 162, 60, 0.05);
}

.tag-icon {
  margin-right: 4px;
}

/* 版权信息 */
.copyright {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  text-align: center;
  z-index: 1;
}

.copyright p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }
  
  .logo-icon {
    width: 60px;
    height: 60px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .quick-test {
    gap: 8px;
  }
  
  .test-tag {
    font-size: 12px;
    padding: 6px 12px;
  }
}

/* 动画 */
.login-card {
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
