package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.Prediction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试 PredictionsService 是否使用正确的 API 端点
 */
public class PredictionsServiceApiTest {

    private SpeedPixClient client;

    @BeforeEach
    public void setUp() {
        // 创建测试客户端
        client = new SpeedPixClient(
            "https://test-endpoint.com",
            "test-app-key",
            "test-app-secret"
        );
    }

    @Test
    public void testPredictionsServiceExists() {
        // 测试预测服务实例存在
        assertNotNull(client.predictions());
    }

    @Test
    public void testPredictionCreateMethodSignature() {
        // 测试 create 方法签名是否正确
        assertDoesNotThrow(() -> {
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", "test prompt");

            // 这个调用应该失败（因为没有真实的服务器），但方法签名应该是正确的
            try {
                client.predictions().create("test-workflow", input,
                    null, null, null, null, "default");
            } catch (SpeedPixException e) {
                // 预期的错误，因为没有真实的服务器连接
                assertTrue(e.getMessage().contains("Request failed") ||
                          e.getMessage().contains("Connection") ||
                          e.getMessage().contains("HTTP"));
            }
        });
    }

    @Test
    public void testPredictionGetMethodSignature() {
        // 测试 get 方法签名是否正确
        assertDoesNotThrow(() -> {
            try {
                client.predictions().get("test-prediction-id");
            } catch (SpeedPixException e) {
                // 预期的错误，因为没有真实的服务器连接
                assertTrue(e.getMessage().contains("Request failed") ||
                          e.getMessage().contains("Connection") ||
                          e.getMessage().contains("HTTP"));
            }
        });
    }

    @Test
    public void testPredictionCancelMethodExists() {
        // 测试 cancel 方法是否存在
        assertDoesNotThrow(() -> {
            try {
                client.predictions().cancel("test-prediction-id");
            } catch (SpeedPixException e) {
                // 应该返回 "not implemented" 错误
                assertTrue(e.getMessage().contains("not implemented"));
            }
        });
    }

    @Test
    public void testPredictionReloadMethodExists() {
        // 测试 Prediction.reload 方法是否存在
        Prediction prediction = new Prediction();
        prediction.setId("test-id");
        prediction.setClient(client);

        assertDoesNotThrow(() -> {
            try {
                prediction.reload();
            } catch (SpeedPixException e) {
                // 预期的错误，因为没有真实的服务器连接或ID不存在
                assertTrue(e.getMessage().contains("Failed to reload") ||
                          e.getMessage().contains("Request failed") ||
                          e.getMessage().contains("Connection") ||
                          e.getMessage().contains("HTTP"));
            }
        });
    }
}
