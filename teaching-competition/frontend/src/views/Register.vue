<template>
  <div class="register-page">
    <!-- 动态背景 -->
    <div class="bg-layer">
      <div class="bg-image"></div>
      <div class="bg-overlay"></div>
      <div class="bg-grid"></div>
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
    </div>

    <!-- 左侧品牌区 -->
    <div class="brand-panel">
      <div class="brand-inner">
        <!-- 装饰顶线 -->
        <div class="brand-top-line"></div>

        <!-- 徽标 -->
        <div class="brand-medal">
          <div class="medal-ring"></div>
          <el-icon :size="30" color="#FBBF24"><Trophy /></el-icon>
        </div>

        <h1 class="brand-title">开启竞赛之旅</h1>
        <p class="brand-tagline">Join the Competition</p>
        <p class="brand-desc">注册成为平台用户，参与精彩竞赛，展示教学风采</p>

        <!-- 亮点 -->
        <div class="brand-highlights">
          <div class="highlight-card">
            <div class="highlight-icon-wrap">
              <el-icon :size="20" color="#60A5FA"><UserFilled /></el-icon>
            </div>
            <div class="highlight-body">
              <strong>快速注册</strong>
              <span>一分钟创建账号，即刻开始报名</span>
            </div>
          </div>
          <div class="highlight-card">
            <div class="highlight-icon-wrap">
              <el-icon :size="20" color="#FBBF24"><Lock /></el-icon>
            </div>
            <div class="highlight-body">
              <strong>数据安全</strong>
              <span>端到端加密传输，隐私全面保障</span>
            </div>
          </div>
          <div class="highlight-card">
            <div class="highlight-icon-wrap">
              <el-icon :size="20" color="#34D399"><CircleCheck /></el-icon>
            </div>
            <div class="highlight-body">
              <strong>即时参与</strong>
              <span>注册完成后即可浏览并报名参赛</span>
            </div>
          </div>
        </div>

        <!-- 步骤指示 -->
        <div class="brand-steps">
          <div class="step-item">
            <div class="step-dot active"></div>
            <span class="step-text">填写信息</span>
          </div>
          <div class="step-line"></div>
          <div class="step-item">
            <div class="step-dot"></div>
            <span class="step-text">提交注册</span>
          </div>
          <div class="step-line"></div>
          <div class="step-item">
            <div class="step-dot"></div>
            <span class="step-text">开始参赛</span>
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <p>&copy; 2026 教学竞赛管理平台 · All Rights Reserved</p>
      </div>
    </div>

    <!-- 右侧注册区 -->
    <div class="form-panel">
      <div class="form-card">
        <div class="form-header">
          <div class="form-icon-wrap">
            <el-icon :size="22" color="#3B82F6"><EditPen /></el-icon>
          </div>
          <h2 class="form-title">用户注册</h2>
          <p class="form-subtitle">请填写以下信息创建您的账户</p>
        </div>

        <el-form :model="registerForm" :rules="rules" ref="formRef" size="large" class="register-form">
          <div class="field-row">
            <div class="field-group">
              <label class="field-label">真实姓名</label>
              <el-form-item prop="name">
                <el-input v-model="registerForm.name" placeholder="请输入姓名" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
            <div class="field-group">
              <label class="field-label">用户名</label>
              <el-form-item prop="username">
                <el-input v-model="registerForm.username" placeholder="请输入用户名" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><UserFilled /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </div>

          <div class="field-group">
            <label class="field-label">密码</label>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password class="custom-input">
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="field-row">
            <div class="field-group">
              <label class="field-label">学号/工号</label>
              <el-form-item prop="studentId">
                <el-input v-model="registerForm.studentId" placeholder="请输入学号" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><Ticket /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
            <div class="field-group">
              <label class="field-label">所属学院</label>
              <el-form-item prop="college">
                <el-input v-model="registerForm.college" placeholder="请输入学院" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><School /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </div>

          <div class="field-row">
            <div class="field-group">
              <label class="field-label">联系电话</label>
              <el-form-item prop="phone">
                <el-input v-model="registerForm.phone" placeholder="请输入电话" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><Phone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
            <div class="field-group">
              <label class="field-label">邮箱</label>
              <el-form-item prop="email">
                <el-input v-model="registerForm.email" placeholder="请输入邮箱" class="custom-input">
                  <template #prefix>
                    <el-icon class="input-icon"><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </div>

          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister" class="submit-btn" size="large">
              <span v-if="!loading">立即注册</span>
              <span v-else>注册中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span class="footer-hint">已有账号？</span>
          <router-link to="/login" class="footer-link">立即登录</router-link>
        </div>
      </div>
    </div>
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* ========== Design Tokens ========== */
.register-page {
  --navy: #1E40AF;
  --navy-dark: #1E3A5F;
  --blue: #3B82F6;
  --blue-light: #60A5FA;
  --gold: #F59E0B;
  --gold-light: #FBBF24;
  --gold-glow: rgba(245, 158, 11, 0.25);
  --green: #34D399;
  --bg-deep: #070D1B;
  --bg-surface: #0E1629;
  --glass: rgba(255, 255, 255, 0.05);
  --glass-border: rgba(255, 255, 255, 0.08);
  --glass-hover: rgba(255, 255, 255, 0.1);
  --text-1: #F1F5F9;
  --text-2: rgba(241, 245, 249, 0.6);
  --text-3: rgba(241, 245, 249, 0.35);
  --ease: 200ms cubic-bezier(0.4, 0, 0.2, 1);

  display: flex;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: var(--bg-deep);
}

/* ========== 背景 ========== */
.bg-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.bg-image {
  position: absolute;
  inset: 0;
  background: url('https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=1920&q=80') center/cover no-repeat;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(7, 13, 27, 0.94) 0%, rgba(14, 22, 41, 0.85) 50%, rgba(7, 13, 27, 0.94) 100%);
}

.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(59, 130, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(59, 130, 246, 0.03) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse 70% 60% at 70% 50%, black 30%, transparent 70%);
}

.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  pointer-events: none;
}

@media (prefers-reduced-motion: no-preference) {
  .glow { animation: glowPulse 10s ease-in-out infinite; }
}

.glow-1 {
  width: 500px;
  height: 500px;
  background: var(--gold-glow);
  top: -180px;
  right: -120px;
}

.glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(59, 130, 246, 0.12);
  bottom: -150px;
  left: 20%;
  animation-delay: 5s;
}

@keyframes glowPulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.08); }
}

/* ========== 左侧品牌面板 ========== */
.brand-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  z-index: 1;
  padding: 60px 56px;
  min-width: 0;
}

.brand-inner {
  max-width: 500px;
}

.brand-top-line {
  width: 48px;
  height: 3px;
  background: linear-gradient(90deg, var(--gold), var(--blue));
  border-radius: 2px;
  margin-bottom: 32px;
}

.brand-medal {
  position: relative;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
}

.medal-ring {
  position: absolute;
  inset: -4px;
  border-radius: 50%;
  border: 2px solid rgba(245, 158, 11, 0.25);
  animation: ringRotate 12s linear infinite;
}

@media (prefers-reduced-motion: prefer) {
  .medal-ring { animation: none; }
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.brand-medal::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.2) 0%, transparent 70%);
}

.brand-title {
  font-size: 34px;
  font-weight: 800;
  color: var(--text-1);
  margin: 0 0 4px;
  letter-spacing: -0.5px;
  line-height: 1.15;
}

.brand-tagline {
  font-size: 12px;
  font-weight: 600;
  color: var(--blue-light);
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0 0 16px;
}

.brand-desc {
  font-size: 15px;
  color: var(--text-2);
  margin: 0 0 36px;
  line-height: 1.6;
}

/* 亮点卡片 */
.brand-highlights {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 36px;
}

.highlight-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: var(--glass);
  backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 14px;
  transition: background var(--ease), border-color var(--ease), transform var(--ease);
  cursor: default;
}

.highlight-card:hover {
  background: var(--glass-hover);
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateX(4px);
}

.highlight-icon-wrap {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.highlight-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.highlight-body strong {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-1);
}

.highlight-body span {
  font-size: 12px;
  color: var(--text-3);
  line-height: 1.5;
}

/* 步骤指示器 */
.brand-steps {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 20px 24px;
  background: var(--glass);
  backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 14px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.step-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  border: 2px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.step-dot.active {
  background: var(--blue);
  border-color: var(--blue);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.2);
}

.step-text {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-3);
  white-space: nowrap;
}

.step-item:first-child .step-text {
  color: var(--blue-light);
}

.step-line {
  flex: 1;
  height: 2px;
  background: rgba(255, 255, 255, 0.08);
  margin: 0 12px;
  margin-bottom: 22px;
}

.brand-footer {
  margin-top: auto;
  padding-top: 40px;
}

.brand-footer p {
  font-size: 12px;
  color: var(--text-3);
  margin: 0;
}

/* ========== 右侧表单面板 ========== */
.form-panel {
  width: 580px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  padding: 40px;
  flex-shrink: 0;
}

.form-card {
  width: 100%;
  max-width: 480px;
}

@media (prefers-reduced-motion: no-preference) {
  .form-card { animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-header {
  margin-bottom: 28px;
  text-align: center;
}

.form-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12), rgba(59, 130, 246, 0.04));
  border: 1px solid rgba(59, 130, 246, 0.15);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.form-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-1);
  margin: 0 0 8px;
  letter-spacing: -0.3px;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-3);
  margin: 0;
}

/* ========== 表单样式 ========== */
.register-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.field-group {
  flex: 1;
  min-width: 0;
}

.field-row {
  display: flex;
  gap: 14px;
}

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-2);
  margin-bottom: 8px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 4px 16px;
  background: var(--glass);
  backdrop-filter: blur(8px);
  border: 1px solid var(--glass-border);
  box-shadow: none;
  transition: border-color var(--ease), background var(--ease), box-shadow var(--ease);
}

.custom-input :deep(.el-input__wrapper:hover) {
  background: var(--glass-hover);
  border-color: rgba(255, 255, 255, 0.14);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background: var(--glass-hover);
  border-color: var(--blue);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.custom-input :deep(.el-input__inner) {
  color: var(--text-1);
  font-size: 14px;
  font-family: inherit;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.25);
}

.input-icon {
  color: var(--text-3);
  font-size: 16px;
}

/* ========== 提交按钮 ========== */
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 700;
  font-family: inherit;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--blue), var(--navy)) !important;
  border: none !important;
  color: #fff !important;
  cursor: pointer;
  letter-spacing: 2px;
  transition: all var(--ease);
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, var(--navy), var(--blue));
  opacity: 0;
  transition: opacity var(--ease);
}

.submit-btn:hover::before {
  opacity: 1;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3) !important;
}

.submit-btn:active {
  transform: translateY(0);
  box-shadow: none !important;
}

/* ========== 底部链接 ========== */
.form-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--glass-border);
}

.footer-hint {
  font-size: 14px;
  color: var(--text-3);
}

.footer-link {
  font-size: 14px;
  font-weight: 700;
  color: var(--blue-light);
  text-decoration: none;
  margin-left: 4px;
  cursor: pointer;
  transition: color var(--ease);
}

.footer-link:hover {
  color: var(--gold-light);
}

/* ========== 响应式 ========== */
@media (max-width: 960px) {
  .brand-panel { display: none; }
  .form-panel { width: 100%; }
  .bg-image {
    background: url('https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=960&q=80') center/cover no-repeat;
  }
  .bg-overlay {
    background: linear-gradient(135deg, rgba(7, 13, 27, 0.96) 0%, rgba(14, 22, 41, 0.9) 60%, rgba(7, 13, 27, 0.96) 100%);
  }
}

@media (max-width: 576px) {
  .form-panel { padding: 24px; }
  .field-row { flex-direction: column; gap: 0; }
  .form-title { font-size: 22px; }
}
</style>
