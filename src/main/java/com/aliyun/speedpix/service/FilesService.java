package com.aliyun.speedpix.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.FileObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件服务类
 */
public class FilesService {

    private final SpeedPixClient client;

    public FilesService(SpeedPixClient client) {
        this.client = client;
    }

    /**
     * 上传文件
     */
    public FileObject create(File file) throws SpeedPixException {
        if (!file.exists()) {
            throw new SpeedPixException("文件不存在: " + file.getAbsolutePath());
        }

        try {
            String contentType = getContentType(file.getName());
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            return uploadFile(file.getName(), fileBytes, contentType);
        } catch (IOException e) {
            throw new SpeedPixException("读取文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件
     */
    public FileObject create(Path path) throws SpeedPixException {
        return create(path.toFile());
    }

    /**
     * 上传文件
     */
    public FileObject create(InputStream inputStream, String filename) throws SpeedPixException {
        try {
            byte[] fileBytes = inputStreamToByteArray(inputStream);
            String contentType = getContentType(filename);
            return uploadFile(filename, fileBytes, contentType);
        } catch (IOException e) {
            throw new SpeedPixException("读取输入流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 实际执行文件上传
     */
    private FileObject uploadFile(String filename, byte[] fileBytes, String contentType) throws SpeedPixException {
        try {
            RequestBody fileBody = RequestBody.create(fileBytes, MediaType.parse(contentType));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename, fileBody)
                    .build();

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "multipart/form-data");

            Response response = client.executeRequest("POST", "/files", headers, requestBody);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.body().string(), FileObject.class);
        } catch (Exception e) {
            throw new SpeedPixException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据文件名获取内容类型
     */
    private String getContentType(String filename) {
        String extension = filename.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else if (extension.endsWith(".mp4")) {
            return "video/mp4";
        } else if (extension.endsWith(".pdf")) {
            return "application/pdf";
        } else {
            return "application/octet-stream";
        }
    }

    /**
     * 将输入流转换为字节数组
     */
    private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
