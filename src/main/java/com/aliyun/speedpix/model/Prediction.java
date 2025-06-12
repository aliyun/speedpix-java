package com.aliyun.speedpix.model;

import com.aliyun.speedpix.exception.SpeedPixException;

import java.util.HashMap;
import java.util.Map;

/**
 * SpeedPix 预测任务对象
 *
 * 表示在 SpeedPix 上运行工作流的结果
 * 字段定义参考其他语言 SDK 保持一致性
 */
public class Prediction {

    // 核心字段 - 与其他语言 SDK 保持一致
    private String id;
    private String status; // "waiting", "running", "succeeded", "failed", "canceled"
    private Map<String, Object> input;
    private Map<String, Object> output; // 对应 comfy_get_result 的 result 字段
    private String error;
    private String errorCode;
    private String invokeId;

    // 内部使用字段
    private transient Object client; // 使用 Object 类型避免循环依赖，transient 避免序列化
    private transient String workflowId;
    private transient String aliasId;

    // Constructors
    public Prediction() {}

    public Prediction(String id) {
        this.id = id;
    }

    /**
     * 构造函数，支持设置内部字段
     */
    public Prediction(String id, Object client, String workflowId, String aliasId) {
        this.id = id;
        this.client = client;
        this.workflowId = workflowId;
        this.aliasId = aliasId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskStatus getTaskStatus() {
        return TaskStatus.fromValue(status);
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        // 如果输出是 Map 类型，直接赋值；否则尝试转换或设置为 null
        if (output instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapOutput = (Map<String, Object>)output;
            this.output = mapOutput;
        } else if (output == null) {
            this.output = null;
        } else {
            // 对于其他类型，为了向后兼容，可以创建一个包装 Map
            Map<String, Object> wrapperMap = new HashMap<>();
            wrapperMap.put("value", output);
            this.output = wrapperMap;
        }
    }

    /**
     * 直接设置输出为 Map 类型
     */
    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // 兼容性方法 (保留向后兼容)
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(String invokeId) {
        this.invokeId = invokeId;
    }

    public Object getClient() {
        return client;
    }

    public void setClient(Object client) {
        this.client = client;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }

    /**
     * 检查任务是否已完成
     */
    public boolean isFinished() {
        return getTaskStatus().isFinished();
    }

    /**
     * 等待预测完成的简化版本（保留向后兼容性）
     * 注意：建议使用 waitForOutput() 方法替代
     */
    public Prediction waitForCompletion() throws InterruptedException {
        return waitForCompletion(1.0);
    }

    /**
     * 等待预测完成的简化版本（保留向后兼容性）
     * 注意：建议使用 waitForOutput(pollingInterval) 方法替代
     *
     * @param pollingInterval 轮询间隔（秒）
     */

    public Prediction waitForCompletion(double pollingInterval) throws InterruptedException {
        try {
            while (!isFinished()) {
                Thread.sleep((long)(pollingInterval * 1000));
                reload();
            }
        } catch (SpeedPixException e) {
            // 对于兼容性方法，不抛出异常，而是设置错误状态
            this.error = e.getMessage();
            this.errorCode = e.getErrorCode();
            this.invokeId = e.getApiInvokeId();
        }
        return this;
    }

    /**
     * 重新加载预测状态
     * 使用正确的 SpeedPix API 端点，实现与其他语言 SDK 一致的逻辑
     *
     * @throws SpeedPixException 如果重新加载失败
     */
    public void reload() throws SpeedPixException {
        if (client == null || id == null) {
            throw new SpeedPixException("Cannot reload prediction without client and ID");
        }

        try {
            // 使用反射获取客户端的 predictions 服务
            Class<?> clientClass = client.getClass();
            Object predictionsService = clientClass.getMethod("predictions").invoke(client);

            // 调用 get 方法获取最新状态
            Prediction updated = (Prediction)predictionsService.getClass()
                .getMethod("get", String.class)
                .invoke(predictionsService, this.id);

            // 更新当前对象的状态
            this.status = updated.getStatus();
            this.error = updated.getError();
            this.errorCode = updated.getErrorCode();
            this.invokeId = updated.getInvokeId();

            // 设置输出时自动进行 URL 转换
            this.setOutput(updated.getOutput());

        } catch (Exception e) {
            throw new SpeedPixException("Failed to reload prediction: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("Prediction{id='%s', status='%s', error='%s'}",
            id, status, error);
    }

}