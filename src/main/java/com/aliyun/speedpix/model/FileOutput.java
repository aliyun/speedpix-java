package com.aliyun.speedpix.model;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 文件输出包装器，支持从 URL 下载和保存文件
 */
public class FileOutput {
    private final String url;
    private byte[] content;
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build();

    public FileOutput(String url) {
        this.url = url;
    }

    /**
     * 获取文件 URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 读取文件内容
     */
    public byte[] read() throws IOException {
        if (content == null) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to download file: HTTP " + response.code());
                }
                if (response.body() == null) {
                    throw new IOException("Empty response body");
                }
                content = response.body().bytes();
            }
        }
        return content;
    }

    /**
     * 保存文件到本地
     */
    public void save(String path) throws IOException {
        save(Paths.get(path));
    }

    /**
     * 保存文件到本地
     */
    public void save(Path path) throws IOException {
        byte[] data = read();
        Files.write(path, data);
    }

    @Override
    public String toString() {
        return "FileOutput{url='" + url + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FileOutput that = (FileOutput) obj;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
