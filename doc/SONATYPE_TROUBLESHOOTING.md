# Sonatype 部署问题诊断报告

## 当前错误分析
错误信息：`authentication failed for https://s01.oss.sonatype.org/content/repositories/snapshots`

## 测试结果
- ❌ **凭证测试失败**: HTTP 401 - 认证失败
- ❌ **无法访问 snapshots repository**

## 可能的原因

### 1. 凭证问题（最可能）
当前配置的凭证可能存在以下问题：
- User Token 已过期
- 用户名/密码输入错误
- Token 复制时包含了额外字符

### 2. 账号权限问题
- 没有 Sonatype OSSRH 账号
- 账号存在但没有部署权限
- GroupId `io.github.speedpix` 未获得批准

### 3. 配置问题
- settings.xml 中的 server id 不匹配
- distributionManagement 配置错误

## 解决步骤

### 步骤 1: 验证 Sonatype 账号
1. 访问 https://s01.oss.sonatype.org/
2. 使用你的账号登录
3. 如果无法登录，说明账号或密码有问题

### 步骤 2: 重新获取 User Token
1. 登录后，点击右上角用户名 -> Profile
2. 找到 "User Token" 部分
3. 点击 "Access User Token"
4. 复制新的用户名和密码

### 步骤 3: 检查 GroupId 权限
1. 登录 https://issues.sonatype.org/
2. 搜索是否有关于 `io.github.speedpix` 的 JIRA issue
3. 如果没有，需要创建新的 GroupId 申请

### 步骤 4: 更新 settings.xml
使用新获取的凭证更新 ~/.m2/settings.xml：

```xml
<server>
    <id>ossrh</id>
    <username>新的Token用户名</username>
    <password>新的Token密码</password>
</server>
```

### 步骤 5: 重新测试
```bash
# 测试凭证
curl -I -u "用户名:密码" "https://s01.oss.sonatype.org/content/repositories/snapshots/"

# 如果返回 200，则继续部署
mvn clean deploy -Dgpg.skip=true
```

## 快速检查清单

- [ ] 有效的 Sonatype OSSRH 账号
- [ ] 能够登录 https://s01.oss.sonatype.org/
- [ ] 有效的 User Token
- [ ] GroupId 申请已提交并批准
- [ ] settings.xml 配置正确
- [ ] 网络可以访问 Sonatype 服务器

## 建议的下一步操作

1. **立即执行**: 重新获取 User Token 并更新 settings.xml
2. **如果仍失败**: 检查是否有 GroupId 申请的 JIRA issue
3. **如果没有申请**: 创建新的 GroupId 申请 issue

请按照上述步骤逐一检查，我们可以继续协助解决具体问题。
