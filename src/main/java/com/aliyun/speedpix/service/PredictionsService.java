package com.aliyun.speedpix.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.*;
import com.aliyun.speedpix.util.JsonEncodingUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 预测任务服务类
 */
public class PredictionsService {

    private final SpeedPixClient client;

    public PredictionsService(SpeedPixClient client) {
        this.client = client;
    }

    /**
     * 创建预测任务
     */
    public Prediction create(ComfyPromptRequest request) throws SpeedPixException {
        return create(request, "default");
    }

    /**
     * 创建预测任务
     */
    public Prediction create(ComfyPromptRequest request, String resourceConfigId) throws SpeedPixException {
        // 验证请求参数
        if (request == null) {
            throw new SpeedPixException("ComfyPromptRequest is required");
        }
        if (request.getWorkflowId() == null || request.getWorkflowId().trim().isEmpty()) {
            throw new SpeedPixException("workflowId is required");
        }

        // 编码输入，处理文件上传
        if (request.getInputs() != null) {
            try {
                Map<String, Object> encodedInput = JsonEncodingUtils.encodeJson(request.getInputs(),
                    JsonEncodingUtils.FileEncodingStrategy.URL, client.files());
                request.setInputs(encodedInput);
            } catch (Exception e) {
                throw new SpeedPixException("Failed to encode input", e);
            }
        }

        // 设置资源配置头
        Map<String, String> headers = new HashMap<>();
        if (resourceConfigId != null && !resourceConfigId.trim().isEmpty()) {
            headers.put("X-SP-RESOURCE-CONFIG-ID", resourceConfigId);
        }

        // 发送请求到正确的 SpeedPix API 端点
        String path = "/scc/comfy_prompt";
        ComfyPromptResponse response = client.post(path, request, ComfyPromptResponse.class, headers);

        // 检查响应
        if (response.hasError()) {
            throw new SpeedPixException(
                response.getError(),
                response.getSubErrorCode() != null ? response.getSubErrorCode() : response.getErrorCode(),
                response.getApiInvokeId()
            );
        }

        if (response.getData() == null) {
            throw new SpeedPixException("Invalid response: missing data");
        }

        // 构建 Prediction 对象
        Prediction prediction = new Prediction();
        prediction.setId(response.getData().getTaskId());
        prediction.setStatus(response.getData().getStatus());
        prediction.setInput(request.getInputs());
        prediction.setClient((Object)client);

        // 将request中的workflowId和aliasId保存到prediction的瞬态字段中，便于后续使用
        prediction.setWorkflowId(request.getWorkflowId());
        prediction.setAliasId(request.getAliasId());

        return prediction;
    }

    /**
     * 创建预测任务（向后兼容）
     */
    public Prediction create(String workflowId, Map<String, Object> input) throws SpeedPixException {
        return create(workflowId, input, null, null, null, null, "default");
    }

    /**
     * 创建预测任务（向后兼容）
     */
    public Prediction create(
        String workflowId,
        Map<String, Object> input,
        String versionId,
        String aliasId,
        Boolean randomiseSeeds,
        Boolean returnTempFiles,
        String resourceConfigId) throws SpeedPixException {

        // 使用Builder模式创建ComfyPromptRequest
        ComfyPromptRequest request = ComfyPromptRequest.builder(workflowId)
            .inputs(input)
            .versionId(versionId)
            .aliasId(aliasId)
            .randomiseSeeds(randomiseSeeds)
            .returnTempFiles(returnTempFiles)
            .build();

        return create(request, resourceConfigId);
    }

    /**
     * 获取预测任务状态
     */
    public Prediction get(String predictionId) throws SpeedPixException {
        // 首先尝试获取进度
        String progressPath = "/scc/comfy_get_progress";
        Map<String, Object> progressRequest = new HashMap<>();
        progressRequest.put("taskId", predictionId);

        ComfyProgressResponse progressResponse;
        try {
            progressResponse = client.post(progressPath, progressRequest, ComfyProgressResponse.class);
        } catch (SpeedPixException e) {
            // 如果进度获取失败，尝试获取结果
            String resultPath = "/scc/comfy_get_result";
            Map<String, Object> resultRequest = new HashMap<>();
            resultRequest.put("taskId", predictionId);

            ComfyResultResponse resultResponse = client.post(resultPath, resultRequest, ComfyResultResponse.class);

            // 检查响应
            if (resultResponse.hasError()) {
                throw new SpeedPixException(
                    resultResponse.getError(),
                    resultResponse.getSubErrorCode() != null ? resultResponse.getSubErrorCode() : resultResponse.getErrorCode(),
                    resultResponse.getApiInvokeId()
                );
            }

            if (resultResponse.getData() == null) {
                throw new SpeedPixException("Invalid response: missing data");
            }

            ComfyResultResponseData data = resultResponse.getData();

            // 构建 Prediction 对象
            Prediction prediction = new Prediction();
            prediction.setId(data.getTaskId());
            prediction.setStatus(data.getStatus());
            prediction.setOutput(data.getOutput());
            prediction.setClient((Object)client);

            // 如果状态是失败，设置错误信息
            if (data.getTaskStatus() == TaskStatus.FAILED) {
                prediction.setError("Task failed");
            }

            return prediction;
        }

        // 检查进度响应
        if (progressResponse.hasError()) {
            throw new SpeedPixException(
                progressResponse.getError(),
                progressResponse.getSubErrorCode() != null ? progressResponse.getSubErrorCode() : progressResponse.getErrorCode(),
                progressResponse.getApiInvokeId()
            );
        }

        if (progressResponse.getData() == null) {
            throw new SpeedPixException("Invalid response: missing data");
        }

        ComfyProgressResponseData progressData = progressResponse.getData();

        // 构建 Prediction 对象
        Prediction prediction = new Prediction();
        prediction.setId(progressData.getTaskId());
        prediction.setStatus(progressData.getStatus());
        prediction.setClient((Object)client);

        // 如果任务已完成，尝试获取结果
        if (progressData.getTaskStatus() == TaskStatus.SUCCEEDED || progressData.getTaskStatus() == TaskStatus.FAILED) {
            String resultPath = "/scc/comfy_get_result";
            Map<String, Object> resultRequest = new HashMap<>();
            resultRequest.put("taskId", predictionId);

            try {
                ComfyResultResponse resultResponse = client.post(resultPath, resultRequest, ComfyResultResponse.class);

                if (!resultResponse.hasError() && resultResponse.getData() != null) {
                    ComfyResultResponseData resultData = resultResponse.getData();
                    prediction.setOutput(resultData.getOutput());

                    // 如果状态是失败，设置错误信息
                    if (resultData.getTaskStatus() == TaskStatus.FAILED) {
                        prediction.setError("Task failed");
                    }
                }
            } catch (SpeedPixException e) {
                // 忽略结果获取错误，返回进度信息
            }
        }

        return prediction;
    }

    /**
     * 取消预测任务
     */
    public Prediction cancel(String predictionId) throws SpeedPixException {
        throw new SpeedPixException("Cancel operation is not implemented yet");
    }
}
