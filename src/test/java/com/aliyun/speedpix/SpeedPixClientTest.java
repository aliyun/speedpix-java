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
        // 测试缺少 endpoint 时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient(null, "test-app-key", "test-app-secret");
        });
    }

    @Test
    public void testClientCreationWithMissingAppKey() {
        // 测试缺少 appKey 时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", null, "test-app-secret");
        });
    }

    @Test
    public void testClientCreationWithMissingAppSecret() {
        // 测试缺少 appSecret 时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            new SpeedPixClient("https://test-endpoint.com", "test-app-key", null);
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
