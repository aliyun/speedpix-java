package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 图片文件输出，包含图片特有的元信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageOutput extends FileOutput {
    private static final long serialVersionUID = 1L;

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

    /**
     * 无参构造函数，用于反序列化
     */
    protected ImageOutput() {
        super();
        this.width = 0;
        this.height = 0;
        this.format = null;
    }

    public ImageOutput(String url, int width, int height, String format, long fileSize) {
        super(url);
        this.width = width;
        this.height = height;
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "ImageOutput{" +
            "url='" + getUrl() + '\'' +
            ", width=" + width +
            ", height=" + height +
            ", format='" + format + '\'' +
            '}';
    }
}
