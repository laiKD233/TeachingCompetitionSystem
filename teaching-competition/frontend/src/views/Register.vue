<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">用户注册</h2>
      
      <el-form :model="registerForm" :rules="rules" ref="formRef" size="large">
        <el-form-item prop="name">
          <el-input v-model="registerForm.name" placeholder="真实姓名" prefix-icon="User" />
        </el-form-item>
        
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" prefix-icon="UserFilled" />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        
        <el-form-item prop="studentId">
          <el-input v-model="registerForm.studentId" placeholder="学号/工号" prefix-icon="Ticket" />
        </el-form-item>
        
        <el-form-item prop="college">
          <el-input v-model="registerForm.college" placeholder="所属学院" prefix-icon="School" />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="联系电话" prefix-icon="Phone" />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱" prefix-icon="Message" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  name: '',
  username: '',
  password: '',
  studentId: '',
  college: '',
  phone: '',
  email: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  college: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
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
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-card {
  width: 400px;
  padding: 20px;
}

.register-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.register-footer {
  text-align: center;
  margin-top: 15px;
}
</style>
