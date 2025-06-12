package com.aliyun.speedpix.model;

/**
 * 文件上传选项
 */
public class FileUploadOptions {
    private String filename;
    private String contentType;

    public FileUploadOptions() {}

    public FileUploadOptions(String filename, String contentType) {
        this.filename = filename;
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public static FileUploadOptions builder() {
        return new FileUploadOptions();
    }

    public FileUploadOptions filename(String filename) {
        this.filename = filename;
        return this;
    }

    public FileUploadOptions contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
}
