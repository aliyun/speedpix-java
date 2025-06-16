package com.aliyun.speedpix.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阿里云 API 网关认证工具类
 */
public class AuthUtils {

    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";
    private static final String GMT_TIMEZONE = "GMT";

    /**
     * 生成阿里云 API 网关认证头
     *
     * @param httpMethod HTTP请求方法，如GET、POST等
     * @param path 请求路径
     * @param appKey 应用Key
     * @param appSecret 应用Secret
     * @param requestBody 请求体内容
     * @param customHeaders 自定义请求头
     * @return 包含认证信息的请求头Map
     */
    public static Map<String, String> generateAuthHeaders(
            String httpMethod,
            String path,
            String appKey,
            String appSecret,
            String requestBody,
            Map<String, String> customHeaders) {

        // 生成时间戳相关信息
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(GMT_TIMEZONE));
        String dateStr = sdf.format(new Date(timestamp)) + " " + GMT_TIMEZONE + "+00:00";
        String timestampStr = String.valueOf(timestamp);
        String nonce = UUID.randomUUID().toString();
        String contentType = "application/json; charset=utf-8";

        // 构建基础头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("date", dateStr);
        headers.put("x-ca-key", appKey);
        headers.put("x-ca-timestamp", timestampStr);
        headers.put("x-ca-nonce", nonce);
        headers.put("x-ca-signature-method", "HmacSHA256");
        headers.put("x-ca-signature-headers", "x-ca-timestamp,x-ca-key,x-ca-nonce,x-ca-signature-method");
        headers.put("Content-Type", contentType);
        headers.put("Accept", contentType);

        // 构建签名字符串
        List<String> signatureParts = new ArrayList<>();
        signatureParts.add(httpMethod);
        signatureParts.add(contentType); // Accept

        // 处理请求体的 MD5
        if (requestBody != null && !requestBody.isEmpty()) {
            byte[] bodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            String bodyMd5 = Base64.encodeBase64String(DigestUtils.md5(bodyBytes));
            headers.put("content-md5", bodyMd5);
            signatureParts.add(bodyMd5);
        } else {
            signatureParts.add("");
        }

        signatureParts.add(contentType); // Content-Type
        signatureParts.add(dateStr);
        signatureParts.add("x-ca-key:" + appKey);
        signatureParts.add("x-ca-nonce:" + nonce);
        signatureParts.add("x-ca-signature-method:HmacSHA256");
        signatureParts.add("x-ca-timestamp:" + timestampStr);
        signatureParts.add(path);

        // 生成签名
        String signatureString = String.join("\n", signatureParts);
        String signature = Base64.encodeBase64String(
            new HmacUtils(HmacAlgorithms.HMAC_SHA_256, appSecret).hmac(signatureString));
        headers.put("x-ca-signature", signature);

        // 合并自定义头信息
        if (customHeaders != null) {
            headers.putAll(customHeaders);
        }

        return headers;
    }
}
