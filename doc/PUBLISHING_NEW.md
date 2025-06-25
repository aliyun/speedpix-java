# 发布到 Maven Central 指南 (Central Portal 新方式)

本文档介绍如何使用 Sonatype 的新 Central Portal 将 SpeedPix Java SDK 发布到 Maven Central。

## 🎯 快速开始

如果您已经有 Central Portal 账号和凭证，可以直接：

```bash
# 发布 snapshot
./scripts/release.sh snapshot

# 发布正式版本
./scripts/release.sh release 1.0.0
```

## 📋 前置要求

### 1. Central Portal 账号注册

**重要**：这是新的注册方式，比旧的 OSSRH 更简单！

1. 访问 [Central Portal](https://central.sonatype.com/)
2. 点击 "Sign up" 注册账号
3. 验证邮箱地址

### 2. 获取发布 Token

1. 登录 [Central Portal](https://central.sonatype.com/)
2. 点击右上角用户名 -> "View Account"
3. 点击 "Generate User Token"
4. 复制用户名和密码（这将用于 Maven 配置）

### 3. 申请 Namespace

1. 在 Central Portal 中点击 "Add Namespace"
2. 输入 `com.aliyun.speedpix`
3. 选择 "GitHub" 验证方式
4. 按照指示验证 GitHub 仓库所有权
5. 等待自动验证（通常几分钟内完成）

### 4. 设置 GPG 签名

```bash
# 生成 GPG 密钥对
gpg --gen-key

# 查看密钥 ID
gpg --list-secret-keys --keyid-format LONG

# 导出公钥（可选 - Central Portal 会自动处理）
gpg --armor --export YOUR_KEY_ID > public-key.asc
```

## 🔧 配置

### 1. 配置 Maven Settings

编辑 `~/.m2/settings.xml`：

```xml
<?xml version="1.0"?>
<settings>
    <servers>
        <!-- Central Portal 凭证 -->
        <server>
            <id>central</id>
            <username>YOUR_TOKEN_USERNAME</username>
            <password>YOUR_TOKEN_PASSWORD</password>
        </server>

        <!-- GPG 密码 -->
        <server>
            <id>gpg.passphrase</id>
            <passphrase>YOUR_GPG_PASSPHRASE</passphrase>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>central-publishing</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.keyname>YOUR_GPG_KEY_ID</gpg.keyname>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

### 2. 项目配置验证

项目已配置了新的 `central-publishing-maven-plugin`：

```xml
<plugin>
    <groupId>org.sonatype.central</groupId>
    <artifactId>central-publishing-maven-plugin</artifactId>
    <version>0.7.0</version>
    <extensions>true</extensions>
    <configuration>
        <publishingServerId>central</publishingServerId>
        <autoPublish>true</autoPublish>
        <waitUntil>published</waitUntil>
        <checksums>all</checksums>
    </configuration>
</plugin>
```

## 🚀 发布流程

### 发布 Snapshot

```bash
# 确保版本号包含 -SNAPSHOT
./scripts/release.sh snapshot
```

### 发布正式版本

```bash
# 设置发布版本并自动部署
./scripts/release.sh release 1.0.0
```

发布过程包括：
1. ✅ 运行所有测试
2. ✅ 生成源码和 Javadoc JAR
3. ✅ GPG 签名所有文件
4. ✅ 上传到 Central Portal
5. ✅ 自动验证和发布
6. ✅ 标记 Git 版本
7. ✅ 准备下一个开发版本

## 📊 监控和验证

### 1. 检查部署状态

访问 [Central Portal Deployments](https://central.sonatype.com/publishing/deployments) 查看部署状态。

### 2. 验证发布

```bash
# 检查 Maven Central（发布后约 30 分钟可用）
curl -I "https://repo1.maven.org/maven2/com/aliyun/speedpix/speedpix-java/1.0.0/"

# 搜索工件
curl "https://search.maven.org/solrsearch/select?q=g:com.aliyun.speedpix"
```

## 🔍 故障排除

### 常见问题

**1. 401 Unauthorized**
- 检查 Central Portal Token 是否正确
- 确认 `settings.xml` 中的 server id 为 `central`
- 验证 Token 是否过期

**2. 403 Forbidden**
- 确认 Namespace `com.aliyun` 已被批准
- 检查 GitHub 仓库验证是否完成

**3. GPG 签名失败**
```bash
# 测试 GPG 签名
echo "test" | gpg --clearsign

# 检查 GPG 配置
gpg --list-secret-keys
```

**4. Javadoc 构建失败**
```bash
# 单独测试 Javadoc 构建
mvn javadoc:javadoc
```

### 调试命令

```bash
# 仅构建，不部署
mvn clean verify

# 跳过测试的构建
mvn clean verify -DskipTests

# 跳过 GPG 签名的测试
mvn clean verify -Dgpg.skip=true

# 详细输出
mvn clean deploy -X
```

## 🆚 与旧 OSSRH 方式的区别

| 特性 | 新 Central Portal | 旧 OSSRH |
|------|------------------|----------|
| 注册方式 | central.sonatype.com | issues.sonatype.org |
| 验证速度 | 分钟级别 | 天级别 |
| 部署插件 | central-publishing-maven-plugin | nexus-staging-maven-plugin |
| 自动发布 | 支持 | 需要手动 |
| Snapshot 支持 | ✅ | ✅ |
| 用户界面 | 现代化 Web UI | 传统 Nexus UI |

## 📚 参考资源

- [Central Portal 官方文档](https://central.sonatype.org/publish/publish-portal-guide/)
- [Maven 插件文档](https://central.sonatype.org/publish/publish-portal-maven/)
- [Namespace 验证指南](https://central.sonatype.org/register/namespace/)
- [GPG 签名要求](https://central.sonatype.org/publish/requirements/gpg/)

## 💡 最佳实践

1. **使用 User Token**：比用户名/密码更安全
2. **启用自动发布**：减少手动操作
3. **版本管理**：使用语义化版本号
4. **测试充分**：发布前确保所有测试通过
5. **文档同步**：及时更新 README 和 CHANGELOG

---

*本文档基于 Central Portal 最新功能编写，如有问题请参考官方文档或创建 Issue。*
