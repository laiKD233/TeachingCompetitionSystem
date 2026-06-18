# Git 仓库 + 自动部署（本地改完自动上服务器）

下面这套是你现在项目可直接落地的流程：  
本地改代码 -> `git push` -> GitHub Actions 自动 SSH 到服务器执行部署脚本。

## 1. 准备 GitHub 仓库（本地电脑）

如果你还没安装 Git，先安装 Git for Windows。  
然后在本地项目目录执行：

```bash
cd "C:\Users\85845\Desktop\教学竞赛\教学竞赛\teaching-competition"
git init
git branch -M main
git add .
git commit -m "init: teaching competition project"
git remote add origin git@github.com:你的GitHub用户名/你的仓库名.git
git push -u origin main
```

说明：
- 自动部署工作流文件已经在仓库中：`.github/workflows/deploy.yml`
- 以后每次推送 `main` 分支都会触发部署。

## 2. 让服务器能从 GitHub 拉代码

在服务器执行：

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
ssh-keygen -t ed25519 -C "server-deploy-key" -f ~/.ssh/github_deploy -N ""
cat ~/.ssh/github_deploy.pub
```

把输出的公钥复制到 GitHub：
- 仓库 -> `Settings` -> `Deploy keys` -> `Add deploy key`
- 勾选 `Allow read access`

继续在服务器执行：

```bash
cat >> ~/.ssh/config <<'EOF'
Host github.com
  HostName github.com
  User git
  IdentityFile ~/.ssh/github_deploy
  IdentitiesOnly yes
EOF
chmod 600 ~/.ssh/config
ssh -T git@github.com
```

然后把服务器目录切到 Git 管理：

```bash
cd /opt/teaching-competition
git init
git remote remove origin 2>/dev/null || true
git remote add origin git@github.com:你的GitHub用户名/你的仓库名.git
git fetch origin
git reset --hard origin/main
```

## 3. 让 GitHub Actions 能 SSH 到服务器

在你本地电脑生成一对给 CI 用的密钥：

```bash
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ./github_actions_deploy_key -N ""
```

把公钥上传到服务器（允许登录用户，推荐 admin）：

```bash
scp ./github_actions_deploy_key.pub admin@114.55.172.43:~/
ssh admin@114.55.172.43 "mkdir -p ~/.ssh && cat ~/github_actions_deploy_key.pub >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys && rm ~/github_actions_deploy_key.pub"
```

在 GitHub 仓库里加 Secrets：
- `Settings` -> `Secrets and variables` -> `Actions` -> `New repository secret`
- 新建以下两个：
  - `SERVER_HOST` = `114.55.172.43`
  - `SERVER_USER` = `admin`
  - `SERVER_SSH_KEY` = `github_actions_deploy_key` 私钥文件内容（以 `-----BEGIN OPENSSH PRIVATE KEY-----` 开头）

## 4. 测试自动部署

本地改一个小文件后提交：

```bash
git add .
git commit -m "test: auto deploy"
git push
```

然后到 GitHub `Actions` 页面看 `Deploy to Aliyun Server` 是否成功。

## 5. 日常发布命令

以后就这三条：

```bash
git add .
git commit -m "feat: xxx"
git push
```

自动部署失败时，去 GitHub Actions 看日志，再在服务器看：

```bash
sudo systemctl status teaching-competition-backend --no-pager -l
sudo journalctl -u teaching-competition-backend -n 120 --no-pager
```
