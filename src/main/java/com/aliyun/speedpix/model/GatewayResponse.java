package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 网关响应基础类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayResponse {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("errCode")
    private String errorCode;

    @JsonProperty("errMessage")
    private String errorMessage;

    @JsonProperty("subErrCode")
    private String subErrorCode;

    @JsonProperty("subErrMessage")
    private String subErrorMessage;

    @JsonProperty("apiInvokeId")
    private String apiInvokeId;

    // Constructors
    public GatewayResponse() {}

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSubErrorCode() {
        return subErrorCode;
    }

    public void setSubErrorCode(String subErrorCode) {
        this.subErrorCode = subErrorCode;
    }

    public String getSubErrorMessage() {
        return subErrorMessage;
    }

    public void setSubErrorMessage(String subErrorMessage) {
        this.subErrorMessage = subErrorMessage;
    }

    public String getApiInvokeId() {
        return apiInvokeId;
    }

    public void setApiInvokeId(String apiInvokeId) {
        this.apiInvokeId = apiInvokeId;
    }

    /**
     * 检查响应是否有错误
     */
    public boolean hasError() {
        return errorCode != null || subErrorCode != null;
    }

    /**
     * 获取错误信息
     */
    public String getError() {
        if (subErrorMessage != null) {
            return subErrorMessage;
        }
        if (errorMessage != null) {
            return errorMessage;
        }
        return null;
    }
}
