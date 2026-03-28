import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {// 配置代理,解决跨域问题
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
