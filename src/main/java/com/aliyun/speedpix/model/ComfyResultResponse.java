package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询结果的响应
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComfyResultResponse extends GatewayResponse {

    @JsonProperty("data")
    private ComfyResultResponseData data;

    // Constructors
    public ComfyResultResponse() {}

    // Getters and Setters
    public ComfyResultResponseData getData() {
        return data;
    }

    public void setData(ComfyResultResponseData data) {
        this.data = data;
    }
}
