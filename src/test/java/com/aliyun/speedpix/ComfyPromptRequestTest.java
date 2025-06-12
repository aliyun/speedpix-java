package com.aliyun.speedpix;

import com.aliyun.speedpix.model.ComfyPromptRequest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ComfyPromptRequest 功能测试
 */
public class ComfyPromptRequestTest {

    @Test
    public void testBuilder() {
        // 测试Builder模式
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "test prompt");
        input.put("width", 512);

        ComfyPromptRequest request = ComfyPromptRequest.builder("test-workflow")
            .inputs(input)
            .versionId("v1.0")
            .aliasId("main")
            .randomiseSeeds(true)
            .returnTempFiles(false)
            .build();

        assertNotNull(request);
        assertEquals("test-workflow", request.getWorkflowId());
        assertEquals(input, request.getInputs());
        assertEquals("v1.0", request.getVersionId());
        assertEquals("main", request.getAliasId());
        assertTrue(request.getRandomiseSeeds());
        assertFalse(request.getReturnTempFiles());
    }

    @Test
    public void testBuilderWithWorkflowId() {
        // 测试带workflowId的Builder
        ComfyPromptRequest request = ComfyPromptRequest.builder("test-workflow-2")
            .aliasId("dev")
            .build();

        assertNotNull(request);
        assertEquals("test-workflow-2", request.getWorkflowId());
        assertEquals("dev", request.getAliasId());
        assertNull(request.getInputs());
        assertNull(request.getVersionId());
        assertNull(request.getRandomiseSeeds());
        assertNull(request.getReturnTempFiles());
    }

    @Test
    public void testBuilderChaining() {
        // 测试链式调用
        ComfyPromptRequest request = ComfyPromptRequest.builder()
            .workflowId("chain-test")
            .versionId("v2.0")
            .aliasId("test")
            .randomiseSeeds(false)
            .returnTempFiles(true)
            .build();

        assertNotNull(request);
        assertEquals("chain-test", request.getWorkflowId());
        assertEquals("v2.0", request.getVersionId());
        assertEquals("test", request.getAliasId());
        assertFalse(request.getRandomiseSeeds());
        assertTrue(request.getReturnTempFiles());
    }

    @Test
    public void testStaticBuilderMethod() {
        // 测试静态builder方法
        ComfyPromptRequest request1 = ComfyPromptRequest.builder().build();
        ComfyPromptRequest request2 = ComfyPromptRequest.builder("workflow").build();

        assertNotNull(request1);
        assertNotNull(request2);
        assertNull(request1.getWorkflowId());
        assertEquals("workflow", request2.getWorkflowId());
    }

    @Test
    public void testConstructors() {
        // 测试构造函数
        ComfyPromptRequest request1 = new ComfyPromptRequest();
        ComfyPromptRequest request2 = new ComfyPromptRequest("test-workflow");

        assertNotNull(request1);
        assertNotNull(request2);
        assertNull(request1.getWorkflowId());
        assertEquals("test-workflow", request2.getWorkflowId());
    }

    @Test
    public void testSettersAndGetters() {
        // 测试传统的setter/getter
        ComfyPromptRequest request = new ComfyPromptRequest();
        Map<String, Object> input = new HashMap<>();
        input.put("key", "value");

        request.setWorkflowId("setter-test");
        request.setVersionId("v1.5");
        request.setInputs(input);
        request.setAliasId("setter-alias");
        request.setRandomiseSeeds(true);
        request.setReturnTempFiles(false);

        assertEquals("setter-test", request.getWorkflowId());
        assertEquals("v1.5", request.getVersionId());
        assertEquals(input, request.getInputs());
        assertEquals("setter-alias", request.getAliasId());
        assertTrue(request.getRandomiseSeeds());
        assertFalse(request.getReturnTempFiles());
    }
}
