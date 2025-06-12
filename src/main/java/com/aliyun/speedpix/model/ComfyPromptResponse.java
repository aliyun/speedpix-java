package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 创建预测任务的响应
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComfyPromptResponse extends GatewayResponse {

    @JsonProperty("data")
    private ComfyPromptResponseData data;

    // Constructors
    public ComfyPromptResponse() {}

    // Getters and Setters
    public ComfyPromptResponseData getData() {
        return data;
    }

    public void setData(ComfyPromptResponseData data) {
        this.data = data;
    }
}
