package com.aliyun.speedpix.model;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 文件输出包装器，支持从 URL 下载和保存文件
 */
public class FileOutput implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final String url;
    protected transient byte[] content;
    protected static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build();

    /**
     * 无参构造函数，用于反序列化
     */
    protected FileOutput() {
        this.url = null;
    }

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
            if (url == null) {
                throw new IllegalStateException("URL cannot be null");
            }
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
     * 获取文件输入流（零拷贝）
     */
    public InputStream getInputStream() throws IOException {
        if (url == null) {
            throw new IllegalStateException("URL cannot be null");
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            response.close();
            throw new IOException("Failed to download file: HTTP " + response.code());
        }
        if (response.body() == null) {
            response.close();
            throw new IOException("Empty response body");
        }
        return response.body().byteStream();
    }

    /**
     * 将文件内容写入到输出流（零拷贝）
     */
    public void writeTo(OutputStream outputStream) throws IOException {
        try (InputStream inputStream = getInputStream()) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    /**
     * 将文件内容写入到输出流，使用自定义缓冲区大小（零拷贝）
     */
    public void writeTo(OutputStream outputStream, int bufferSize) throws IOException {
        try (InputStream inputStream = getInputStream()) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }

    /**
     * 保存文件到本地
     */
    public void save(String path) throws IOException {
        save(Paths.get(path));
    }

    /**
     * 保存文件到本地（零拷贝版本）
     */
    public void save(Path path) throws IOException {
        try (InputStream inputStream = getInputStream()) {
            Files.copy(inputStream, path);
        }
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
