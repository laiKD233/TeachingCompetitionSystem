<template>
  <div class="guest-layout">
    <!-- 顶部导航栏 -->
    <nav class="guest-navbar">
      <div class="navbar-container">
        <!-- Logo -->
        <div class="navbar-logo" @click="goHome">
          <el-icon :size="28" color="#FBBF24"><Trophy /></el-icon>
          <span class="logo-text">学科竞赛管理平台</span>
        </div>

        <!-- 导航菜单 -->
        <div class="navbar-menu">
          <router-link to="/guest/home" class="nav-item" active-class="active">首页</router-link>
          <router-link to="/guest/competitions" class="nav-item" active-class="active">竞赛信息</router-link>
          <router-link to="/guest/awards" class="nav-item" active-class="active">往届成果</router-link>
          <a href="#contact" class="nav-item">联系我们</a>
        </div>

        <!-- 右侧按钮 -->
        <div class="navbar-actions">
          <template v-if="isLoggedIn">
            <router-link to="/participant/dashboard" class="btn btn-register">进入系统</router-link>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-login">登录</router-link>
            <router-link to="/register" class="btn btn-register">注册</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- 内容区域 -->
    <main class="guest-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="guest-footer" id="contact">
      <div class="footer-container">
        <div class="footer-section">
          <h3>联系我们</h3>
          <p>邮箱: contact@teaching-competition.com</p>
          <p>电话: 400-123-4567</p>
          <p>地址: 教育科技大厦A座10层</p>
        </div>
        <div class="footer-section">
          <h3>快速链接</h3>
          <router-link to="/guest/competitions">竞赛列表</router-link>
          <router-link to="/guest/awards">往届成果</router-link>
          <router-link to="/login">登录系统</router-link>
        </div>
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>学科竞赛管理平台致力于为教育工作者提供专业化的竞赛管理服务，助力教学创新与人才培养。</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 学科竞赛管理平台 · All Rights Reserved</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

const goHome = () => {
  router.push('/guest/home')
}
</script>

<style scoped>
.guest-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #070D1B;
}

/* ========== 导航栏 ========== */
.guest-navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(14, 22, 41, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 40px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.navbar-logo:hover {
  transform: scale(1.02);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #F1F5F9;
  white-space: nowrap;
}

.navbar-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.nav-item {
  padding: 8px 18px;
  color: rgba(241, 245, 249, 0.7);
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-item:hover {
  color: #F1F5F9;
  background: rgba(255, 255, 255, 0.08);
}

.nav-item.active {
  color: #FBBF24;
  background: rgba(251, 191, 36, 0.1);
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn {
  padding: 10px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-login {
  color: #F1F5F9;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-login:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
}

.btn-register {
  color: #fff;
  background: linear-gradient(135deg, #3B82F6, #1E40AF);
  border: none;
}

.btn-register:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

/* ========== 内容区域 ========== */
.guest-content {
  flex: 1;
  min-height: calc(100vh - 70px - 300px);
}

/* ========== 页脚 ========== */
.guest-footer {
  background: rgba(7, 13, 27, 0.98);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 60px 40px 30px;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 60px;
  margin-bottom: 40px;
}

.footer-section h3 {
  font-size: 16px;
  font-weight: 700;
  color: #FBBF24;
  margin: 0 0 20px;
}

.footer-section p {
  font-size: 14px;
  color: rgba(241, 245, 249, 0.6);
  line-height: 1.8;
  margin: 0 0 10px;
}

.footer-section a {
  display: block;
  font-size: 14px;
  color: rgba(241, 245, 249, 0.6);
  text-decoration: none;
  margin-bottom: 12px;
  transition: color 0.2s ease;
}

.footer-section a:hover {
  color: #60A5FA;
}

.footer-bottom {
  max-width: 1400px;
  margin: 0 auto;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  text-align: center;
}

.footer-bottom p {
  font-size: 13px;
  color: rgba(241, 245, 249, 0.4);
  margin: 0;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .navbar-container {
    padding: 0 24px;
    gap: 24px;
  }
  
  .navbar-menu {
    gap: 4px;
  }
  
  .nav-item {
    padding: 8px 14px;
    font-size: 14px;
  }
  
  .footer-container {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    flex-wrap: wrap;
    height: auto;
    padding: 16px 20px;
    gap: 16px;
  }
  
  .navbar-menu {
    order: 3;
    width: 100%;
    justify-content: space-between;
  }
  
  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .btn {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .guest-footer {
    padding: 40px 20px 20px;
  }
}
</style>
