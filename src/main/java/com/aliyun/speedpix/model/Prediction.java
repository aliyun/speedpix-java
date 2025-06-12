package com.aliyun.speedpix.model;

import java.util.Map;

/**
 * SpeedPix 预测任务对象
 */
public class Prediction {

    private String id;
    private String status;
    private Object input;
    private Object output;
    private String error;
    private Double progress;
    private String createdAt;
    private String completedAt;
    private Map<String, String> logs;
    private Object client; // 使用 Object 类型避免循环依赖

    // Constructors
    public Prediction() {}

    public Prediction(String id) {
        this.id = id;
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

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public Object getOutput() {
        return output;
    }

    public void setOutput(Object output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Map<String, String> getLogs() {
        return logs;
    }

    public void setLogs(Map<String, String> logs) {
        this.logs = logs;
    }

    public Object getClient() {
        return client;
    }

    public void setClient(Object client) {
        this.client = client;
    }

    /**
     * 检查任务是否已完成
     */
    public boolean isFinished() {
        return getTaskStatus().isFinished();
    }

    /**
     * 等待预测完成的简化版本
     * 注意：需要通过客户端来实现实际的等待逻辑
     */
    public Prediction waitForCompletion() throws InterruptedException {
        return waitForCompletion(1.0);
    }

    /**
     * 等待预测完成的简化版本
     * @param pollingInterval 轮询间隔（秒）
     */
    public Prediction waitForCompletion(double pollingInterval) throws InterruptedException {
        // 这里是一个简化的实现，实际应该通过客户端来重新加载状态
        // 在实际使用中，这个方法会被 SpeedPixClient 重写或扩展

        while (!isFinished()) {
            Thread.sleep((long) (pollingInterval * 1000));
            // 注意：在实际实现中，这里应该重新加载预测状态
            // 由于避免循环依赖，这里只是一个占位实现
            break; // 临时退出，避免无限循环
        }

        return this;
    }
}