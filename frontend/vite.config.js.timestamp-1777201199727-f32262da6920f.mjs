// vite.config.js
import { defineConfig } from "file:///C:/Users/31672/OneDrive/%E6%A1%8C%E9%9D%A2/TeachingCompetitionSystem/%E6%95%99%E5%AD%A6%E7%AB%9E%E8%B5%9B%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/teaching-competition/frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///C:/Users/31672/OneDrive/%E6%A1%8C%E9%9D%A2/TeachingCompetitionSystem/%E6%95%99%E5%AD%A6%E7%AB%9E%E8%B5%9B%E7%AE%A1%E7%90%86%E7%B3%BB%E7%BB%9F/teaching-competition/frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import { resolve } from "path";
var __vite_injected_original_dirname = "C:\\Users\\31672\\OneDrive\\\u684C\u9762\\TeachingCompetitionSystem\\\u6559\u5B66\u7ADE\u8D5B\u7BA1\u7406\u7CFB\u7EDF\\teaching-competition\\frontend";
var vite_config_default = defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": resolve(__vite_injected_original_dirname, "src")
    }
  },
  server: {
    port: 3e3,
    proxy: {
      // 配置代理,解决跨域问题
      "/api": {
        target: "http://localhost:8090",
        changeOrigin: true
      }
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcuanMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJDOlxcXFxVc2Vyc1xcXFwzMTY3MlxcXFxPbmVEcml2ZVxcXFxcdTY4NENcdTk3NjJcXFxcVGVhY2hpbmdDb21wZXRpdGlvblN5c3RlbVxcXFxcdTY1NTlcdTVCNjZcdTdBREVcdThENUJcdTdCQTFcdTc0MDZcdTdDRkJcdTdFREZcXFxcdGVhY2hpbmctY29tcGV0aXRpb25cXFxcZnJvbnRlbmRcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkM6XFxcXFVzZXJzXFxcXDMxNjcyXFxcXE9uZURyaXZlXFxcXFx1Njg0Q1x1OTc2MlxcXFxUZWFjaGluZ0NvbXBldGl0aW9uU3lzdGVtXFxcXFx1NjU1OVx1NUI2Nlx1N0FERVx1OEQ1Qlx1N0JBMVx1NzQwNlx1N0NGQlx1N0VERlxcXFx0ZWFjaGluZy1jb21wZXRpdGlvblxcXFxmcm9udGVuZFxcXFx2aXRlLmNvbmZpZy5qc1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vQzovVXNlcnMvMzE2NzIvT25lRHJpdmUvJUU2JUExJThDJUU5JTlEJUEyL1RlYWNoaW5nQ29tcGV0aXRpb25TeXN0ZW0vJUU2JTk1JTk5JUU1JUFEJUE2JUU3JUFCJTlFJUU4JUI1JTlCJUU3JUFFJUExJUU3JTkwJTg2JUU3JUIzJUJCJUU3JUJCJTlGL3RlYWNoaW5nLWNvbXBldGl0aW9uL2Zyb250ZW5kL3ZpdGUuY29uZmlnLmpzXCI7aW1wb3J0IHsgZGVmaW5lQ29uZmlnIH0gZnJvbSAndml0ZSdcclxuaW1wb3J0IHZ1ZSBmcm9tICdAdml0ZWpzL3BsdWdpbi12dWUnXHJcbmltcG9ydCB7IHJlc29sdmUgfSBmcm9tICdwYXRoJ1xyXG5cclxuZXhwb3J0IGRlZmF1bHQgZGVmaW5lQ29uZmlnKHtcclxuICBwbHVnaW5zOiBbdnVlKCldLFxyXG4gIHJlc29sdmU6IHtcclxuICAgIGFsaWFzOiB7XHJcbiAgICAgICdAJzogcmVzb2x2ZShfX2Rpcm5hbWUsICdzcmMnKVxyXG4gICAgfVxyXG4gIH0sXHJcbiAgc2VydmVyOiB7XHJcbiAgICBwb3J0OiAzMDAwLFxyXG4gICAgcHJveHk6IHsvLyBcdTkxNERcdTdGNkVcdTRFRTNcdTc0MDYsXHU4OUUzXHU1MUIzXHU4REU4XHU1N0RGXHU5NUVFXHU5ODk4XHJcbiAgICAgICcvYXBpJzoge1xyXG4gICAgICAgIHRhcmdldDogJ2h0dHA6Ly9sb2NhbGhvc3Q6ODA5MCcsXHJcbiAgICAgICAgY2hhbmdlT3JpZ2luOiB0cnVlXHJcbiAgICAgIH1cclxuICAgIH1cclxuICB9XHJcbn0pXHJcbiJdLAogICJtYXBwaW5ncyI6ICI7QUFBcWlCLFNBQVMsb0JBQW9CO0FBQ2xrQixPQUFPLFNBQVM7QUFDaEIsU0FBUyxlQUFlO0FBRnhCLElBQU0sbUNBQW1DO0FBSXpDLElBQU8sc0JBQVEsYUFBYTtBQUFBLEVBQzFCLFNBQVMsQ0FBQyxJQUFJLENBQUM7QUFBQSxFQUNmLFNBQVM7QUFBQSxJQUNQLE9BQU87QUFBQSxNQUNMLEtBQUssUUFBUSxrQ0FBVyxLQUFLO0FBQUEsSUFDL0I7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUE7QUFBQSxNQUNMLFFBQVE7QUFBQSxRQUNOLFFBQVE7QUFBQSxRQUNSLGNBQWM7QUFBQSxNQUNoQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQ0YsQ0FBQzsiLAogICJuYW1lcyI6IFtdCn0K
