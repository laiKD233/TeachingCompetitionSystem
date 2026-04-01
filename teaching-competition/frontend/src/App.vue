<template>
  <router-view />
</template>

<script setup>
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

:root {
  /* 主色调 */
  --primary-500: #3B82F6;
  --primary-600: #2563EB;
  --primary-700: #1D4ED8;
  --primary-800: #1E40AF;
  --primary-900: #1E3A8A;
  
  /* 辅助色 */
  --secondary-400: #A78BFA;
  --secondary-500: #8B5CF6;
  --secondary-600: #7C3AED;
  
  /* 成功/警告/错误 */
  --success-500: #10B981;
  --success-600: #059669;
  --warning-500: #F59E0B;
  --warning-600: #D97706;
  --error-500: #EF4444;
  --error-600: #DC2626;
  
  /* 中性色 */
  --slate-50: #F8FAFC;
  --slate-100: #F1F5F9;
  --slate-200: #E2E8F0;
  --slate-300: #CBD5E1;
  --slate-400: #94A3B8;
  --slate-500: #64748B;
  --slate-600: #475569;
  --slate-700: #334155;
  --slate-800: #1E293B;
  --slate-900: #0F172A;
  
  /* 背景色 */
  --bg-primary: #F8FAFC;
  --bg-secondary: #F1F5F9;
  --bg-tertiary: #E2E8F0;
  
  /* 文字色 */
  --text-primary: #0F172A;
  --text-secondary: #475569;
  --text-tertiary: #64748B;
  --text-white: #FFFFFF;
  
  /* 边框 */
  --border-light: rgba(226, 232, 240, 0.8);
  --border-medium: #CBD5E1;
  --border-dark: #94A3B8;
  
  /* 阴影 */
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  --shadow-2xl: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  
  /* 圆角 */
  --radius-sm: 6px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-2xl: 24px;
  
  /* 过渡动画 */
  --transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-normal: 250ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: 350ms cubic-bezier(0.4, 0, 0.2, 1);
  
  /* 兼容性别名 (与 common.css 统一) */
  --bg-white: #FFFFFF;
  --bg-light: #F1F5F9;
  --primary-color: #2563EB;
  --primary-gradient: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
  --secondary-gradient: linear-gradient(135deg, #60A5FA 0%, #2563EB 100%);
  --border-color: #CBD5E1;
  --text-placeholder: #94A3B8;
  --success-color: #059669;
  --warning-color: #D97706;
  --danger-color: #DC2626;
  --info-color: #64748B;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: 'Inter', 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-primary);
  background-color: var(--bg-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  height: 100vh;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--bg-secondary);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: var(--slate-400);
  border-radius: 4px;
  border: 2px solid var(--bg-secondary);
}

::-webkit-scrollbar-thumb:hover {
  background: var(--slate-500);
}

/* 焦点样式 */
:focus-visible {
  outline: 2px solid var(--primary-500);
  outline-offset: 2px;
}

/* 链接样式 */
a {
  color: var(--primary-500);
  text-decoration: none;
  transition: color var(--transition-fast);
}

a:hover {
  color: var(--primary-600);
}

/* 全局动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-fast);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active {
  transition: transform var(--transition-normal), opacity var(--transition-normal);
}

.slide-up-enter-from {
  transform: translateY(20px);
  opacity: 0;
}
</style>
