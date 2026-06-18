<template>
  <div class="guest-home">
    <!-- 动态背景 -->
    <div class="bg-layer">
      <div class="bg-image"></div>
      <div class="bg-overlay"></div>
      <div class="bg-grid"></div>
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
    </div>

    <!-- 轮播图区域 -->
    <div class="carousel-section">
      <el-carousel :interval="4000" height="400px" indicator-position="bottom">
        <el-carousel-item v-for="(img, index) in carouselImages" :key="index">
          <img :src="img.url" :alt="img.alt" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 品牌展示区 -->
    <div class="brand-content">
      <div class="brand-inner">
        <!-- 装饰顶线 -->
        <div class="brand-top-line"></div>

        <!-- 奖杯徽标 -->
        <div class="brand-medal">
          <div class="medal-ring"></div>
          <el-icon :size="32" color="#FBBF24"><Trophy /></el-icon>
        </div>

        <h1 class="brand-title">学科竞赛管理平台</h1>
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

        <!-- 行动按钮 -->
        <div class="brand-actions">
          <router-link to="/guest/competitions" class="action-btn btn-primary">
            浏览竞赛
          </router-link>
          <router-link to="/register" class="action-btn btn-secondary">
            立即注册
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const carouselImages = [
  {
    url: new URL('@/assets/R-C.png', import.meta.url).href,
    alt: '学科竞赛'
  },
  {
    url: new URL('@/assets/OIP-C.webp', import.meta.url).href,
    alt: '课堂教学'
  },
  {
    url: new URL('@/assets/v2-93becc709e52027d26971d1660c38c6d_r.jpg', import.meta.url).href,
    alt: '颁奖典礼'
  }
]
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

.guest-home {
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

  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: var(--bg-deep);
}

/* ========== 背景 ========== */
.bg-layer {
  position: fixed;
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

/* ========== 轮播图 ========== */
.carousel-section {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 40px auto 0;
  padding: 0 20px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: fill;
}

:deep(.el-carousel__item) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-carousel__container) {
  border-radius: 16px;
}

:deep(.el-carousel__button) {
  background-color: var(--blue);
  opacity: 0.5;
}

:deep(.el-carousel__indicator.is-active button) {
  opacity: 1;
  background-color: var(--gold-light);
}

/* ========== 品牌内容区 ========== */
.brand-content {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 60px 40px;
}

.brand-inner {
  max-width: 700px;
  width: 100%;
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 装饰线 */
.brand-top-line {
  width: 48px;
  height: 3px;
  background: linear-gradient(90deg, var(--gold), var(--blue));
  border-radius: 2px;
  margin: 0 auto 32px;
}

/* 奖杯徽标 */
.brand-medal {
  position: relative;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 28px;
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
  font-size: 42px;
  font-weight: 800;
  color: var(--text-1);
  margin: 0 0 4px;
  letter-spacing: -0.5px;
  line-height: 1.15;
  text-align: center;
}

.brand-tagline {
  font-size: 13px;
  font-weight: 600;
  color: var(--blue-light);
  text-transform: uppercase;
  letter-spacing: 2px;
  margin: 0 0 16px;
  text-align: center;
}

.brand-desc {
  font-size: 16px;
  color: var(--text-2);
  margin: 0 0 48px;
  line-height: 1.6;
  text-align: center;
}

/* 亮点卡片 */
.brand-highlights {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 48px;
}

.highlight-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 18px 20px;
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
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(59, 130, 246, 0.1));
  border: 1px solid rgba(245, 158, 11, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: var(--gold-light);
}

.highlight-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 2px;
}

.highlight-body strong {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-1);
}

.highlight-body span {
  font-size: 13.5px;
  color: var(--text-3);
  line-height: 1.5;
}

/* 数据统计条 */
.brand-stats {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 24px 28px;
  background: var(--glass);
  backdrop-filter: blur(12px);
  border: 1px solid var(--glass-border);
  border-radius: 14px;
  margin-bottom: 40px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 26px;
  font-weight: 800;
  color: var(--gold-light);
  letter-spacing: -0.5px;
}

.stat-label {
  display: block;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-3);
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--glass-border);
}

/* 行动按钮 */
.brand-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.action-btn {
  padding: 14px 36px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 700;
  text-decoration: none;
  transition: all var(--ease);
  letter-spacing: 1px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--blue), var(--navy));
  color: #fff;
  border: none;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.4);
}

.btn-secondary {
  background: transparent;
  color: var(--gold-light);
  border: 2px solid var(--gold);
}

.btn-secondary:hover {
  background: rgba(245, 158, 11, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(245, 158, 11, 0.3);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .brand-title {
    font-size: 32px;
  }
  
  .brand-desc {
    font-size: 14px;
  }
  
  .brand-content {
    padding: 40px 20px;
  }
  
  .brand-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
    text-align: center;
  }
  
  .brand-stats {
    padding: 20px;
  }
  
  .stat-value {
    font-size: 22px;
  }
}

@media (max-width: 480px) {
  .brand-title {
    font-size: 28px;
  }
  
  .highlight-card {
    padding: 14px 16px;
  }
}
</style>
