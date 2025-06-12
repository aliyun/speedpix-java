package com.aliyun.speedpix.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.*;
import com.aliyun.speedpix.util.JsonEncodingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    public Prediction create(String workflowId, Map<String, Object> input) throws SpeedPixException {
        return create(workflowId, input, null, null, null, null, "default");
    }

    /**
     * 创建预测任务
     */
    public Prediction create(
            String workflowId,
            Map<String, Object> input,
            String versionId,
            String aliasId,
            Boolean randomiseSeeds,
            Boolean returnTempFiles,
            String resourceConfigId) throws SpeedPixException {

        // 构建请求对象
        ComfyPromptRequest request = new ComfyPromptRequest();
        request.setWorkflowId(workflowId);
        request.setVersionId(versionId);
        request.setAliasId(aliasId);
        request.setRandomiseSeeds(randomiseSeeds);
        request.setReturnTempFiles(returnTempFiles);

        // 编码输入，处理文件上传
        try {
            Object encodedInput = JsonEncodingUtils.encodeJson(input, JsonEncodingUtils.FileEncodingStrategy.URL);
            if (encodedInput instanceof Map) {
                request.setInputs((Map<String, Object>) encodedInput);
            }
        } catch (Exception e) {
            throw new SpeedPixException("Failed to encode input", e);
        }

        // 发送请求
        String path = "/v1/predictions";
        ComfyPromptResponse response = client.post(path, request, ComfyPromptResponse.class);

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
        prediction.setInput(input);
        prediction.setClient((Object) client);

        return prediction;
    }

    /**
     * 获取预测任务状态
     */
    public Prediction get(String predictionId) throws SpeedPixException {
        String path = "/v1/predictions/" + predictionId;
        ComfyResultResponse response = client.get(path, ComfyResultResponse.class);

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

        ComfyResultResponseData data = response.getData();

        // 构建 Prediction 对象
        Prediction prediction = new Prediction();
        prediction.setId(data.getTaskId());
        prediction.setStatus(data.getStatus());
        prediction.setOutput(data.getOutput());
        prediction.setClient((Object) client);

        // 如果状态是失败，设置错误信息
        if (data.getTaskStatus() == TaskStatus.FAILED) {
            prediction.setError("Task failed");
        }

        return prediction;
    }

    /**
     * 取消预测任务
     */
    public Prediction cancel(String predictionId) throws SpeedPixException {
        String path = "/v1/predictions/" + predictionId + "/cancel";
        ComfyResultResponse response = client.post(path, null, ComfyResultResponse.class);

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

        ComfyResultResponseData data = response.getData();

        // 构建 Prediction 对象
        Prediction prediction = new Prediction();
        prediction.setId(data.getTaskId());
        prediction.setStatus(data.getStatus());
        prediction.setOutput(data.getOutput());
        prediction.setClient((Object) client);

        return prediction;
    }
}
