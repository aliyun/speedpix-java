package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.ComfyPromptRequest;

import java.util.Map;

/**
 * SpeedPix 静态工厂类，提供便捷的全局方法
 */
public class SpeedPix {

    private static SpeedPixClient defaultClient = null;

    /**
     * 获取默认客户端实例
     */
    public static SpeedPixClient getDefaultClient() {
        if (defaultClient == null) {
            defaultClient = new SpeedPixClient(null, null, null);
        }
        return defaultClient;
    }

    /**
     * 设置默认客户端
     */
    public static void setDefaultClient(SpeedPixClient client) {
        defaultClient = client;
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest和默认客户端）
     */
    public static Object run(ComfyPromptRequest request) throws SpeedPixException, InterruptedException {
        return getDefaultClient().run(request);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest和指定客户端）
     */
    public static Object run(ComfyPromptRequest request, SpeedPixClient client) throws SpeedPixException, InterruptedException {
        return client.run(request);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest，支持resourceConfigId）
     */
    public static Object run(ComfyPromptRequest request, String resourceConfigId) throws SpeedPixException, InterruptedException {
        return getDefaultClient().run(request, resourceConfigId);
    }

    /**
     * 运行模型并返回结果（使用默认客户端）
     */
    public static Object run(String workflowId, Map<String, Object> input) throws SpeedPixException, InterruptedException {
        return getDefaultClient().run(workflowId, input);
    }

    /**
     * 运行模型并返回结果（使用指定客户端）
     */
    public static Object run(String workflowId, Map<String, Object> input, SpeedPixClient client) throws SpeedPixException, InterruptedException {
        return client.run(workflowId, input);
    }

    /**
     * 运行模型并返回结果（使用默认客户端，支持完整参数）
     */
    public static Object run(
            String workflowId,
            Map<String, Object> input,
            boolean wait,
            String versionId,
            String aliasId,
            Boolean randomiseSeeds,
            Boolean returnTempFiles,
            String resourceConfigId,
            double pollingInterval) throws SpeedPixException, InterruptedException {

        return getDefaultClient().run(
                workflowId, input, wait, versionId, aliasId,
                randomiseSeeds, returnTempFiles, resourceConfigId, pollingInterval
        );
    }
}
