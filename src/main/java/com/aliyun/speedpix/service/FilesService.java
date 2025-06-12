package com.aliyun.speedpix.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.FileObject;
import com.aliyun.speedpix.model.FileUploadOptions;
import com.aliyun.speedpix.model.FileUploadSignResponse;
import com.aliyun.speedpix.util.MimeTypeDetector;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件服务类
 * 实现两步上传流程：1. 获取上传签名 2. PUT 上传文件
 */
public class FilesService {

    private final SpeedPixClient client;

    public FilesService(SpeedPixClient client) {
        this.client = client;
    }

    /**
     * 上传文件（使用 File 对象）
     */
    public FileObject create(File file) throws SpeedPixException {
        if (!file.exists()) {
            throw new SpeedPixException("文件不存在: " + file.getAbsolutePath());
        }

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String contentType = MimeTypeDetector.guessMimeTypeFromFilename(file.getName());
            return uploadFileWithTwoStepProcess(file.getName(), fileBytes, contentType);
        } catch (IOException e) {
            throw new SpeedPixException("读取文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件（使用 Path 对象）
     */
    public FileObject create(Path path) throws SpeedPixException {
        return create(path.toFile());
    }

    /**
     * 上传文件（使用 InputStream，需要提供文件名）
     */
    public FileObject create(InputStream inputStream, String filename) throws SpeedPixException {
        try {
            byte[] fileBytes = inputStreamToByteArray(inputStream);
            String contentType = detectContentTypeFromStream(fileBytes, filename);
            return uploadFileWithTwoStepProcess(filename, fileBytes, contentType);
        } catch (IOException e) {
            throw new SpeedPixException("读取输入流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件（使用 InputStream 和选项）
     */
    public FileObject create(InputStream inputStream, FileUploadOptions options) throws SpeedPixException {
        if (options == null) {
            throw new SpeedPixException("FileUploadOptions 不能为空");
        }

        String filename = options.getFilename();
        String contentType = options.getContentType();

        if (filename == null || filename.trim().isEmpty()) {
            filename = "file";
        }

        try {
            byte[] fileBytes = inputStreamToByteArray(inputStream);

            if (contentType == null || contentType.trim().isEmpty()) {
                contentType = detectContentTypeFromStream(fileBytes, filename);
            }

            return uploadFileWithTwoStepProcess(filename, fileBytes, contentType);
        } catch (IOException e) {
            throw new SpeedPixException("读取输入流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件（使用字节数组）
     */
    public FileObject create(byte[] fileBytes, String filename, String contentType) throws SpeedPixException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new SpeedPixException("文件内容不能为空");
        }

        if (filename == null || filename.trim().isEmpty()) {
            filename = "file";
        }

        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = MimeTypeDetector.detectMimeType(fileBytes);
        }

        return uploadFileWithTwoStepProcess(filename, fileBytes, contentType);
    }

    /**
     * 实际执行两步上传流程
     * Step 1: 获取上传签名
     * Step 2: PUT 上传文件到签名 URL
     */
    private FileObject uploadFileWithTwoStepProcess(String filename, byte[] fileBytes, String contentType) throws SpeedPixException {
        try {
            // Step 1: 获取上传签名
            FileUploadSignResponse signResponse = getUploadSignature(filename, contentType, fileBytes.length);

            if (signResponse.getData() == null || signResponse.getData().getUploadUrl() == null) {
                throw new SpeedPixException("获取上传签名失败：响应数据为空");
            }

            String uploadUrl = signResponse.getData().getUploadUrl();

            // Step 2: 使用 PUT 方法上传文件
            uploadFileToPutUrl(uploadUrl, fileBytes, contentType);

            // Step 3: 构造并返回 FileObject
            return createFileObjectFromSignResponse(signResponse, filename, contentType, fileBytes.length);

        } catch (Exception e) {
            throw new SpeedPixException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取上传签名
     */
    private FileUploadSignResponse getUploadSignature(String filename, String contentType, long fileSize) throws SpeedPixException {
        try {
            Map<String, Object> signRequest = new HashMap<>();
            signRequest.put("originalFilename", filename);
            signRequest.put("contentType", contentType);
            signRequest.put("size", fileSize);

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(signRequest);

            RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            Response response = client.executeRequest("POST", "/scc/sp_create_temp_file_upload_sign", headers, requestBody);

            if (!response.isSuccessful()) {
                throw new SpeedPixException("获取上传签名失败，HTTP状态码: " + response.code());
            }

            String responseBody = response.body().string();
            return mapper.readValue(responseBody, FileUploadSignResponse.class);

        } catch (Exception e) {
            throw new SpeedPixException("获取上传签名失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 PUT 方法上传文件到签名 URL
     */
    private void uploadFileToPutUrl(String uploadUrl, byte[] fileBytes, String contentType) throws SpeedPixException {
        try {
            RequestBody fileBody = RequestBody.create(fileBytes, MediaType.parse(contentType));

            Request.Builder requestBuilder = new Request.Builder()
                    .url(uploadUrl)
                    .put(fileBody)
                    .addHeader("Content-Type", contentType)
                    .addHeader("Content-Length", String.valueOf(fileBytes.length));

            OkHttpClient httpClient = new OkHttpClient();
            Response response = httpClient.newCall(requestBuilder.build()).execute();

            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无错误详情";
                throw new SpeedPixException("上传文件失败，HTTP状态码: " + response.code() + ", 错误详情: " + errorBody);
            }

        } catch (Exception e) {
            throw new SpeedPixException("上传文件到签名URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从签名响应构造 FileObject
     */
    private FileObject createFileObjectFromSignResponse(FileUploadSignResponse signResponse, String filename, String contentType, long fileSize) {
        FileUploadSignResponse.Data data = signResponse.getData();

        FileObject fileObject = new FileObject();
        fileObject.setPath(data.getPath());
        fileObject.setAccessUrl(data.getAccessUrl());
        fileObject.setUploadUrl(data.getUploadUrl());
        fileObject.setObjectKey(data.getObjectKey());
        fileObject.setName(filename);
        fileObject.setContentType(contentType);
        fileObject.setSize(fileSize);

        // 处理 expireTime (可能是字符串或数字)
        if (data.getExpireTime() != null) {
            if (data.getExpireTime() instanceof String) {
                fileObject.setExpireTime((String) data.getExpireTime());
            } else if (data.getExpireTime() instanceof Number) {
                fileObject.setExpireTime(String.valueOf(data.getExpireTime()));
            }
        }

        return fileObject;
    }

    /**
     * 从 InputStream 和文件名检测内容类型
     */
    private String detectContentTypeFromStream(byte[] fileBytes, String filename) {
        // 首先尝试从文件内容检测
        String detectedType = MimeTypeDetector.detectMimeType(fileBytes);

        // 如果检测为通用类型，尝试从文件名猜测
        if ("application/octet-stream".equals(detectedType) && filename != null) {
            String guessedType = MimeTypeDetector.guessMimeTypeFromFilename(filename);
            if (!"application/octet-stream".equals(guessedType)) {
                return guessedType;
            }
        }

        return detectedType;
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
