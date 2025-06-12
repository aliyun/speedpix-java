package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * 查询结果响应的数据部分
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComfyResultResponseData {

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("images")
    private List<String> images;

    @JsonProperty("info")
    private Map<String, String> info;

    @JsonProperty("status")
    private String status;

    @JsonProperty("result")
    private Map<String, Object> result;

    // Constructors
    public ComfyResultResponseData() {}

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
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

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    /**
     * 获取输出结果，直接返回 result 结构
     */
    public Map<String, Object> getOutput() {
        return result;
    }
}
