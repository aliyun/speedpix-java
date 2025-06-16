package com.aliyun.speedpix.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 输出转换工具类
 *
 * 提供将 Map&lt;String, Object&gt; 数据转换为用户定义数据结构的功能。
 * 使用 Jackson ObjectMapper 进行类型转换。
 */
public class OutputConverterUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 Map 数据转换为指定类型的对象
     *
     * @param data 原始数据 Map
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象，如果 data 为 null 则返回 null
     */
    public static <T> T convertTo(Map<String, Object> data, Class<T> targetClass) {
        if (data == null) {
            return null;
        }
        return objectMapper.convertValue(data, targetClass);
    }

    /**
     * 检查 Map 数据是否为 null 或空
     *
     * @param data 数据 Map
     * @return 如果 data 为 null 或空则返回 true
     */
    public static boolean isEmpty(Map<String, Object> data) {
        return data == null || data.isEmpty();
    }

    /**
     * 获取内部使用的 ObjectMapper 实例
     *
     * @return ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
