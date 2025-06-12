package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询进度响应的数据部分
 */
public class ComfyProgressResponseData {

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("etaRelative")
    private Double etaRelative;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    // Constructors
    public ComfyProgressResponseData() {}

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Double getEtaRelative() {
        return etaRelative;
    }

    public void setEtaRelative(Double etaRelative) {
        this.etaRelative = etaRelative;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
