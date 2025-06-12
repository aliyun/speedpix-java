package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 文件上传签名响应
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileUploadSignResponse extends GatewayResponse {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        @JsonProperty("path")
        private String path;

        @JsonProperty("expireTime")
        private Object expireTime; // Can be string or number

        @JsonProperty("uploadUrl")
        private String uploadUrl;

        @JsonProperty("accessUrl")
        private String accessUrl;

        @JsonProperty("objectKey")
        private String objectKey;

        // Getters and setters
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Object getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Object expireTime) {
            this.expireTime = expireTime;
        }

        public String getUploadUrl() {
            return uploadUrl;
        }

        public void setUploadUrl(String uploadUrl) {
            this.uploadUrl = uploadUrl;
        }

        public String getAccessUrl() {
            return accessUrl;
        }

        public void setAccessUrl(String accessUrl) {
            this.accessUrl = accessUrl;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public void setObjectKey(String objectKey) {
            this.objectKey = objectKey;
        }
    }

    @JsonProperty("data")
    private Data data;

    // Getters and setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
