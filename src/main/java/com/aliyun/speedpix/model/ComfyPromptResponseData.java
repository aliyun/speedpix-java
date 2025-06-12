package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 创建预测任务响应的数据部分
 */
public class ComfyPromptResponseData {

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("estimatedDurationInSeconds")
    private Double estimatedDurationInSeconds;

    // Constructors
    public ComfyPromptResponseData() {}

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public Double getEstimatedDurationInSeconds() {
        return estimatedDurationInSeconds;
    }

    public void setEstimatedDurationInSeconds(Double estimatedDurationInSeconds) {
        this.estimatedDurationInSeconds = estimatedDurationInSeconds;
    }
}
