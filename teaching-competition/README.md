# 教学竞赛管理系统 - 快速启动指南

## 📋 项目概述

这是一个完整的教学竞赛管理系统，包含前后端分离的完整实现。

### 技术栈
- **后端**: Spring Boot 3.2.5 + MyBatis-Plus + MySQL + JWT
- **前端**: Vue 3 + Vite + Element Plus + Pinia

---

## 🚀 快速启动

### 第一步：启动后端服务

1. **打开终端，进入后端目录**
```bash
cd backend
```

2. **启动 Spring Boot 应用**
```bash
mvn spring-boot:run
```

3. **验证后端启动成功**
- 访问：http://localhost:8080/api/auth/login
- 看到登录接口即表示成功

### 第二步：安装并启动前端

1. **打开新终端，进入前端目录**
```bash
cd frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

4. **访问系统**
- 浏览器打开：http://localhost:3000
- 使用测试账号登录

---

## 👥 测试账号

| 用户名 | 密码 | 角色 | 功能权限 |
|--------|------|------|---------|
| admin | admin | 超级管理员 | 所有功能 + 用户管理 |
| teacher1 | 123456 | 教师/评审 | 竞赛管理、报名审核、评审分配 |
| student1 | 123456 | 学生 | 报名参赛、提交作品、查看成绩 |

---

## 📁 项目结构

### 后端目录结构
```
backend/
├── src/main/java/com/teaching/competition/
│   ├── controller/          # API 控制器
│   ├── service/             # 业务逻辑层
│   │   └── impl/           # Service 实现
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   ├── dto/                 # 数据传输对象
│   ├── vo/                  # 视图对象
│   ├── common/              # 通用类和枚举
│   ├── config/              # 配置类
│   ├── security/            # 安全相关
│   └── exception/           # 异常处理
└── src/main/resources/
    ├── application.yml      # 配置文件
    └── schema.sql           # 数据库初始化脚本
```

### 前端目录结构
```
frontend/
├── src/
│   ├── api/                 # API 接口定义
│   ├── router/              # 路由配置
│   ├── stores/              # Pinia 状态管理
│   ├── utils/               # 工具函数
│   ├── views/               # 页面组件
│   │   ├── guest/          # 访客页面
│   │   ├── participant/    # 参赛者页面
│   │   ├── admin/          # 管理员页面
│   │   └── super-admin/    # 超级管理员页面
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── package.json             # 依赖配置
├── vite.config.js           # Vite 配置
└── index.html               # HTML 模板
```

---

## 🔧 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teaching_competition?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的 MySQL 密码
    type: com.alibaba.druid.pool.DruidDataSource
```

### 前端配置 (vite.config.js)

```javascript
// 代理配置，将所有以 /api 开头的请求代理到后端服务器，防止跨域
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

---

## 🗄️ 数据库初始化

如果数据库未初始化，执行以下命令：

```bash
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS teaching_competition CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p123456 --default-character-set=utf8mb4 teaching_competition -e "source backend/src/main/resources/schema.sql"
```

---

## 🌟 核心功能模块

### 1. 认证授权模块 ✅
- [x] 用户登录（JWT Token）
- [x] 用户注册
- [x] 基于角色的权限控制

### 2. 用户管理模块 ✅
- [x] 个人信息管理
- [x] 修改密码
- [x] 用户列表（超级管理员）

### 3. 竞赛管理模块 ✅
- [x] 发布竞赛
- [x] 编辑/删除竞赛
- [x] 竞赛列表查询
- [x] 竞赛详情查看

### 4. 报名管理模块 ✅
- [x] 在线报名
- [x] 报名审核
- [x] 报名状态查询

### 5. 作品管理模块 ✅
- [x] 上传作品
- [x] 查看作品
- [x] 下载作品

### 6. 评审管理模块 ✅
- [x] 分配评审任务
- [x] 在线评分
- [x] 评审意见填写

### 7. 成绩公示模块 ✅
- [x] 成绩统计
- [x] 获奖名单生成
- [x] 成绩公示

### 8. 系统管理模块 ✅
- [x] 操作日志
- [x] 用户管理

---

## 📊 API 接口文档

### 使用 Apifox 测试

1. **导入测试集合**
   - 打开 Apifox
   - 项目 → 导入数据 → Postman Collection
   - 选择 `apifox-collection.json` 文件

2. **快速测试**
   - 先执行"用户登录"接口
   - 后续接口会自动携带 Token
   - 按模块测试各个功能

详细使用方法见：`APIFOX_GUIDE.md`

---

## 🐛 常见问题

### 1. 后端启动失败

**问题**: 端口 8080 被占用  
**解决**: 
```bash
# Windows
netstat -ano | findstr :8080
taskkill /F /PID <进程 ID>
```

**问题**: 数据库连接失败  
**解决**: 检查 MySQL 是否启动，修改 `application.yml` 中的数据库密码

### 2. 前端无法访问后端

**问题**: 跨域错误  
**解决**: 检查 `vite.config.js` 中的代理配置是否正确

**问题**: 401 未授权  
**解决**: Token 过期或无效，重新登录即可

### 3. 登录失败

**问题**: 密码错误  
**解决**: jwt暗文密码，Redis里的确认

---

## 🎯 下一步完善建议

### 前端需要完善的页面（当前为框架）

1. **参赛者页面** (`src/views/participant/`)
   - Dashboard.vue - 数据统计和快捷入口
   - Registrations.vue - 报名列表和状态
   - Works.vue - 作品上传和管理
   - Scores.vue - 成绩查询

2. **管理员页面** (`src/views/admin/`)
   - Competitions.vue - 竞赛 CRUD 操作
   - Registrations.vue - 报名审核表格
   - Reviews.vue - 评审分配界面
   - Results.vue - 成绩统计图表

3. **超级管理员页面** (`src/views/super-admin/`)
   - Users.vue - 用户管理表格
   - Logs.vue - 操作日志列表

### 后端可以增强的功能

1. **文件上传** - 完善作品文件上传功能
2. **邮件通知** - 报名结果、评审邀请等
3. **Excel 导出** - 成绩单、报名表导出
4. **图表统计** - 各类数据统计分析

---

## 📝 开发指南

### 前端开发

1. **添加新页面**
   - 在 `src/views/` 下创建组件
   - 在 `src/router/index.js` 中添加路由
   - 在 `MainLayout.vue` 中添加菜单项

2. **调用 API**
   - 在 `src/api/` 下创建 API 模块
   - 使用 `request` 工具发送请求
   - 处理响应和错误

3. **状态管理**
   - 在 `src/stores/` 下创建 Pinia store
   - 在组件中使用 `useStore()` 访问状态

### 后端开发

1. **添加新接口**
   - Controller 层添加方法
   - Service 层实现业务逻辑
   - Mapper 层编写 SQL（或使用 MyBatis-Plus 自带方法）

2. **统一响应格式**
   - 使用 `Result<T>` 包装返回值
   - 使用 `PageResult<T>` 包装分页数据

3. **异常处理**
   - 使用 `BusinessException` 抛出业务异常
   - 全局异常处理器会统一捕获

---

## 📞 技术支持

如有问题，请检查：
1. 后端日志（控制台输出）
2. 前端浏览器控制台（F12）
3. 数据库连接状态
4. API 请求响应（Network 标签）

---

## 🎉 完成状态

✅ 后端完整实现（所有 Controller、Service、Mapper）  
✅ 数据库表结构和初始数据  
✅ 前端基础架构（路由、状态管理、API 封装）  
✅ 登录注册功能完整可用  
✅ 主布局和导航菜单  
✅ 所有页面框架已创建  
🔲 各业务页面详细实现（待完成）  

---

**祝使用愉快！** 🚀
