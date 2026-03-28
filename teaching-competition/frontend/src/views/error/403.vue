<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-icon">
        <el-icon :size="80"><Lock /></el-icon>
      </div>
      
      <h1 class="error-title">403</h1>
      <h2 class="error-subtitle">权限不足</h2>
      <p class="error-desc">抱歉，您没有访问此页面的权限</p>
      
      <div class="error-actions">
        <el-button type="primary" @click="goBack" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回上一页
        </el-button>
        <el-button @click="goHome" size="large">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button type="warning" @click="goLogin" size="large">
          <el-icon><User /></el-icon>
          重新登录
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

const goLogin = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.error-container {
  text-align: center;
  padding: 60px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
}

.error-icon {
  margin-bottom: 30px;
}

.error-icon :deep(.el-icon) {
  color: #ff6b6b;
}

.error-title {
  font-size: 80px;
  font-weight: 700;
  color: #ff6b6b;
  margin-bottom: 10px;
}

.error-subtitle {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.error-desc {
  font-size: 16px;
  color: #606266;
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 40px;
}

.error-tips {
  text-align: left;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 12px;
  font-size: 14px;
  color: #606266;
}

.error-tips p {
  font-weight: 600;
  margin-bottom: 12px;
}

.error-tips ul {
  padding-left: 20px;
  margin: 0;
}

.error-tips li {
  margin-bottom: 8px;
  line-height: 1.6;
}
</style>
