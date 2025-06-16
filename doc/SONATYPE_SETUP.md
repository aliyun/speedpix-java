# Sonatype OSSRH 凭证配置指南

## 问题诊断
你遇到的 401 Unauthorized 错误是因为：
1. ✅ **Mirror 配置已修复** - 移除了对 Sonatype 的镜像拦截
2. ❌ **需要配置正确的 Sonatype 凭证**

## 获取 Sonatype OSSRH 凭证

### 1. 注册 Sonatype OSSRH 账号
如果还没有账号，请访问：https://issues.sonatype.org/secure/Signup!default.jspa

### 2. 创建 JIRA Issue 申请 GroupId
- 登录后创建一个新的 Issue
- Project: Community Support - Open Source Project Repository Hosting (OSSRH)
- Issue Type: New Project
- Summary: Request for io.github.speedpix groupId
- Group Id: io.github.speedpix
- Project URL: https://github.com/speedpix/speedpix-java （请替换为你的实际 GitHub 仓库）
- SCM URL: https://github.com/speedpix/speedpix-java.git

### 3. 获取用户 Token（推荐）
建议使用 User Token 而不是密码：
1. 登录 https://s01.oss.sonatype.org/
2. 点击右上角用户名 -> Profile
3. 在 User Token 部分点击 "Access User Token"
4. 复制显示的用户名和密码

## 配置 settings.xml

将 ~/.m2/settings.xml 中的 ossrh 服务器配置替换为：

```xml
<server>
    <id>ossrh</id>
    <username>你的用户token用户名</username>
    <password>你的用户token密码</password>
</server>
```

## 验证步骤

1. **确认 JIRA Issue 已批准**
   - 你的 GroupId 申请必须先被 Sonatype 团队批准

2. **测试连接**
   ```bash
   mvn clean deploy -Dgpg.skip=true
   ```

3. **完整部署**
   ```bash
   mvn clean deploy
   ```

## 常见问题

### Q: 如何知道我的 GroupId 申请是否已批准？
A: 检查你的 JIRA Issue 状态，会收到 Sonatype 团队的回复确认。

### Q: 我应该使用密码还是 User Token？
A: 强烈建议使用 User Token，更安全且不会过期。

### Q: 部署到 snapshot 还是 release？
A:
- Snapshot: 版本号包含 `-SNAPSHOT`，自动发布
- Release: 正式版本，需要手动在 Nexus Repository Manager 中发布

## 下一步
1. 获取 Sonatype 凭证并更新 settings.xml
2. 确保 GroupId 申请已批准
3. 重新运行部署命令
