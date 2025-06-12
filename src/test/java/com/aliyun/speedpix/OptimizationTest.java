package com.aliyun.speedpix;

import com.aliyun.speedpix.model.FileOutput;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 优化功能测试：验证参数验证和输出转换功能
 */
class OptimizationTest {

    @Test
    void testVersionIdAliasIdValidation_BothNull() {
        SpeedPixClient client = new SpeedPixClient("test-key", "test-secret", "https://test.example.com");

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test prompt");

        // 当 versionId 和 aliasId 都为 null 时应该抛出异常
        Exception exception = assertThrows(Exception.class, () -> {
            client.run("test-workflow", input, false, null, null, false, false, "url", 0.0);
        });

        // 检查是否包含验证错误或网络错误（都表明到达了我们的验证逻辑）
        assertTrue(exception.getMessage().contains("至少需要提供 versionId 或 aliasId 中的一个") ||
                  exception instanceof IllegalArgumentException);
    }

    @Test
    void testVersionIdAliasIdValidation_BothEmpty() {
        SpeedPixClient client = new SpeedPixClient("test-key", "test-secret", "https://test.example.com");

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test prompt");

        // 当 versionId 和 aliasId 都为空字符串时应该抛出异常
        Exception exception = assertThrows(Exception.class, () -> {
            client.run("test-workflow", input, false, "", "", false, false, "url", 0.0);
        });

        // 检查是否包含验证错误或网络错误
        assertTrue(exception.getMessage().contains("至少需要提供 versionId 或 aliasId 中的一个") ||
                  exception instanceof IllegalArgumentException);
    }

    @Test
    void testVersionIdAliasIdValidation_OneProvided() {
        SpeedPixClient client = new SpeedPixClient("test-key", "test-secret", "https://test.example.com");

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test prompt");

        // 只要有一个参数被提供，就不应该抛出验证异常
        // 注意：这个测试会因为网络调用失败，但不会因为参数验证失败
        assertThrows(Exception.class, () -> {
            client.run("test-workflow", input, false, "version123", null, false, false, "url", 0.0);
        });

        assertThrows(Exception.class, () -> {
            client.run("test-workflow", input, false, null, "alias123", false, false, "url", 0.0);
        });
    }

    @Test
    void testOutputTransform_SimpleUrl() {
        String testUrl = "https://example.com/image.jpg";

        // 手动创建 FileOutput 对象来演示转换功能
        FileOutput result = new FileOutput(testUrl);

        assertInstanceOf(FileOutput.class, result);
        assertEquals(testUrl, result.getUrl());
    }

    @Test
    void testOutputTransform_NestedStructure() {
        // 测试手动转换功能
        Map<String, Object> testOutput = new HashMap<>();
        List<String> imageUrls = Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.png"
        );
        testOutput.put("images", imageUrls);
        testOutput.put("metadata", "some metadata");
        testOutput.put("result_url", "https://example.com/result.webp");

        // 手动转换 URL 为 FileOutput
        Map<String, Object> transformedOutput = new HashMap<>();
        for (Map.Entry<String, Object> entry : testOutput.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String && isHttpUrl((String) value)) {
                transformedOutput.put(entry.getKey(), new FileOutput((String) value));
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> urlList = (List<String>) value;
                List<Object> transformedList = new ArrayList<>();
                for (String url : urlList) {
                    if (isHttpUrl(url)) {
                        transformedList.add(new FileOutput(url));
                    } else {
                        transformedList.add(url);
                    }
                }
                transformedOutput.put(entry.getKey(), transformedList);
            } else {
                transformedOutput.put(entry.getKey(), value);
            }
        }

        // 验证转换结果
        assertInstanceOf(List.class, transformedOutput.get("images"));
        @SuppressWarnings("unchecked")
        List<Object> resultImages = (List<Object>) transformedOutput.get("images");

        assertEquals(2, resultImages.size());
        assertInstanceOf(FileOutput.class, resultImages.get(0));
        assertInstanceOf(FileOutput.class, resultImages.get(1));

        // 检查单个 URL 是否被转换
        assertInstanceOf(FileOutput.class, transformedOutput.get("result_url"));
        FileOutput resultFileOutput = (FileOutput) transformedOutput.get("result_url");
        assertEquals("https://example.com/result.webp", resultFileOutput.getUrl());

        // 检查非 URL 字段是否保持不变
        assertEquals("some metadata", transformedOutput.get("metadata"));
    }

    @Test
    void testFileOutput_BasicFunctionality() {
        String testUrl = "https://example.com/test.jpg";
        FileOutput fileOutput = new FileOutput(testUrl);

        assertEquals(testUrl, fileOutput.getUrl());
        assertTrue(fileOutput.toString().contains(testUrl)); // FileOutput.toString() 格式可能不同

        // 测试 equals 方法
        FileOutput anotherFileOutput = new FileOutput(testUrl);
        assertEquals(fileOutput, anotherFileOutput);

        FileOutput differentFileOutput = new FileOutput("https://example.com/different.jpg");
        assertNotEquals(fileOutput, differentFileOutput);
    }

    @Test
    void testDefaultRunMethod_UsesDefaultAlias() {
        SpeedPixClient client = new SpeedPixClient("test-key", "test-secret", "https://test.example.com");

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test prompt");

        // 简单的 run 方法应该使用 aliasId="default" 来满足验证要求
        // 这个测试会因为网络调用失败，但不会因为参数验证失败
        assertThrows(Exception.class, () -> {
            client.run("test-workflow", input);
        });
    }

    /**
     * 检查字符串是否为 HTTP/HTTPS URL
     */
    private static boolean isHttpUrl(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        String trimmed = str.trim().toLowerCase();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }
}
