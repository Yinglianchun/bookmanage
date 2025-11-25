# 代码同步指引

以下步骤帮助你确认远程地址并同步最新提交到本地：

## 1. 确认远程地址
在仓库根目录执行：
```bash
git remote -v
```
若显示你的仓库地址（例如 `https://github.com/Yinglianchun/bookmanage.git`），则已配置正确。

## 2. 获取远程更新
```bash
git fetch origin
```
这会拉取远程分支最新信息（不合并）。

## 3. 切换到目标分支
```bash
git checkout work
```
若本地不存在该分支，可使用 `git checkout -b work origin/work` 创建并追踪远程分支。

## 4. 合并远程更新
```bash
git pull origin work
```
将远程 `work` 分支的最新提交同步到本地。

## 5. 推送本地修改（如有）
完成本地开发后，通过以下命令推送到远程：
```bash
git push origin work
```
如遇权限或认证问题，请确认已配置 SSH/HTTPS 凭据。

