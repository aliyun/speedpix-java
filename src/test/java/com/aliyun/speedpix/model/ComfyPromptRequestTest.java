package com.aliyun.speedpix.model;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 ComfyPromptRequest 的 aliasId/versionId 逻辑
 */
public class ComfyPromptRequestTest {

    @Test
    public void testDefaultAliasId() {
        // 当未提供 versionId 和 aliasId 时，应该设置 aliasId 为 "main"
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .build();

        assertNull(request.getVersionId());
        assertEquals("main", request.getAliasId());
    }

    @Test
    public void testExplicitAliasId() {
        // 当明确提供 aliasId 时，应该使用提供的值
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .aliasId("custom-alias")
            .build();

        assertNull(request.getVersionId());
        assertEquals("custom-alias", request.getAliasId());
    }

    @Test
    public void testVersionIdWithoutAliasId() {
        // 当提供 versionId 时，不应该设置默认的 aliasId
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .versionId("version-456")
            .build();

        assertEquals("version-456", request.getVersionId());
        assertNull(request.getAliasId());
    }

    @Test
    public void testVersionIdWithExplicitAliasId() {
        // 当同时提供 versionId 和 aliasId 时，两者都应该被设置
        // （由服务器端决定优先级）
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .versionId("version-456")
            .aliasId("custom-alias")
            .build();

        assertEquals("version-456", request.getVersionId());
        assertEquals("custom-alias", request.getAliasId());
    }

    @Test
    public void testCompleteRequest() {
        // 测试完整的请求构建
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("prompt", "A beautiful landscape");
        inputs.put("width", 512);
        inputs.put("height", 512);

        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(inputs)
            .aliasId("production")
            .randomiseSeeds(true)
            .returnTempFiles(false)
            .build();

        assertEquals("workflow-123", request.getWorkflowId());
        assertEquals(inputs, request.getInputs());
        assertEquals("production", request.getAliasId());
        assertNull(request.getVersionId());
        assertTrue(request.getRandomiseSeeds());
        assertFalse(request.getReturnTempFiles());
    }

    @Test
    public void testEmptyAliasId() {
        // 当提供空字符串作为 aliasId 时，应该使用空字符串（不是默认值）
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .aliasId("")
            .build();

        assertNull(request.getVersionId());
        assertEquals("", request.getAliasId());
    }

    @Test
    public void testNullAliasIdExplicitly() {
        // 当明确设置 aliasId 为 null 时，应该保持 null（不是默认值）
        ComfyPromptRequest request = ComfyPromptRequest.builder("workflow-123")
            .inputs(Collections.singletonMap("prompt", "test"))
            .aliasId(null)
            .build();

        assertNull(request.getVersionId());
        assertNull(request.getAliasId());
    }
}
