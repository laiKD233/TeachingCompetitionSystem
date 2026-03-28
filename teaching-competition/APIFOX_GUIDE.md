# Apifox 接口测试集合导入指南

## 📋 方法一：导入 Postman Collection（推荐）

### 步骤 1：打开 Apifox
1. 启动 Apifox 客户端
2. 创建或打开"教学竞赛管理系统"项目

### 步骤 2：导入数据
1. 点击项目左侧菜单的 **"数据管理"** 图标（数据库形状）
2. 点击右上角的 **"导入数据"** 按钮
3. 选择导入类型：**Postman Collection**
4. 选择文件：`apifox-collection.json`
5. 点击 **"确定"** 开始导入

### 步骤 3：验证导入
导入成功后，你会看到：
- ✅ 8 个接口分组（认证、用户、竞赛、报名、作品、评审、奖项、超级管理员）
- ✅ 40+ 个接口请求
- ✅ 自动配置的 Bearer Token 认证
- ✅ 环境变量（base_url, access_token）

---

## 🚀 快速测试流程

### 1️⃣ 先测试登录接口
在 Apifox 中：
1. 展开 **"认证模块"** 文件夹
2. 点击 **"用户登录"** 接口
3. 直接发送请求（默认已配置 admin/admin123）
4. 查看响应结果

**预期响应：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "name": "系统管理员",
    "role": "ADMIN"
  }
}
```

✅ **Token 会自动保存**到环境变量 `access_token` 中！

### 2️⃣ 测试需要认证的接口
登录成功后，所有需要认证的接口都会**自动携带 Token**：

1. 展开 **"用户模块"**
2. 点击 **"获取当前用户信息"**
3. 发送请求
4. 查看响应

**请求头会自动包含：**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🔧 高级用法

### 修改默认账号
如果想用其他账号测试：

1. 点击 **"用户登录"** 接口
2. 切换到 **"Body"** 标签页
3. 修改 JSON 中的用户名和密码：
```json
{
  "username": "teacher1",
  "password": "123456"
}
```
4. 点击 **"保存"**
5. 重新发送登录请求

### 修改 API 地址
如果后端不在 localhost:8080：

1. 点击左侧 **"环境管理"**
2. 编辑 **"Default Environment"**
3. 修改变量 `base_url` 的值
4. 例如改为：`http://192.168.1.100:8080`
5. 所有接口会自动使用新地址

---

## 📊 完整接口清单

### 🔐 认证模块 (3 个)
- ✅ POST /api/auth/login - 用户登录（自动保存 Token）
- ✅ POST /api/auth/register - 用户注册
- ✅ GET /api/auth/logout - 退出登录

### 👤 用户模块 (3 个)
- ✅ GET /api/user/current - 获取当前用户信息
- ✅ PUT /api/user/update - 更新用户信息
- ✅ PUT /api/user/password - 修改密码

### 🏆 竞赛模块 (6 个)
- ✅ GET /api/competition/public/list - 公开竞赛列表
- ✅ GET /api/competition/public/detail/{id} - 竞赛详情
- ✅ GET /api/competition/admin/list - 管理员竞赛列表
- ✅ POST /api/competition - 创建竞赛
- ✅ PUT /api/competition/{id} - 更新竞赛
- ✅ DELETE /api/competition/{id} - 删除竞赛

### 📝 报名模块 (5 个)
- ✅ POST /api/registration - 提交报名
- ✅ GET /api/registration/my - 我的报名
- ✅ PUT /api/registration/{id}/approve - 通过审核
- ✅ PUT /api/registration/reject - 拒绝报名
- ✅ GET /api/registration/admin/list - 管理员查看报名

### 📦 作品模块 (4 个)
- ✅ POST /api/work - 上传作品（支持文件上传）
- ✅ GET /api/work/my - 我的作品
- ✅ GET /api/work/{id} - 作品详情
- ✅ GET /api/work/admin/list - 管理员查看作品

### 🧑‍🏫 评审模块 (4 个)
- ✅ POST /api/review/assign - 分配评审任务
- ✅ POST /api/review/score - 提交评分
- ✅ GET /api/review/my-tasks - 我的评审任务
- ✅ GET /api/review/scores/{competitionId} - 查看成绩

### 🏅 奖项模块 (4 个)
- ✅ POST /api/award/publish - 发布奖项
- ✅ POST /api/award/announcement/{competitionId} - 结果公示
- ✅ GET /api/award/list/{competitionId} - 获奖列表
- ✅ GET /api/award/results/{competitionId} - 成绩结果

### 👑 超级管理员模块 (8 个)
- ✅ GET /api/super-admin/users - 用户列表
- ✅ POST /api/super-admin/users - 添加用户
- ✅ PUT /api/super-admin/users/{id} - 更新用户
- ✅ DELETE /api/super-admin/users/{id} - 删除用户
- ✅ PUT /api/super-admin/users/{id}/freeze - 冻结用户
- ✅ PUT /api/super-admin/users/{id}/unfreeze - 解冻用户
- ✅ PUT /api/super-admin/users/{id}/reset-password - 重置密码
- ✅ GET /api/super-admin/logs - 操作日志

### 📤 文件模块 (2 个)
- ✅ POST /api/file/upload - 文件上传
- ✅ GET /api/file/download/{filename} - 文件下载

---

## 💡 实用技巧

### 1. 使用前置脚本自动生成测试数据
在请求的 **"前置操作"** 中添加：
```javascript
// 生成随机用户名
const randomUsername = 'user_' + Math.random().toString(36).substring(7);
pm.variables.set('random_username', randomUsername);
```

### 2. 使用后置脚本自动验证
在请求的 **"后置操作"** 中添加：
```javascript
// 验证响应状态码
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证返回数据结构
pm.test("Response has correct structure", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('message');
});
```

### 3. 批量运行接口测试
1. 点击左侧 **"自动化测试"**
2. 创建新的测试场景
3. 拖拽需要测试的接口
4. 点击 **"运行"** 批量执行

---

## ⚠️ 常见问题

### Q1: Token 过期怎么办？
**A:** 重新登录即可，Token 会自动覆盖环境变量中的旧值。

### Q2: 如何测试不同角色？
**A:** 
1. 先用 admin 账号登录
2. 在用户管理中创建 teacher 和 student 账号
3. 分别用不同账号登录测试

### Q3: 文件上传失败？
**A:** 
- 确保选择了正确的文件
- 检查文件大小是否超过限制（默认 100MB）
- 确认后端临时目录有写入权限

### Q4: 接口返回 404？
**A:** 
- 检查后端服务是否正常运行
- 确认 base_url 配置正确
- 查看接口路径是否拼写正确

### Q5: 如何调试 SQL？
**A:** 
- 后端 application.yml 已开启 SQL 日志
- 查看控制台输出的 SQL 语句
- 可以用 Navicat 等工具直接查询数据库

---

## 📚 下一步

1. ✅ 完成所有接口的功能测试
2. ✅ 创建自动化测试场景
3. ✅ 生成接口测试报告
4. ✅ 导出测试报告分享给团队

---

## 🎯 测试建议顺序

1. **基础功能测试**
   - 登录 → 获取用户信息 → 更新用户信息
   
2. **核心业务流程**
   - 创建竞赛 → 提交报名 → 审核通过 → 上传作品 → 分配评审 → 提交评分 → 发布奖项

3. **管理功能测试**
   - 用户管理 → 竞赛管理 → 报名管理 → 评审管理

---

**祝你测试顺利！** 🎉

如有问题，请查看 PROJECT_STATUS.md 获取更多文档。
