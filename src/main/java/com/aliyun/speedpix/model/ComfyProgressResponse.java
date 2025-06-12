package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询进度的响应
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComfyProgressResponse extends GatewayResponse {

    @JsonProperty("data")
    private ComfyProgressResponseData data;

    // Constructors
    public ComfyProgressResponse() {}

    // Getters and Setters
    public ComfyProgressResponseData getData() {
        return data;
    }

    public void setData(ComfyProgressResponseData data) {
        this.data = data;
    }
}
