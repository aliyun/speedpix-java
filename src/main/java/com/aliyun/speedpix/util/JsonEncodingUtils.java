package com.aliyun.speedpix.util;

import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.service.FilesService;
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
     *
     * @param obj 需要编码的对象Map
     * @param strategy 文件编码策略
     * @param filesService 文件服务实例
     * @return 编码后的对象Map
     * @throws IOException 文件操作异常
     * @throws SpeedPixException SpeedPix业务异常
     */
    public static Map<String, Object> encodeJson(Map<String, Object> obj, FileEncodingStrategy strategy,
        FilesService filesService)
        throws IOException, SpeedPixException {
        if (obj == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.put(key, encodeValue(value, strategy, filesService));
        }
        return result;
    }

    private static Object encodeValue(Object obj, FileEncodingStrategy strategy, FilesService filesService)
        throws IOException, SpeedPixException {
        if (obj == null) {
            return null;
        }

        // 处理 Iterable (List, Set, etc.)
        if (obj instanceof Iterable) {
            // 这里简化处理，实际项目中可能需要更复杂的逻辑
            return obj;
        }

        // 处理文件路径字符串
        if (obj instanceof String) {
            String str = (String)obj;
            Path path = Paths.get(str);
            if (Files.exists(path) && Files.isRegularFile(path)) {
                try {
                    return encodeFile(path, strategy, filesService);
                } catch (IOException e) {
                    // 如果文件处理失败，返回原字符串
                    return obj;
                }
            }
            return obj;
        }

        // 处理 Path 对象
        if (obj instanceof Path) {
            Path path = (Path)obj;
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return encodeFile(path, strategy, filesService);
            }
            return obj.toString();
        }

        // 处理 File 对象
        if (obj instanceof File) {
            File file = (File)obj;
            if (file.exists() && file.isFile()) {
                return encodeFile(file.toPath(), strategy, filesService);
            }
            return file.getAbsolutePath();
        }

        // 处理 InputStream
        if (obj instanceof InputStream) {
            if (strategy == FileEncodingStrategy.BASE64) {
                return encodeInputStreamToBase64((InputStream)obj);
            } else {
                // 使用带 FileUploadOptions 的方法避免歧义性
                return filesService.create((InputStream)obj,
                    new com.aliyun.speedpix.model.FileUploadOptions("file", null))
                    .getAccessUrl();
            }
        }

        return obj;
    }

    /**
     * 编码文件
     */
    private static Object encodeFile(Path path, FileEncodingStrategy strategy, FilesService filesService)
        throws IOException, SpeedPixException {
        if (strategy == FileEncodingStrategy.BASE64) {
            long fileSize = Files.size(path);
            if (fileSize > MAX_BASE64_FILE_SIZE) {
                throw new IOException("文件过大，base64 编码仅支持小于 1MB 的文件");
            }
            byte[] fileBytes = Files.readAllBytes(path);
            return Base64.encodeBase64String(fileBytes);
        } else {
            return filesService.create(path).getAccessUrl();
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
     *
     * @param obj 需要转换的对象
     * @return JSON字符串
     * @throws JsonProcessingException JSON处理异常
     */
    public static String toJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * JSON 字符串转对象
     *
     * @param <T> 目标类型泛型
     * @param json JSON字符串
     * @param clazz 目标类型的Class对象
     * @return 转换后的对象
     * @throws JsonProcessingException JSON处理异常
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
}
