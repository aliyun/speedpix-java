package com.aliyun.speedpix;

import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComfyPromptRequest 集成测试
 */
public class ComfyPromptRequestIntegrationTest {

    @Test
    public void testPredictionOutputIsMapType() {
        // 测试 Prediction 的 output 字段是 Map<String, Object> 类型
        Prediction prediction = new Prediction();

        // 测试设置 Map 类型的输出
        Map<String, Object> outputMap = new HashMap<>();
        outputMap.put("images", new String[]{"http://example.com/image1.png"});
        outputMap.put("info", "Generated successfully");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("width", 1024);
        parameters.put("height", 1024);
        outputMap.put("parameters", parameters);

        prediction.setOutput(outputMap);

        Map<String, Object> retrievedOutput = prediction.getOutput();
        assertNotNull(retrievedOutput);
        assertEquals(outputMap, retrievedOutput);
        assertTrue(retrievedOutput.containsKey("images"));
        assertTrue(retrievedOutput.containsKey("info"));
        assertTrue(retrievedOutput.containsKey("parameters"));
    }

    @Test
    public void testPredictionOutputWithNonMapInput() {
        // 测试设置非 Map 类型的输出时的向后兼容处理
        Prediction prediction = new Prediction();

        // 设置字符串类型的输出
        String stringOutput = "simple output";
        prediction.setOutput(stringOutput);

        Map<String, Object> retrievedOutput = prediction.getOutput();
        assertNotNull(retrievedOutput);
        assertEquals(1, retrievedOutput.size());
        assertTrue(retrievedOutput.containsKey("value"));
        assertEquals(stringOutput, retrievedOutput.get("value"));
    }

    @Test
    public void testPredictionOutputWithNullInput() {
        // 测试设置 null 输出
        Prediction prediction = new Prediction();
        prediction.setOutput((Object) null);

        Map<String, Object> retrievedOutput = prediction.getOutput();
        assertNull(retrievedOutput);
    }

    @Test
    public void testComfyPromptRequestWithPredictionsService() {
        // 测试 ComfyPromptRequest 与 PredictionsService 的集成
        // 这是一个单元测试，不会实际发送请求

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "A beautiful landscape");
        input.put("width", 1024);
        input.put("height", 1024);

        ComfyPromptRequest request = ComfyPromptRequest.builder("test-workflow-id")
            .inputs(input)
            .aliasId("main")
            .versionId("v1.0")
            .randomiseSeeds(true)
            .returnTempFiles(false)
            .build();

        // 验证请求构建正确
        assertNotNull(request);
        assertEquals("test-workflow-id", request.getWorkflowId());
        assertEquals(input, request.getInputs());
        assertEquals("main", request.getAliasId());
        assertEquals("v1.0", request.getVersionId());
        assertTrue(request.getRandomiseSeeds());
        assertFalse(request.getReturnTempFiles());
    }    @Test
    public void testSpeedPixClientMethodSignatures() {
        // 测试 SpeedPixClient 的方法签名是否正确
        // 这个测试确保方法存在且可以调用（不创建实际客户端实例）

        assertDoesNotThrow(() -> {
            // 验证 ComfyPromptRequest 相关方法存在
            SpeedPixClient.class.getMethod("run", ComfyPromptRequest.class);
            SpeedPixClient.class.getMethod("run", ComfyPromptRequest.class, String.class);
            SpeedPixClient.class.getMethod("run", ComfyPromptRequest.class, String.class, boolean.class, double.class);

            // 验证传统方法仍然存在（向后兼容）
            SpeedPixClient.class.getMethod("run", String.class, Map.class);
        });
    }@Test
    public void testSpeedPixStaticMethods() {
        // 测试 SpeedPix 静态类的新方法
        assertDoesNotThrow(() -> {
            // 验证静态方法存在
            SpeedPix.class.getMethod("run", ComfyPromptRequest.class);
            SpeedPix.class.getMethod("run", ComfyPromptRequest.class, SpeedPixClient.class);
            SpeedPix.class.getMethod("run", ComfyPromptRequest.class, String.class);
        });
    }

    @Test
    public void testBuilderPatternFluency() {
        // 测试 Builder 模式的流畅性
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "Test prompt");

        ComfyPromptRequest request = ComfyPromptRequest.builder()
            .workflowId("workflow-123")
            .inputs(input)
            .aliasId("test-alias")
            .versionId("v2.0")
            .randomiseSeeds(false)
            .returnTempFiles(true)
            .build();

        assertNotNull(request);
        assertEquals("workflow-123", request.getWorkflowId());
        assertEquals(input, request.getInputs());
        assertEquals("test-alias", request.getAliasId());
        assertEquals("v2.0", request.getVersionId());
        assertFalse(request.getRandomiseSeeds());
        assertTrue(request.getReturnTempFiles());
    }
}
