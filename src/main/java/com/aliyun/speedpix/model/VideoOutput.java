package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 视频文件输出，包含视频特有的元信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoOutput extends FileOutput {
    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;
    private final long duration; // 时长，单位：毫秒
    private final String format;
    private final String codec; // 编解码器
    private final double frameRate; // 帧率
    private final int bitrate; // 比特率
    private final long fileSize;
    private final boolean hasAudio; // 是否包含音频

    /**
     * 无参构造函数，用于反序列化
     */
    protected VideoOutput() {
        super();
        this.width = 0;
        this.height = 0;
        this.duration = 0;
        this.format = null;
        this.codec = null;
        this.frameRate = 0.0;
        this.bitrate = 0;
        this.fileSize = 0;
        this.hasAudio = false;
    }

    public VideoOutput(String url, int width, int height, long duration, String format,
            String codec, double frameRate, int bitrate, long fileSize, boolean hasAudio) {
        super(url);
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.format = format;
        this.codec = codec;
        this.frameRate = frameRate;
        this.bitrate = bitrate;
        this.fileSize = fileSize;
        this.hasAudio = hasAudio;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getDuration() {
        return duration;
    }

    public String getFormat() {
        return format;
    }

    public String getCodec() {
        return codec;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public int getBitrate() {
        return bitrate;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean hasAudio() {
        return hasAudio;
    }

    @Override
    public String toString() {
        return "VideoOutput{" +
                "url='" + getUrl() + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", duration=" + duration +
                ", format='" + format + '\'' +
                ", codec='" + codec + '\'' +
                ", frameRate=" + frameRate +
                ", bitrate=" + bitrate +
                ", fileSize=" + fileSize +
                ", hasAudio=" + hasAudio +
                '}';
    }
}
