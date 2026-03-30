<template>
  <div class="login-page">
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

        <!-- 奖杯徽标 -->
        <div class="brand-medal">
          <div class="medal-ring"></div>
          <el-icon :size="32" color="#FBBF24"><Trophy /></el-icon>
        </div>

        <h1 class="brand-title">教学竞赛管理平台</h1>
        <p class="brand-tagline">Teaching Competition Platform</p>
        <p class="brand-desc">助力教学创新，赋能竞赛管理，让每一场竞赛都精彩纷呈</p>

        <!-- 功能亮点 -->
        <div class="brand-highlights">
          <div class="highlight-card">
            <div class="highlight-num">01</div>
            <div class="highlight-body">
              <strong>全流程竞赛管理</strong>
              <span>从发布、报名、评审到公示，一站式闭环管理</span>
            </div>
          </div>
          <div class="highlight-card">
            <div class="highlight-num">02</div>
            <div class="highlight-body">
              <strong>多维度智能评审</strong>
              <span>支持自定义评分标准，专家在线打分，成绩实时汇总</span>
            </div>
          </div>
          <div class="highlight-card">
            <div class="highlight-num">03</div>
            <div class="highlight-body">
              <strong>可视化数据看板</strong>
              <span>竞赛数据一目了然，多维度统计分析与报表导出</span>
            </div>
          </div>
        </div>

        <!-- 数据统计条 -->
        <div class="brand-stats">
          <div class="stat-item">
            <span class="stat-value">100+</span>
            <span class="stat-label">竞赛项目</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">5,000+</span>
            <span class="stat-label">参赛选手</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">50+</span>
            <span class="stat-label">合作院校</span>
          </div>
        </div>
      </div>
      <div class="brand-footer">
        <p>&copy; 2026 教学竞赛管理平台 · All Rights Reserved</p>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="form-panel">
      <div class="form-card">
        <div class="form-header">
          <div class="form-icon-wrap">
            <el-icon :size="22" color="#3B82F6"><User /></el-icon>
          </div>
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账户以继续使用系统</p>
        </div>

        <el-form :model="loginForm" :rules="rules" ref="formRef" size="large" class="login-form" @submit.prevent>
          <div class="field-group">
            <label class="field-label">用户名</label>
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
          </div>

          <div class="field-group">
            <label class="field-label">密码</label>
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
          </div>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              @click="handleLogin"
              class="submit-btn"
              size="large"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span class="footer-hint">还没有账号？</span>
          <router-link to="/register" class="footer-link">立即注册</router-link>
        </div>
      </div>
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

/* ========== Design Tokens ========== */
.login-page {
  --navy: #1E40AF;
  --navy-dark: #1E3A5F;
  --blue: #3B82F6;
  --blue-light: #60A5FA;
  --gold: #F59E0B;
  --gold-light: #FBBF24;
  --gold-glow: rgba(245, 158, 11, 0.25);
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
  background: url('https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=1920&q=80') center/cover no-repeat;
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
  mask-image: radial-gradient(ellipse 70% 60% at 30% 50%, black 30%, transparent 70%);
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
  background: rgba(59, 130, 246, 0.12);
  top: -180px;
  left: -120px;
}

.glow-2 {
  width: 400px;
  height: 400px;
  background: var(--gold-glow);
  bottom: -150px;
  right: 20%;
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

/* 装饰线 */
.brand-top-line {
  width: 48px;
  height: 3px;
  background: linear-gradient(90deg, var(--gold), var(--blue));
  border-radius: 2px;
  margin-bottom: 32px;
}

/* 奖杯徽标 */
.brand-medal {
  position: relative;
  width: 64px;
  height: 64px;
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
  margin: 0 0 40px;
  line-height: 1.6;
}

/* 亮点卡片 */
.brand-highlights {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 40px;
}

.highlight-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 18px;
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

.highlight-num {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(59, 130, 246, 0.1));
  border: 1px solid rgba(245, 158, 11, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: var(--gold-light);
}

.highlight-body {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding-top: 2px;
}

.highlight-body strong {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-1);
}

.highlight-body span {
  font-size: 12.5px;
  color: var(--text-3);
  line-height: 1.5;
}

/* 数据统计条 */
.brand-stats {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 20px 24px;
  background: var(--glass);
  backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 14px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 22px;
  font-weight: 800;
  color: var(--gold-light);
  letter-spacing: -0.5px;
}

.stat-label {
  display: block;
  font-size: 11px;
  font-weight: 500;
  color: var(--text-3);
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: var(--glass-border);
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
  width: 520px;
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
  max-width: 400px;
}

@media (prefers-reduced-motion: no-preference) {
  .form-card { animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1); }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-header {
  margin-bottom: 36px;
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
.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.field-group {
  margin-bottom: 4px;
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
  font-size: 15px;
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
  margin-top: 28px;
  padding-top: 24px;
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
    background: url('https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=960&q=80') center/cover no-repeat;
  }
  .bg-overlay {
    background: linear-gradient(135deg, rgba(7, 13, 27, 0.96) 0%, rgba(14, 22, 41, 0.9) 60%, rgba(7, 13, 27, 0.96) 100%);
  }
}

@media (max-width: 480px) {
  .form-panel { padding: 24px; }
  .form-title { font-size: 22px; }
}
</style>
