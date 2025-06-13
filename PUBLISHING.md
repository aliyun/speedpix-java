# 发布到 Maven 中央仓库指南

本文档介绍如何将 SpeedPix Java SDK 发布到 Maven 中央仓库。

## 前置要求

### 1. 注册 Sonatype 账户

访问 [Sonatype OSSRH](https://central.sonatype.org/register/central-portal/) 注册账户。

### 2. 验证 GitHub 仓库所有权

由于使用 `io.github.speedpix` 作为 groupId，需要验证您对 `speedpix` GitHub 组织的控制权：

1. 在 Sonatype Central Portal 中申请 `io.github.speedpix` namespace
2. 按照指示在 GitHub 仓库中添加验证文件或创建指定的仓库

### 3. 生成 GPG 密钥

```bash
# 生成 GPG 密钥对
gpg --gen-key

# 列出密钥
gpg --list-secret-keys --keyid-format LONG

# 导出公钥到密钥服务器
gpg --keyserver keyserver.ubuntu.com --send-keys <key-id>
gpg --keyserver keys.openpgp.org --send-keys <key-id>

# 导出私钥（用于 CI/CD）
gpg --export-secret-keys -a <key-id> > private-key.asc
```

## 配置

### 1. Maven Settings

在 `~/.m2/settings.xml` 中添加服务器配置：

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>your-sonatype-username</username>
            <password>your-sonatype-password</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.keyname>your-key-id</gpg.keyname>
                <gpg.passphrase>your-gpg-passphrase</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

### 2. 环境变量（推荐用于 CI/CD）

```bash
export SONATYPE_USERNAME=your-username
export SONATYPE_PASSWORD=your-password
export GPG_KEY_ID=your-key-id
export GPG_PASSPHRASE=your-passphrase
```

## 发布流程

### 方法 1: 使用发布脚本

```bash
# 给脚本执行权限
chmod +x scripts/release.sh

# 发布快照版本
./scripts/release.sh snapshot

# 发布正式版本
./scripts/release.sh release
```

### 方法 2: 手动发布

#### 发布快照版本

```bash
# 确保版本号以 -SNAPSHOT 结尾
mvn clean deploy -P ossrh
```

#### 发布正式版本

```bash
# 1. 设置正式版本号
mvn versions:set -DnewVersion=1.0.0

# 2. 构建并部署
mvn clean deploy -P ossrh

# 3. 标记版本
git add .
git commit -m "Release version 1.0.0"
git tag v1.0.0
git push origin main --tags

# 4. 更新到下一个快照版本
mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
git add .
git commit -m "Prepare for next development iteration"
git push origin main
```

## GitHub Actions 自动化发布

### 设置 GitHub Secrets

在 GitHub 仓库设置中添加以下 secrets：

- `SONATYPE_USERNAME`: Sonatype 用户名
- `SONATYPE_PASSWORD`: Sonatype 密码
- `GPG_PRIVATE_KEY`: GPG 私钥内容（base64 编码）
- `GPG_PASSPHRASE`: GPG 密钥密码

### 自动发布工作流

工作流文件已创建在 `.github/workflows/publish.yml`，支持：

- 推送到 main 分支时自动发布快照版本
- 创建 release 标签时自动发布正式版本

## 发布检查清单

发布前请确认：

- [ ] 所有测试通过: `mvn test`
- [ ] 代码质量检查通过
- [ ] 版本号正确设置
- [ ] CHANGELOG.md 已更新
- [ ] README.md 已更新
- [ ] GPG 签名配置正确
- [ ] Sonatype 凭据配置正确

## 发布后验证

1. 检查 [Sonatype Central Portal](https://central.sonatype.com/)
2. 在新项目中测试依赖：

```xml
<dependency>
    <groupId>io.github.speedpix</groupId>
    <artifactId>speedpix-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

3. 等待同步到 Maven 中央仓库（通常需要 10-30 分钟）

## 常见问题

### 1. GPG 签名失败

确保 GPG 密钥已正确配置并且密码正确：

```bash
echo "test" | gpg --clearsign
```

### 2. Sonatype 认证失败

检查用户名和密码是否正确，可以在 Sonatype Central Portal 登录测试。

### 3. 版本已存在

Maven 中央仓库不允许覆盖已发布的版本，需要更新版本号。

### 4. 构建失败

确保所有必需的插件和依赖都已正确配置：

```bash
mvn clean compile
mvn clean package
```

## 相关链接

- [Sonatype Central Portal](https://central.sonatype.org/)
- [Maven Central Repository](https://search.maven.org/)
- [GPG 指南](https://central.sonatype.org/publish/requirements/gpg/)
- [Maven 发布指南](https://central.sonatype.org/publish/publish-maven/)
