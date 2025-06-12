package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;

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
    public static <T> Prediction<T> run(ComfyPromptRequest request, Class<T> targetClass) throws SpeedPixException,
        InterruptedException {
        return getDefaultClient().run(request, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest和指定客户端）
     */
    public static <T> Prediction<T> run(ComfyPromptRequest request, SpeedPixClient client, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return client.run(request, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest，支持resourceConfigId）
     */
    public static <T> Prediction<T> run(ComfyPromptRequest request, String resourceConfigId, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return getDefaultClient().run(request, resourceConfigId, targetClass);
    }

    /**
     * 运行模型并返回结果（使用默认客户端）
     */
    public static <T> Prediction<T> run(String workflowId, Map<String, Object> input, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return getDefaultClient().run(workflowId, input, targetClass);
    }

    /**
     * 运行模型并返回结果（使用指定客户端）
     */
    public static <T> Prediction<T> run(String workflowId, Map<String, Object> input, SpeedPixClient client,
        Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return client.run(workflowId, input, targetClass);
    }

    /**
     * 运行模型并返回结果（使用默认客户端，支持完整参数）
     */
    public static <T> Prediction<T> run(
        String workflowId,
        Map<String, Object> input,
        boolean wait,
        String versionId,
        String aliasId,
        Boolean randomiseSeeds,
        Boolean returnTempFiles,
        String resourceConfigId,
        double pollingInterval, Class<T> targetClass) throws SpeedPixException, InterruptedException {

        return getDefaultClient().run(
            workflowId, input, wait, versionId, aliasId,
            randomiseSeeds, returnTempFiles, resourceConfigId, pollingInterval, targetClass);
    }
}
