# 阿里云轻量服务器部署指南

## 1. 服务器准备

1. 系统建议 Ubuntu 22.04。
2. 安全组/防火墙放行端口：`22`、`80`、`443`。
3. 不要把 `3306`、`6379` 直接暴露公网。

## 2. 安装运行环境

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven nginx mysql-server redis-server git
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs
```

## 3. 上传代码并放到固定目录

```bash
sudo mkdir -p /opt/teaching-competition
sudo chown -R $USER:$USER /opt/teaching-competition
cd /opt/teaching-competition
git clone <你的仓库地址> .
```

## 4. 配置后端环境变量

1. 复制模板：

```bash
cp .env.example .env
```

2. 修改 `.env` 关键项：

- `SPRING_PROFILES_ACTIVE=prod`
- `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`
- `REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`
- `JWT_SECRET`
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS`（例如 `https://your-domain.com`）

## 5. 初始化数据库

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS teaching_competition CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -uroot -p teaching_competition < backend/src/main/resources/database.sql
```

## 6. 一键部署

```bash
chmod +x deploy/scripts/deploy-server.sh
sudo bash deploy/scripts/deploy-server.sh
```

## 7. 验证

1. 后端健康检查：

```bash
curl http://127.0.0.1:8090/api/auth/login
```

2. 浏览器访问：

- `http://服务器公网IP`

## 8. 常用运维命令

```bash
sudo systemctl status teaching-competition-backend
sudo journalctl -u teaching-competition-backend -f
sudo systemctl restart teaching-competition-backend
sudo nginx -t
sudo systemctl reload nginx
```
