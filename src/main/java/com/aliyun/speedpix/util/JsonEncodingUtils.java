package com.aliyun.speedpix.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 编码工具类，处理文件上传和编码
 */
public class JsonEncodingUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_BASE64_FILE_SIZE = 1024 * 1024; // 1MB

    /**
     * 文件编码策略
     */
    public enum FileEncodingStrategy {
        BASE64,
        URL
    }

    /**
     * 编码对象，处理文件上传
     */
    public static Object encodeJson(Object obj, FileEncodingStrategy strategy) throws IOException {
        if (obj == null) {
            return null;
        }

        // 处理 Map
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            Map<Object, Object> result = new HashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(entry.getKey(), encodeJson(entry.getValue(), strategy));
            }
            return result;
        }

        // 处理 Iterable (List, Set, etc.)
        if (obj instanceof Iterable) {
            // 这里简化处理，实际项目中可能需要更复杂的逻辑
            return obj;
        }

        // 处理文件路径字符串
        if (obj instanceof String) {
            String str = (String) obj;
            Path path = Paths.get(str);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                try {
                    return encodeFile(path, strategy);
                } catch (IOException e) {
                    // 如果文件处理失败，返回原字符串
                    return obj;
                }
            }
            return obj;
        }

        // 处理 Path 对象
        if (obj instanceof Path) {
            Path path = (Path) obj;
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return encodeFile(path, strategy);
            }
            return obj.toString();
        }

        // 处理 File 对象
        if (obj instanceof File) {
            File file = (File) obj;
            if (file.exists() && file.isFile()) {
                return encodeFile(file.toPath(), strategy);
            }
            return file.getAbsolutePath();
        }

        // 处理 InputStream
        if (obj instanceof InputStream) {
            if (strategy == FileEncodingStrategy.BASE64) {
                return encodeInputStreamToBase64((InputStream) obj);
            } else {
                throw new UnsupportedOperationException("URL encoding for InputStream requires file upload implementation");
            }
        }

        return obj;
    }

    /**
     * 编码文件
     */
    private static Object encodeFile(Path path, FileEncodingStrategy strategy) throws IOException {
        if (strategy == FileEncodingStrategy.BASE64) {
            long fileSize = Files.size(path);
            if (fileSize > MAX_BASE64_FILE_SIZE) {
                throw new IOException("文件过大，base64 编码仅支持小于 1MB 的文件");
            }
            byte[] fileBytes = Files.readAllBytes(path);
            return Base64.encodeBase64String(fileBytes);
        } else {
            // URL 策略需要实际的文件上传实现
            throw new UnsupportedOperationException("URL encoding requires file upload implementation");
        }
    }

    /**
     * 将 InputStream 编码为 Base64
     */
    private static String encodeInputStreamToBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        byte[] bytes = buffer.toByteArray();
        if (bytes.length > MAX_BASE64_FILE_SIZE) {
            throw new IOException("文件过大，base64 编码仅支持小于 1MB 的文件");
        }

        return Base64.encodeBase64String(bytes);
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String toJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * JSON 字符串转对象
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
}
