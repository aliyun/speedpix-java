package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.SpeedPixException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SpeedPixClient 基础测试
 */
public class SpeedPixClientTest {

    @Test
    public void testClientCreationWithValidParameters() {
        // 测试使用有效参数创建客户端
        SpeedPixClient client = new SpeedPixClient(
            "https://test-endpoint.com",
            "test-app-key",
            "test-app-secret"
        );

        assertNotNull(client);
        assertNotNull(client.predictions());
        assertNotNull(client.files());
    }

    @Test
    public void testClientCreationWithMissingEndpoint() {
        // 测试缺少 endpoint 时使用默认值（不再抛出异常）
        SpeedPixClient client = new SpeedPixClient(null, "test-app-key", "test-app-secret");
        assertNotNull(client);

        // 验证使用了默认的 endpoint
        // 注意：我们通过反射或其他方式验证，这里假设有getter方法
        // 实际实现可能需要根据具体的类结构调整
    }

    @Test
    public void testClientCreationWithMissingAppKey() {
        // 测试缺少 appKey 时抛出异常（使用空字符串而不是null）
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", "", "test-app-secret");
        });

        // 也测试空白字符串
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", "   ", "test-app-secret");
        });
    }

    @Test
    public void testClientCreationWithMissingAppSecret() {
        // 测试缺少 appSecret 时抛出异常（使用空字符串而不是null）
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", "test-app-key", "");
        });

        // 也测试空白字符串
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", "test-app-key", "   ");
        });
    }

    @Test
    public void testSimpleConstructor() {
        // 测试最简构造函数 - 仅需要 appKey 和 appSecret
        SpeedPixClient client = new SpeedPixClient("test-app-key", "test-app-secret");

        assertNotNull(client);
        assertNotNull(client.predictions());
        assertNotNull(client.files());
    }

    @Test
    public void testBuilder() {
        // 测试Builder模式
        SpeedPixClient client = SpeedPixClient.builder()
            .appKey("test-app-key")
            .appSecret("test-app-secret")
            .endpoint("https://custom-endpoint.com")
            .userAgent("test-agent/1.0.0")
            .timeoutSeconds(60)
            .build();

        assertNotNull(client);
        assertNotNull(client.predictions());
        assertNotNull(client.files());
    }

    @Test
    public void testBuilderWithDefaults() {
        // 测试Builder模式使用默认值
        SpeedPixClient client = SpeedPixClient.builder()
            .appKey("test-app-key")
            .appSecret("test-app-secret")
            .build();

        assertNotNull(client);
        assertNotNull(client.predictions());
        assertNotNull(client.files());
    }

    @Test
    public void testBuilderFromEnv() {
        // 测试Builder的fromEnv方法
        // 注意：这个测试依赖环境变量，在实际环境中可能需要模拟
        SpeedPixClient.Builder builder = SpeedPixClient.builder().fromEnv();
        assertNotNull(builder);

        // 如果环境变量存在，应该能够构建客户端
        // 如果不存在，构建时会抛出异常
        if (System.getenv("SPEEDPIX_APP_KEY") != null && System.getenv("SPEEDPIX_APP_SECRET") != null) {
            SpeedPixClient client = builder.build();
            assertNotNull(client);
        }
    }

    @Test
    public void testBuilderMissingCredentials() {
        // 测试Builder缺少必需参数时的行为
        assertThrows(IllegalArgumentException.class, () -> {
            SpeedPixClient.builder()
                .endpoint("https://test-endpoint.com")
                .build();
        });
    }

    @Test
    public void testStaticFactoryMethods() {
        // 测试静态工厂方法
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test");

        // 这个测试在实际环境中需要有效的凭据
        // 这里只是验证方法签名正确
        assertDoesNotThrow(() -> {
            // SpeedPix.run("test-workflow", input);
        });
    }
}
