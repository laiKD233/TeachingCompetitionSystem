import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
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
          target: env.VITE_DEV_API_TARGET || 'http://localhost:8090',
          changeOrigin: true
        }
      }
    }
  }
})
