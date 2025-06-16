# 发布到 Maven 中央仓库指南

本文档介绍如何将 SpeedPix Java SDK 发布到 Maven 中央仓库。

## 快速参考：GPG Key-ID 获取

如果您只想快速获取 GPG key-id，执行以下命令：

```bash
# 快速获取 key-id
gpg --list-secret-keys --keyid-format LONG | grep sec | head -1
```

输出示例：
```
sec   rsa4096/1A2B3C4D5E6F7G8H 2025-06-13 [SC]
```

在这个例子中，`1A2B3C4D5E6F7G8H` 就是您的 key-id。

如果没有输出，说明您还没有 GPG 密钥，请按照下面的详细步骤生成。

---

## 前置要求

### 1. 注册 Sonatype 账户

访问 [Sonatype OSSRH](https://central.sonatype.org/register/central-portal/) 注册账户。

### 2. 验证 GitHub 仓库所有权

由于使用 `io.github.speedpix` 作为 groupId，需要验证您对 `speedpix` GitHub 组织的控制权：

1. 在 Sonatype Central Portal 中申请 `io.github.speedpix` namespace
2. 按照指示在 GitHub 仓库中添加验证文件或创建指定的仓库

### 3. 生成 GPG 密钥

```bash
# 生成 GPG 密钥对（按提示输入姓名、邮箱等信息）
gpg --gen-key

# 列出密钥并查看 key-id
gpg --list-secret-keys --keyid-format LONG
```

执行 `gpg --list-secret-keys --keyid-format LONG` 后，您会看到类似以下输出：

```
/Users/username/.gnupg/secring.gpg
------------------------------------
sec   rsa4096/1A2B3C4D5E6F7G8H 2025-06-13 [SC]
      ABCD1234EFGH5678IJKL9012MNOP3456QRST7890
uid                   [ultimate] Your Name <your.email@example.com>
ssb   rsa4096/9H8G7F6E5D4C3B2A 2025-06-13 [E]
```

在这个例子中：
- **key-id (短格式)**: `1A2B3C4D5E6F7G8H`
- **key-id (长格式)**: `ABCD1234EFGH5678IJKL9012MNOP3456QRST7890`

推荐使用长格式的 key-id，更安全且唯一。

```bash
# 使用 key-id 导出公钥到密钥服务器（使用您实际的 key-id）
gpg --keyserver keyserver.ubuntu.com --send-keys ABCD1234EFGH5678IJKL9012MNOP3456QRST7890
gpg --keyserver keys.openpgp.org --send-keys ABCD1234EFGH5678IJKL9012MNOP3456QRST7890

# 导出私钥（用于 CI/CD，使用您实际的 key-id）
gpg --export-secret-keys -a ABCD1234EFGH5678IJKL9012MNOP3456QRST7890 > private-key.asc

# 验证密钥是否正确导出
gpg --list-keys ABCD1234EFGH5678IJKL9012MNOP3456QRST7890
```

### GPG 密钥设置详细说明

#### 1. 生成密钥时的注意事项

在执行 `gpg --gen-key` 时，系统会提示您输入以下信息：

- **姓名**: 输入您的真实姓名
- **邮箱**: 使用与 Sonatype 账户相同的邮箱地址
- **密码**: 设置一个强密码，后续签名时需要使用

#### 2. 获取 key-id 的不同方法

```bash
# 方法 1: 显示详细信息（推荐）
gpg --list-secret-keys --keyid-format LONG

# 方法 2: 只显示 key-id
gpg --list-secret-keys --keyid-format LONG | grep sec | cut -d'/' -f2 | cut -d' ' -f1

# 方法 3: 显示指纹信息
gpg --fingerprint --list-secret-keys
```

#### 3. 常见问题解决

**问题 1: 密钥服务器无法访问**
```bash
# 尝试不同的密钥服务器
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys YOUR_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID
```

**问题 2: 密钥过期**
```bash
# 延长密钥有效期
gpg --edit-key YOUR_KEY_ID
# 在 GPG 提示符下输入: expire
# 然后选择新的过期时间，输入: save
```

**问题 3: 忘记密码**
```bash
# 修改密钥密码
gpg --edit-key YOUR_KEY_ID
# 在 GPG 提示符下输入: passwd
# 然后输入新密码，输入: save
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
# 测试 GPG 签名功能
echo "test" | gpg --clearsign

# 如果出现错误，检查密钥是否存在
gpg --list-secret-keys --keyid-format LONG

# 测试使用特定密钥签名
echo "test" | gpg --default-key YOUR_KEY_ID --clearsign
```

### 2. 找不到 GPG key-id

如果无法找到 key-id，按以下步骤操作：

```bash
# 列出所有密钥
gpg --list-secret-keys --keyid-format LONG

# 如果没有密钥，需要先生成
gpg --gen-key

# 确认密钥已正确生成
gpg --list-keys
```

### 3. GPG 密钥无法上传到密钥服务器

```bash
# 尝试不同的密钥服务器
gpg --keyserver hkp://keyserver.ubuntu.com:80 --send-keys YOUR_KEY_ID
gpg --keyserver hkp://pool.sks-keyservers.net:80 --send-keys YOUR_KEY_ID

# 如果仍然失败，可以手动上传到网页版
# 导出公钥并复制内容到密钥服务器网站
gpg --armor --export YOUR_KEY_ID
```

### 4. Maven 中 GPG 密钥配置问题

在 Maven settings.xml 中配置密钥时，确保使用正确的 key-id：

```xml
<properties>
    <gpg.executable>gpg</gpg.executable>
    <gpg.keyname>YOUR_FULL_KEY_ID</gpg.keyname>  <!-- 使用完整的 key-id -->
    <gpg.passphrase>your-gpg-passphrase</gpg.passphrase>
</properties>
```

### 5. Sonatype 认证失败

检查用户名和密码是否正确，可以在 Sonatype Central Portal 登录测试。

### 6. 版本已存在

Maven 中央仓库不允许覆盖已发布的版本，需要更新版本号。

### 7. 构建失败

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
