<template>
  <div class="error-page">
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    <div class="bg-mesh"></div>

    <div class="error-card">
      <div class="error-visual">
        <div class="error-number-ring">
          <span class="error-number">404</span>
        </div>
        <div class="error-icon-wrap">
          <el-icon :size="36"><CircleClose /></el-icon>
        </div>
      </div>
      <h2 class="error-subtitle">页面未找到</h2>
      <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>

      <div class="error-actions">
        <el-button type="primary" @click="goBack" size="large" class="action-primary">
          <el-icon><ArrowLeft /></el-icon>
          返回上一页
        </el-button>
        <el-button @click="goHome" size="large" class="action-default">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/guest/competitions')
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(160deg, #0f172a 0%, #1e293b 40%, #334155 100%);
  position: relative;
  overflow: hidden;
}

.bg-mesh {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 30% 40%, rgba(37, 99, 235, 0.06) 0%, transparent 50%),
    radial-gradient(circle at 70% 60%, rgba(96, 165, 250, 0.06) 0%, transparent 50%);
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
  animation: float 8s ease-in-out infinite;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
}

.shape-2 {
  width: 200px;
  height: 200px;
  bottom: -80px;
  left: -60px;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(6deg); }
}

.error-card {
  text-align: center;
  padding: 60px 48px;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  max-width: 480px;
  animation: cardEnter 0.6s ease-out;
}

@keyframes cardEnter {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.error-visual {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.error-number-ring {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid rgba(37, 99, 235, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse-ring 3s ease-in-out infinite;
}

@keyframes pulse-ring {
  0%, 100% { box-shadow: 0 0 0 0 rgba(37, 99, 235, 0.15); }
  50% { box-shadow: 0 0 0 16px rgba(37, 99, 235, 0); }
}

.error-number {
  font-size: 56px;
  font-weight: 800;
  background: linear-gradient(135deg, var(--primary-400), var(--primary-700));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -2px;
}

.error-icon-wrap {
  position: absolute;
  bottom: -8px;
  right: calc(50% - 70px);
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(37, 99, 235, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-color);
}

.error-subtitle {
  font-size: 22px;
  font-weight: 600;
  color: #e2e8f0;
  margin-bottom: 12px;
}

.error-desc {
  font-size: 15px;
  color: #94a3b8;
  margin-bottom: 36px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.action-primary {
  padding: 12px 28px;
  border-radius: var(--radius-md);
  font-weight: 600;
  background: var(--primary-gradient) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.35);
  transition: all 0.3s ease;
}

.action-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.5) !important;
}

.action-default {
  padding: 12px 28px;
  border-radius: var(--radius-md);
  font-weight: 600;
  background: rgba(255, 255, 255, 0.06) !important;
  border: 1px solid rgba(255, 255, 255, 0.12) !important;
  color: #e2e8f0 !important;
  transition: all 0.3s ease;
}

.action-default:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  transform: translateY(-2px);
}

@media (max-width: 576px) {
  .error-card {
    padding: 40px 24px;
    margin: 0 16px;
  }
  .error-actions {
    flex-direction: column;
  }
}
</style>
