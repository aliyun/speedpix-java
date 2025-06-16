#!/bin/bash
# Sonatype 凭证配置脚本

echo "=== SpeedPix Java SDK - Sonatype 部署配置 ==="
echo ""

# 检查当前配置
echo "1. 检查当前 settings.xml..."
if [ -f ~/.m2/settings.xml ]; then
    echo "✓ settings.xml 文件存在"
    if grep -q "YOUR_SONATYPE_USERNAME" ~/.m2/settings.xml; then
        echo "⚠️  需要配置 Sonatype 凭证"
    else
        echo "✓ 似乎已配置凭证"
    fi
else
    echo "❌ settings.xml 文件不存在"
fi

echo ""
echo "2. 获取 Sonatype 凭证的步骤："
echo "   a) 访问 https://issues.sonatype.org/secure/Signup!default.jspa 注册账号"
echo "   b) 创建 GroupId 申请 Issue (如果还没有)"
echo "   c) 获取 User Token: https://s01.oss.sonatype.org/ -> Profile -> User Token"
echo ""

echo "3. 手动配置步骤："
echo "   编辑 ~/.m2/settings.xml，找到 ossrh 服务器配置："
echo "   <server>"
echo "       <id>ossrh</id>"
echo "       <username>你的用户名或Token用户名</username>"
echo "       <password>你的密码或Token密码</password>"
echo "   </server>"
echo ""

echo "4. 验证配置："
echo "   mvn clean deploy -Dgpg.skip=true  # 跳过GPG签名的测试"
echo "   mvn clean deploy                 # 完整部署"
echo ""

# 检查是否已经配置了实际的凭证
if grep -q "YOUR_SONATYPE_USERNAME" ~/.m2/settings.xml 2>/dev/null; then
    echo "❌ 当前 settings.xml 中还有占位符，需要替换为实际凭证"
    echo ""
    echo "是否要现在配置？(需要你手动输入凭证)"
    echo "如果你有凭证，可以运行以下命令进行配置："
    echo ""
    echo "# 替换用户名 (将 YOUR_USERNAME 替换为实际用户名)"
    echo "sed -i.bak 's/YOUR_SONATYPE_USERNAME/实际用户名/' ~/.m2/settings.xml"
    echo ""
    echo "# 替换密码 (将 YOUR_PASSWORD 替换为实际密码)"
    echo "sed -i.bak 's/YOUR_SONATYPE_PASSWORD/实际密码/' ~/.m2/settings.xml"
fi
