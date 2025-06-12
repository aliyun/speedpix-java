package com.aliyun.speedpix.util;

import com.aliyun.speedpix.model.FileOutput;

import java.util.*;

/**
 * 输出转换工具类
 * 将 API 响应中的 HTTP/HTTPS URL 转换为 FileOutput 对象
 */
public class OutputTransformUtils {

    /**
     * 转换预测输出，处理返回的 URL 格式
     * 将 URL 字符串转换为 FileOutput 对象以支持 .save() 和 .read() 方法
     *
     * @param value 要转换的对象
     * @return 转换后的对象，URL 字符串会被转换为 FileOutput 对象
     */
    public static Object transformOutput(Object value) {
        if (value == null) {
            return null;
        }

        // 处理 Map
        if (value instanceof Map) {
            Map<?, ?> sourceMap = (Map<?, ?>) value;
            Map<String, Object> resultMap = new HashMap<>();

            for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
                String key = entry.getKey() != null ? entry.getKey().toString() : null;
                Object transformedValue = transformOutput(entry.getValue());
                resultMap.put(key, transformedValue);
            }

            return resultMap;
        }

        // 处理 List
        if (value instanceof List) {
            List<?> sourceList = (List<?>) value;
            List<Object> resultList = new ArrayList<>();

            for (Object item : sourceList) {
                resultList.add(transformOutput(item));
            }

            return resultList;
        }

        // 处理数组
        if (value instanceof Object[]) {
            Object[] sourceArray = (Object[]) value;
            Object[] resultArray = new Object[sourceArray.length];

            for (int i = 0; i < sourceArray.length; i++) {
                resultArray[i] = transformOutput(sourceArray[i]);
            }

            return resultArray;
        }

        // 处理 Set
        if (value instanceof Set) {
            Set<?> sourceSet = (Set<?>) value;
            Set<Object> resultSet = new HashSet<>();

            for (Object item : sourceSet) {
                resultSet.add(transformOutput(item));
            }

            return resultSet;
        }

        // 处理字符串 - 检查是否为 HTTP/HTTPS URL
        if (value instanceof String) {
            String str = (String) value;
            if (isHttpUrl(str)) {
                return new FileOutput(str);
            }
            return str;
        }

        // 其他类型直接返回
        return value;
    }

    /**
     * 检查字符串是否为 HTTP/HTTPS URL
     *
     * @param str 要检查的字符串
     * @return 如果是 HTTP/HTTPS URL 返回 true，否则返回 false
     */
    private static boolean isHttpUrl(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }

        String trimmed = str.trim().toLowerCase();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }

    /**
     * 递归转换对象的便捷方法
     * 主要用于处理深度嵌套的数据结构
     *
     * @param obj 要转换的对象
     * @param <T> 对象类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T transform(T obj) {
        return (T) transformOutput(obj);
    }
}
