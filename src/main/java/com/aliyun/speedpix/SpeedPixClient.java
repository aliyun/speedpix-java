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

    /**
     * 默认构造函数 - 从环境变量读取所有配置
     * 需要设置 SPEEDPIX_APP_KEY 和 SPEEDPIX_APP_SECRET 环境变量
     */
    public SpeedPixClient() {
        this(null, null, null, null, 30);
    }

    /**
     * 最简构造函数 - 推荐使用
     * 使用默认endpoint，仅需要提供必需的认证信息
     *
     * @param appKey 应用密钥
     * @param appSecret 应用密码
     */
    public SpeedPixClient(String appKey, String appSecret) {
        this(null, appKey, appSecret, null, 30);
    }

    /**
     * 标准构造函数 - 自定义endpoint
     *
     * @param endpoint API端点 (可选，默认: https://openai.edu-aliyun.com)
     * @param appKey 应用密钥
     * @param appSecret 应用密码
     */
    public SpeedPixClient(String endpoint, String appKey, String appSecret) {
        this(endpoint, appKey, appSecret, null, 30);
    }

    /**
     * 完整构造函数 - 自定义所有参数
     *
     * @param endpoint API端点 (可选，默认: https://openai.edu-aliyun.com)
     * @param appKey 应用密钥
     * @param appSecret 应用密码
     * @param userAgent 用户代理 (可选，默认: speedpix-java/1.0.0)
     * @param timeoutSeconds 超时时间秒数 (默认: 30)
     */
    public SpeedPixClient(String endpoint, String appKey, String appSecret, String userAgent, int timeoutSeconds) {
        // 设置 endpoint，支持 null 和空字符串都使用默认值
        String envEndpoint = System.getenv("SPEEDPIX_ENDPOINT");
        if (endpoint != null && !endpoint.trim().isEmpty()) {
            this.endpoint = endpoint;
        } else if (envEndpoint != null && !envEndpoint.trim().isEmpty()) {
            this.endpoint = envEndpoint;
        } else {
            this.endpoint = "https://openai.edu-aliyun.com";
        }
        this.appKey = appKey != null ? appKey : System.getenv("SPEEDPIX_APP_KEY");
        this.appSecret = appSecret != null ? appSecret : System.getenv("SPEEDPIX_APP_SECRET");
        this.userAgent = userAgent != null ? userAgent : "speedpix-java/1.0.0";

        // 验证必需参数 (endpoint 现在有默认值，不再必需)
        if (this.appKey == null || this.appKey.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "appKey is required, set SPEEDPIX_APP_KEY env var or pass appKey parameter");
        }
        if (this.appSecret == null || this.appSecret.trim().isEmpty()) {
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
     *
     * @return PredictionsService实例
     */
    public PredictionsService predictions() {
        return predictionsService;
    }

    /**
     * 获取文件服务
     *
     * @return FilesService实例
     */
    public FilesService files() {
        return filesService;
    }

    /**
     * 运行模型并返回结果
     *
     * @param <T> 返回结果的类型
     * @param workflowId 工作流ID
     * @param input 输入参数Map
     * @param targetClass 目标结果类型
     * @return 预测结果
     * @throws SpeedPixException SpeedPix业务异常
     * @throws InterruptedException 线程中断异常
     */
    public <T> Prediction<T> run(String workflowId, Map<String, Object> input, Class<T> targetClass)
        throws SpeedPixException,
        InterruptedException {
        return run(workflowId, input, true, null, "main", null, null, "default", 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest）
     *
     * @param <T> 返回结果的类型
     * @param request ComfyPrompt请求对象
     * @param targetClass 目标结果类型
     * @return 预测结果
     * @throws SpeedPixException SpeedPix业务异常
     * @throws InterruptedException 线程中断异常
     */
    public <T> Prediction<T> run(ComfyPromptRequest request, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return run(request, "default", true, 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest，支持resourceConfigId）
     *
     * @param <T> 返回结果的类型
     * @param request ComfyPrompt请求对象
     * @param resourceConfigId 资源配置ID
     * @param targetClass 目标结果类型
     * @return 预测结果
     * @throws SpeedPixException SpeedPix业务异常
     * @throws InterruptedException 线程中断异常
     */
    public <T> Prediction<T> run(ComfyPromptRequest request, String resourceConfigId, Class<T> targetClass)
        throws SpeedPixException, InterruptedException {
        return run(request, resourceConfigId, true, 1.0, targetClass);
    }

    /**
     * 运行模型并返回结果（使用ComfyPromptRequest，支持完整参数）
     *
     * @param <T> 返回结果的类型
     * @param request ComfyPrompt请求对象
     * @param resourceConfigId 资源配置ID
     * @param wait 是否等待结果完成
     * @param pollingInterval 轮询间隔（秒）
     * @param targetClass 目标结果类型
     * @return 预测结果
     * @throws SpeedPixException SpeedPix业务异常
     * @throws InterruptedException 线程中断异常
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

    /**
     * SpeedPixClient Builder - 提供流式API构建客户端
     *
     * 使用示例:
     * <pre>
     * SpeedPixClient client = SpeedPixClient.builder()
     *     .appKey("your-app-key")
     *     .appSecret("your-app-secret")
     *     .endpoint("https://custom-endpoint.com")  // 可选
     *     .userAgent("my-app/1.0.0")               // 可选
     *     .timeoutSeconds(60)                      // 可选
     *     .build();
     * </pre>
     */
    public static class Builder {
        private String endpoint;
        private String appKey;
        private String appSecret;
        private String userAgent;
        private int timeoutSeconds = 30;

        /**
         * 设置API端点
         * @param endpoint API端点URL (可选，默认: https://openai.edu-aliyun.com)
         * @return Builder实例
         */
        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * 设置应用密钥
         * @param appKey 应用密钥
         * @return Builder实例
         */
        public Builder appKey(String appKey) {
            this.appKey = appKey;
            return this;
        }

        /**
         * 设置应用密码
         * @param appSecret 应用密码
         * @return Builder实例
         */
        public Builder appSecret(String appSecret) {
            this.appSecret = appSecret;
            return this;
        }

        /**
         * 设置用户代理
         * @param userAgent 用户代理字符串
         * @return Builder实例
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * 设置超时时间
         * @param timeoutSeconds 超时时间(秒)
         * @return Builder实例
         */
        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        /**
         * 从环境变量自动配置认证信息
         * @return Builder实例
         */
        public Builder fromEnv() {
            this.appKey = System.getenv("SPEEDPIX_APP_KEY");
            this.appSecret = System.getenv("SPEEDPIX_APP_SECRET");
            String envEndpoint = System.getenv("SPEEDPIX_ENDPOINT");
            if (envEndpoint != null && !envEndpoint.trim().isEmpty()) {
                this.endpoint = envEndpoint;
            }
            return this;
        }

        /**
         * 构建SpeedPixClient实例
         * @return SpeedPixClient实例
         */
        public SpeedPixClient build() {
            return new SpeedPixClient(endpoint, appKey, appSecret, userAgent, timeoutSeconds);
        }
    }

    /**
     * 创建Builder实例
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
}
