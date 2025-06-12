package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chanyuan.lb
 * @description
 * @create 2025-06-12 11:06
 */
public class ImageFile {
    @JsonProperty("url")
    private String url;

    @JsonProperty("preview")
    private String preview;

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("object_key")
    private String objectKey;

    @JsonProperty("format")
    private String format;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("type")
    private String type;

    public ImageFile() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ImageFile{" +
            "url='" + url + '\'' +
            ", preview='" + preview + '\'' +
            ", filename='" + filename + '\'' +
            ", objectKey='" + objectKey + '\'' +
            ", format='" + format + '\'' +
            ", width=" + width +
            ", height=" + height +
            ", type='" + type + '\'' +
            '}';
    }
}
