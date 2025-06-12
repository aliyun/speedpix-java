package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.service.FilesService;
import com.aliyun.speedpix.service.PredictionsService;
import com.aliyun.speedpix.util.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SpeedPix API 客户端
 */
public class SpeedPixClient {

    private final String endpoint;
    private final String appKey;
    private final String appSecret;
    private final String userAgent;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // 服务实例
    private final PredictionsService predictionsService;
    private final FilesService filesService;

    public SpeedPixClient() {
        this(null, null, null, null, 30);
    }

    /**
     * 构造函数
     */
    public SpeedPixClient(String endpoint, String appKey, String appSecret) {
        this(endpoint, appKey, appSecret, null, 30);
    }

    /**
     * 构造函数
     */
    public SpeedPixClient(String endpoint, String appKey, String appSecret, String userAgent, int timeoutSeconds) {
        this.endpoint = endpoint != null ? endpoint : System.getenv("SPEEDPIX_ENDPOINT");
        this.appKey = appKey != null ? appKey : System.getenv("SPEEDPIX_APP_KEY");
        this.appSecret = appSecret != null ? appSecret : System.getenv("SPEEDPIX_APP_SECRET");
        this.userAgent = userAgent != null ? userAgent : "speedpix-java/1.0.0";

        // 验证必需参数
        if (this.endpoint == null || this.endpoint.isEmpty()) {
            throw new IllegalArgumentException(
                "endpoint is required, set SPEEDPIX_ENDPOINT env var or pass endpoint parameter");
        }
        if (this.appKey == null || this.appKey.isEmpty()) {
            throw new IllegalArgumentException(
                "appKey is required, set SPEEDPIX_APP_KEY env var or pass appKey parameter");
        }
        if (this.appSecret == null || this.appSecret.isEmpty()) {
            throw new IllegalArgumentException(
                "appSecret is required, set SPEEDPIX_APP_SECRET env var or pass appSecret parameter");
        }

        // 创建 HTTP 客户端
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build();

        this.objectMapper = new ObjectMapper();

        // 初始化服务
        this.predictionsService = new PredictionsService(this);
        this.filesService = new FilesService(this);
    }

    /**
     * 获取预测服务
     */
    public PredictionsService predictions() {
        return predictionsService;
    }

    /**
     * 获取文件服务
     */
    public FilesService files() {
        return filesService;
    }

    /**
     * 运行模型并返回结果
     */
    public <T> Prediction<T> run(String workflowId, Map<String, Object> input, Class<T> targetClass)
        throws SpeedPixException,
        InterruptedException {
        return run(workflowId, input, true, null, "main", null, null, "default", 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest）
     */
    public <T> Prediction<T> run(ComfyPromptRequest request, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return run(request, "default", true, 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest）
     */
    public <T> Prediction<T> run(ComfyPromptRequest request, String resourceConfigId, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return run(request, resourceConfigId, true, 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest）
     */
    public <T> Prediction<T> run(ComfyPromptRequest request, String resourceConfigId, boolean wait,
        double pollingInterval, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        // 创建预测任务
        Prediction<T> prediction = predictionsService.create(request, resourceConfigId, targetClass);

        if (!wait) {
            return prediction;
        }

        // 等待完成 - 直接在客户端中实现等待逻辑
        while (!prediction.isFinished()) {
            Thread.sleep((long)(pollingInterval * 1000));
            prediction = predictionsService.get(prediction.getId(), targetClass);
        }

        if (prediction.getError() != null) {
            throw new PredictionException(prediction);
        }

        return prediction;
    }

    /**
     * 运行模型并返回结果（兼容性方法）
     */
    public <T> Prediction<T> run(
        String workflowId,
        Map<String, Object> input,
        boolean wait,
        String versionId,
        String aliasId,
        Boolean randomiseSeeds,
        Boolean returnTempFiles,
        String resourceConfigId,
        double pollingInterval,
        Class<T> targetClass
    ) throws SpeedPixException, InterruptedException {

        // 使用Builder模式创建ComfyPromptRequest
        ComfyPromptRequest request = ComfyPromptRequest.builder(workflowId)
            .inputs(input)
            .versionId(versionId)
            .aliasId(aliasId)
            .randomiseSeeds(randomiseSeeds)
            .returnTempFiles(returnTempFiles)
            .build();

        return run(request, resourceConfigId, wait, pollingInterval, targetClass);
    }

    /**
     * 发送 POST 请求
     */
    public <T> T post(String path, Object requestBody, Class<T> responseClass) throws SpeedPixException {
        return request("POST", path, requestBody, responseClass);
    }

    /**
     * 发送 POST 请求（支持自定义头部）
     */
    public <T> T post(String path, Object requestBody, Class<T> responseClass, Map<String, String> headers)
        throws SpeedPixException {
        try {
            String requestBodyJson = null;
            if (requestBody != null) {
                requestBodyJson = objectMapper.writeValueAsString(requestBody);
            }

            RequestBody body = requestBodyJson != null ?
                RequestBody.create(requestBodyJson, MediaType.get("application/json; charset=utf-8")) :
                RequestBody.create("", MediaType.get("application/json; charset=utf-8"));

            try (Response response = executeRequest("POST", path, headers, body)) {
                if (response.body() == null) {
                    throw new SpeedPixException("Empty response body");
                }

                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, responseClass);
            }
        } catch (IOException e) {
            throw new SpeedPixException("Request failed", e);
        }
    }

    /**
     * 发送 HTTP 请求
     */
    private <T> T request(String method, String path, Object requestBody, Class<T> responseClass)
        throws SpeedPixException {
        try {
            String url = buildUrl(path);
            String requestBodyJson = null;

            if (requestBody != null) {
                requestBodyJson = objectMapper.writeValueAsString(requestBody);
            }

            // 生成认证头
            Map<String, String> authHeaders = AuthUtils.generateAuthHeaders(
                method, path, appKey, appSecret, requestBodyJson, null
            );

            // 构建请求
            Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", userAgent);

            // 添加认证头
            for (Map.Entry<String, String> header : authHeaders.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }

            // 设置请求体
            if ("POST".equals(method) && requestBodyJson != null) {
                RequestBody body = RequestBody.create(
                    requestBodyJson,
                    MediaType.get("application/json; charset=utf-8")
                );
                requestBuilder.post(body);
            } else if ("GET".equals(method)) {
                requestBuilder.get();
            }

            Request request = requestBuilder.build();

            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorMessage = "HTTP " + response.code() + ": " + response.message();
                    if (response.body() != null) {
                        try {
                            String errorBody = response.body().string();
                            // 尝试解析错误响应
                            Map<?, ?> errorData = objectMapper.readValue(errorBody, Map.class);
                            String subErrMessage = (String)errorData.get("subErrMessage");
                            String errMessage = (String)errorData.get("errMessage");
                            String apiInvokeId = (String)errorData.get("apiInvokeId");

                            if (subErrMessage != null) {
                                errorMessage = subErrMessage;
                            } else if (errMessage != null) {
                                errorMessage = errMessage;
                            }

                            throw new SpeedPixException(errorMessage, null, apiInvokeId);
                        } catch (JsonProcessingException e) {
                            // 如果解析失败，使用原始错误信息
                        }
                    }
                    throw new SpeedPixException(errorMessage);
                }

                if (response.body() == null) {
                    throw new SpeedPixException("Empty response body");
                }

                String responseBody = response.body().string();
                return objectMapper.readValue(responseBody, responseClass);
            }

        } catch (IOException e) {
            throw new SpeedPixException("Request failed", e);
        }
    }

    /**
     * 执行 HTTP 请求（支持自定义请求体）
     */
    public Response executeRequest(String method, String path, Map<String, String> additionalHeaders,
        RequestBody requestBody) throws SpeedPixException {
        try {
            String url = buildUrl(path);

            // 构建请求
            Request.Builder requestBuilder = new Request.Builder().url(url);

            // 添加认证和其他默认头部
            Map<String, String> headers = AuthUtils.generateAuthHeaders(method, path, appKey, appSecret, null, null);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }

            // 添加额外的头部
            if (additionalHeaders != null) {
                for (Map.Entry<String, String> header : additionalHeaders.entrySet()) {
                    // 跳过 Content-Type，因为 MultipartBody 会自动设置
                    if (!"Content-Type".equalsIgnoreCase(header.getKey())) {
                        requestBuilder.addHeader(header.getKey(), header.getValue());
                    }
                }
            }

            // 设置请求方法和请求体
            if ("POST".equals(method) && requestBody != null) {
                requestBuilder.post(requestBody);
            } else if ("PUT".equals(method) && requestBody != null) {
                requestBuilder.put(requestBody);
            } else if ("GET".equals(method)) {
                requestBuilder.get();
            } else if ("DELETE".equals(method)) {
                requestBuilder.delete();
            }

            Request request = requestBuilder.build();

            // 发送请求
            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorMessage = "HTTP " + response.code() + ": " + response.message();
                if (response.body() != null) {
                    try {
                        String errorBody = response.body().string();
                        // 尝试解析错误响应
                        Map<?, ?> errorData = objectMapper.readValue(errorBody, Map.class);
                        String subErrMessage = (String)errorData.get("subErrMessage");
                        String errMessage = (String)errorData.get("errMessage");
                        String apiInvokeId = (String)errorData.get("apiInvokeId");

                        if (subErrMessage != null) {
                            errorMessage = subErrMessage;
                        } else if (errMessage != null) {
                            errorMessage = errMessage;
                        }

                        throw new SpeedPixException(errorMessage, null, apiInvokeId);
                    } catch (JsonProcessingException e) {
                        // 如果解析失败，使用原始错误信息
                    }
                }
                throw new SpeedPixException(errorMessage);
            }

            return response;

        } catch (IOException e) {
            throw new SpeedPixException("Request failed", e);
        }
    }

    /**
     * 构建完整的 URL
     */
    private String buildUrl(String path) {
        return endpoint + path;
    }
}
