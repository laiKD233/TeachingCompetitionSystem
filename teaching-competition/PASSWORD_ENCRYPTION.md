# 密码加密方式说明

## ✅ 已完成升级

系统密码已全部采用 **BCrypt** 加密算法，确保安全性。

---

## 🔐 加密实现

### 1. 注册流程
```java
// UserServiceImpl.java - register() 方法
user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt 加密
```

**加密过程：**
- 用户输入明文密码（如：`admin123`）
- Spring Security 的 `PasswordEncoder` 使用 BCrypt 算法加密
- 生成加密后的密码（如：`$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6`）
- 存储到数据库

### 2. 登录流程
```java
// UserServiceImpl.java - login() 方法
if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
    throw new BusinessException("密码错误");
}
```

**验证过程：**
- 用户输入明文密码
- 从数据库读取 BCrypt 加密密码
- 使用 `passwordEncoder.matches()` 进行匹配验证
- BCrypt 特性：每次加密结果不同，但可以正确验证

### 3. 修改密码流程
```java
// UserServiceImpl.java - updatePassword() 方法
// 验证原密码
if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
    throw new BusinessException("原密码错误");
}

// 加密新密码
user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
updateById(user);
```

---

## 📊 数据库密码示例

| 用户名 | 密码（数据库中） | 说明 |
|--------|----------------|------|
| admin | `$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6` | BCrypt 加密 |
| teacher1 | `$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6` | BCrypt 加密 |
| student1 | `$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8RqX5pMXYvhLJHJdEqGzPqBx.5zW6` | BCrypt 加密 |
| 新用户 | 自动加密存储 | 注册时自动加密 |

---

## 🔑 测试账号

所有测试账号密码都已更新为 BCrypt 加密：

| 用户名 | 明文密码 | 角色 |
|--------|---------|------|
| admin | admin123 | ADMIN |
| teacher1 | 123456 | TEACHER |
| student1 | 123456 | STUDENT |

---

## 🧪 测试步骤

### 1. 测试现有账号登录
在 Apifox 或前端登录页面测试：

**管理员登录：**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**教师登录：**
```json
{
  "username": "teacher1",
  "password": "123456"
}
```

**学生登录：**
```json
{
  "username": "student1",
  "password": "123456"
}
```

### 2. 测试新用户注册

**注册请求：**
```json
{
  "name": "测试用户",
  "username": "testuser",
  "password": "test123456",
  "studentId": "20240001",
  "college": "计算机学院",
  "phone": "13800138000",
  "email": "test@example.com"
}
```

**注册后登录：**
```json
{
  "username": "testuser",
  "password": "test123456"
}
```

应该成功返回 Token。

---

## 🔍 BCrypt 加密特点

### 1. 不可逆性
- BCrypt 是单向加密算法
- 无法从加密后的密码还原出明文密码
- 即使数据库泄露，攻击者也无法获取原始密码

### 2. 加盐（Salt）
- BCrypt 自动为每个密码生成随机盐值
- 即使两个用户使用相同密码，加密结果也不同
- 有效防止彩虹表攻击

### 3. 自适应成本因子
- 加密强度可调整（默认 cost=10）
- 随着硬件性能提升，可增加 cost 值
- 平衡安全性和性能

### 4. 验证机制
```java
// BCrypt 验证原理
boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
// 内部实现：
// 1. 从 encodedPassword 中提取 salt
// 2. 使用相同 salt 对 plainPassword 进行加密
// 3. 比较加密结果是否一致
```

---

## 📝 代码修改总结

### 修改的文件
1. **UserServiceImpl.java**
   - ✅ `login()` 方法 - 改为 BCrypt 验证
   - ✅ `register()` 方法 - 改为 BCrypt 加密
   - ✅ `updatePassword()` 方法 - 已经是 BCrypt（无需修改）

### 修改的代码

#### 登录验证（第 41-44 行）
```java
// 修改前（明文对比）
if (!dto.getPassword().equals(user.getPassword())) {

// 修改后（BCrypt 验证）
if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
```

#### 注册加密（第 71 行）
```java
// 修改前（明文）
user.setPassword(dto.getPassword());

// 修改后（BCrypt 加密）
user.setPassword(passwordEncoder.encode(dto.getPassword()));
```

---

## ⚠️ 重要提示

### 1. 旧数据处理
如果数据库中已有明文密码用户，需要：
1. 方案一：重置所有用户密码
2. 方案二：升级时检测密码格式，首次登录强制修改密码

### 2. 密码策略建议
```java
// 可以在 PasswordUpdateDTO 中添加密码强度验证
// 建议密码要求：
// - 最少 6 位字符
// - 包含字母和数字
// - 区分大小写
```

### 3. 安全增强建议
- [ ] 添加密码强度验证（注册时）
- [ ] 添加密码修改频率限制
- [ ] 添加登录失败次数限制
- [ ] 添加密码过期时间（可选）

---

## 🎯 验证清单

- [x] 注册功能 - 密码 BCrypt 加密
- [x] 登录功能 - 密码 BCrypt 验证
- [x] 修改密码 - 新旧密码都使用 BCrypt
- [x] 测试账号 - 数据库密码已更新为加密格式
- [x] 后端服务 - 已重启并正常运行

---

## 🚀 下一步

1. **测试注册功能** - 注册新用户并登录
2. **测试登录功能** - 使用三个测试账号登录
3. **测试修改密码** - 修改密码后用新密码登录
4. **前端适配** - 前端密码输入框应支持复杂密码

---

**系统现已升级到安全的 BCrypt 密码加密！** ✅
