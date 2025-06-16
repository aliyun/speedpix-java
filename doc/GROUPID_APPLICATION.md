# Sonatype OSSRH GroupId 申请模板

如果你还没有为 `io.github.speedpix` 提交 GroupId 申请，请使用以下信息：

## JIRA Issue 创建信息

**访问地址**: https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134

**填写信息**:
- **Project**: Community Support - Open Source Project Repository Hosting (OSSRH)
- **Issue Type**: New Project
- **Summary**: Request for io.github.speedpix groupId
- **Description**:
  ```
  I would like to request access to publish artifacts under the groupId: io.github.speedpix

  This is for the SpeedPix Java SDK project, which provides a Java client library for AI image generation workflows.
  ```

- **Group Id**: `io.github.speedpix`
- **Project URL**: `https://github.com/speedpix/speedpix-java` (请替换为你的实际 GitHub 仓库)
- **SCM URL**: `https://github.com/speedpix/speedpix-java.git`

## 重要说明

1. **GitHub 仓库要求**: 确保你的 GitHub 仓库是公开的，并且包含项目代码
2. **域名验证**: 使用 `io.github.speedpix` 需要你拥有对应的 GitHub 用户名或组织
3. **等待批准**: 申请提交后，通常需要 1-2 个工作日审核

## 申请状态检查

申请提交后，你会收到邮件通知，也可以通过以下方式检查状态：
- 登录 https://issues.sonatype.org/
- 查看你创建的 issue 状态
- 等待 Sonatype 团队回复确认

## 批准后的操作

一旦 GroupId 申请被批准，你就可以：
1. 部署 SNAPSHOT 版本到 https://s01.oss.sonatype.org/content/repositories/snapshots/
2. 部署 RELEASE 版本到 https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
3. 在 Nexus Repository Manager 中管理你的发布

## 常见问题

**Q: 我应该使用什么 GroupId？**
A:
- 如果你有域名: `com.yourdomn.projectname`
- 如果使用 GitHub: `io.github.yourusername.projectname`
- 对于个人项目: `io.github.yourgithubusername`

**Q: 申请多久会被批准？**
A: 通常 1-2 个工作日，有时更快。

**Q: 申请被拒绝了怎么办？**
A: 检查 JIRA issue 中的回复，通常会说明拒绝原因，按要求修改后重新申请。
