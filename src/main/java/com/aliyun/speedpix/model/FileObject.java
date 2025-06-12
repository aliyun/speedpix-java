package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SpeedPix 文件上传后的文件对象
 */
public class FileObject {

    @JsonProperty("path")
    private String path;

    @JsonProperty("expireTime")
    private String expireTime;

    @JsonProperty("uploadUrl")
    private String uploadUrl;

    @JsonProperty("accessUrl")
    private String accessUrl;

    @JsonProperty("objectKey")
    private String objectKey;

    // 本地文件信息
    private String name;
    private String contentType;
    private Long size;

    // Constructors
    public FileObject() {}

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 获取文件的访问 URL
     */
    public String getUrl() {
        return accessUrl;
    }

    @Override
    public String toString() {
        return accessUrl != null ? accessUrl : "";
    }
}
