package com.aliyun.speedpix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 音频文件输出，包含音频特有的元信息
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AudioOutput extends FileOutput {
    private static final long serialVersionUID = 1L;

    private final long duration; // 时长，单位：毫秒
    private final String format;
    private final int bitrate; // 比特率
    private final int sampleRate; // 采样率
    private final int channels; // 声道数
    private final long fileSize;

    /**
     * 无参构造函数，用于反序列化
     */
    protected AudioOutput() {
        super();
        this.duration = 0;
        this.format = null;
        this.bitrate = 0;
        this.sampleRate = 0;
        this.channels = 0;
        this.fileSize = 0;
    }

    public AudioOutput(String url, long duration, String format, int bitrate,
                      int sampleRate, int channels, long fileSize) {
        super(url);
        this.duration = duration;
        this.format = format;
        this.bitrate = bitrate;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.fileSize = fileSize;
    }

    public long getDuration() {
        return duration;
    }

    public String getFormat() {
        return format;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "AudioOutput{" +
                "url='" + getUrl() + '\'' +
                ", duration=" + duration +
                ", format='" + format + '\'' +
                ", bitrate=" + bitrate +
                ", sampleRate=" + sampleRate +
                ", channels=" + channels +
                ", fileSize=" + fileSize +
                '}';
    }
}
