# SpeedPix Java SDK - Spring Boot 集成指南

本文档提供 SpeedPix Java SDK 与 Spring Boot 完整的集成案例和最佳实践。

## 版本兼容性

| Spring Boot 版本 | Java 版本要求 | 注解包名 | 推荐配置 |
|-----------------|-------------|---------|----------|
| 2.x (2.7.x)    | Java 8-17   | `javax.annotation.*` | 经典配置 |
| 3.x (3.0.x+)    | Java 17+    | `jakarta.annotation.*` | 现代配置 |

## Spring Boot 2.x 集成案例

### 1. 项目依赖配置 (Spring Boot 2.x)

**pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>speedpix-spring-boot-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>11</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpeedPix Java SDK -->
        <dependency>
            <groupId>com.aliyun.speedpix</groupId>
            <artifactId>speedpix-java</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. 配置类 (Spring Boot 2.x)

**SpeedPixConfiguration.java**
```java
package com.example.config;

import com.aliyun.speedpix.SpeedPixClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@EnableConfigurationProperties(SpeedPixConfiguration.SpeedPixProperties.class)
public class SpeedPixConfiguration {

    @Bean
    public SpeedPixClient speedPixClient(SpeedPixProperties properties) {
        return new SpeedPixClient(
            properties.getEndpoint(),
            properties.getAppKey(),
            properties.getAppSecret()
        );
    }

    @ConfigurationProperties(prefix = "speedpix")
    @Validated
    public static class SpeedPixProperties {

        private String endpoint = "https://openai.edu-aliyun.com";

        @NotBlank(message = "SpeedPix App Key 不能为空")
        private String appKey;

        @NotBlank(message = "SpeedPix App Secret 不能为空")
        private String appSecret;

        private int timeoutSeconds = 60;

        // Getters and Setters
        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }
    }
}
```

### 3. 服务层 (Spring Boot 2.x)

**ImageGenerationService.java**
```java
package com.example.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.ImageOutput;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.exception.SpeedPixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenerationService.class);

    private final SpeedPixClient speedPixClient;

    @Autowired
    public ImageGenerationService(SpeedPixClient speedPixClient) {
        this.speedPixClient = speedPixClient;
    }

    // 结果数据结构
    public static class GenerationResult {
        private ImageOutput images;

        public ImageOutput getImages() {
            return images;
        }

        public void setImages(ImageOutput images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "GenerationResult{images=" + images + '}';
        }
    }

    /**
     * 文本生成图像
     */
    public GenerationResult generateImageFromText(String prompt, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始文本生成图像，prompt: {}, workflowId: {}", prompt, workflowId);

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", prompt);

        ComfyPromptRequest request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .randomiseSeeds(true)
                .build();

        Prediction<GenerationResult> result = speedPixClient.run(request, GenerationResult.class);

        logger.info("文本生成图像完成，预测ID: {}", result.getId());
        return result.getOutput();
    }

    /**
     * 图像风格转换
     */
    public GenerationResult styleTransfer(String imagePath, String styleImagePath, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始图像风格转换，workflowId: {}", workflowId);

        Map<String, Object> input = new HashMap<>();
        input.put("image", imagePath);
        input.put("style_image", styleImagePath);
        input.put("strength", 0.8);

        ComfyPromptRequest request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .build();

        Prediction<GenerationResult> result = speedPixClient.run(request, GenerationResult.class);

        logger.info("图像风格转换完成，预测ID: {}", result.getId());
        return result.getOutput();
    }
}
```

### 4. 控制器层 (Spring Boot 2.x)

**ImageController.java**
```java
package com.example.controller;

import com.example.service.ImageGenerationService;
import com.aliyun.speedpix.exception.SpeedPixException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageGenerationService imageGenerationService;

    @Autowired
    public ImageController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    /**
     * 文本生成图像接口
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateImage(
            @RequestParam String prompt,
            @RequestParam String workflowId,
            HttpServletResponse response) {
        try {
            ImageGenerationService.GenerationResult result =
                imageGenerationService.generateImageFromText(prompt, workflowId);

            // 设置响应头
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=generated_image.png");

            // 将图像数据写入响应
            try (InputStream inputStream = result.getImages().getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }

            return ResponseEntity.ok().build();

        } catch (SpeedPixException e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("系统错误: " + e.getMessage());
        }
    }

    /**
     * 图像风格转换接口
     */
    @PostMapping("/style-transfer")
    public ResponseEntity<?> styleTransfer(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("styleImage") MultipartFile styleImageFile,
            @RequestParam String workflowId,
            HttpServletResponse response) {
        try {
            // 保存上传的文件到临时位置
            String imagePath = saveUploadedFile(imageFile);
            String styleImagePath = saveUploadedFile(styleImageFile);

            ImageGenerationService.GenerationResult result =
                imageGenerationService.styleTransfer(imagePath, styleImagePath, workflowId);

            // 设置响应头
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=style_transfer_result.png");

            // 将图像数据写入响应
            try (InputStream inputStream = result.getImages().getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("处理失败: " + e.getMessage());
        }
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = "/tmp/" + fileName;
        file.transferTo(new java.io.File(filePath));
        return filePath;
    }
}
```

## Spring Boot 3.x 集成案例

### 1. 项目依赖配置 (Spring Boot 3.x)

**pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>speedpix-spring-boot3-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpeedPix Java SDK -->
        <dependency>
            <groupId>com.aliyun.speedpix</groupId>
            <artifactId>speedpix-java</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. 配置类 (Spring Boot 3.x)

**SpeedPixConfiguration.java**
```java
package com.example.config;

import com.aliyun.speedpix.SpeedPixClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;  // 注意：Spring Boot 3.x 使用 jakarta

@Configuration
@EnableConfigurationProperties(SpeedPixConfiguration.SpeedPixProperties.class)
public class SpeedPixConfiguration {

    @Bean
    public SpeedPixClient speedPixClient(SpeedPixProperties properties) {
        return SpeedPixClient.builder()
                .endpoint(properties.getEndpoint())
                .appKey(properties.getAppKey())
                .appSecret(properties.getAppSecret())
                .timeoutSeconds(properties.getTimeoutSeconds())
                .build();
    }

    @ConfigurationProperties(prefix = "speedpix")
    @Validated
    public static class SpeedPixProperties {

        private String endpoint = "https://openai.edu-aliyun.com";

        @NotBlank(message = "SpeedPix App Key 不能为空")
        private String appKey;

        @NotBlank(message = "SpeedPix App Secret 不能为空")
        private String appSecret;

        private int timeoutSeconds = 60;

        // Getters and Setters 保持不变
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getAppSecret() { return appSecret; }
        public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    }
}
```

### 3. 服务层 (Spring Boot 3.x)

**ImageGenerationService.java**
```java
package com.example.service;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.ImageOutput;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.exception.SpeedPixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ImageGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ImageGenerationService.class);

    private final SpeedPixClient speedPixClient;

    public ImageGenerationService(SpeedPixClient speedPixClient) {
        this.speedPixClient = speedPixClient;
    }

    // 使用 Java 17 的 record 特性
    public record GenerationResult(ImageOutput images) {
        @Override
        public String toString() {
            return "GenerationResult{images=" + images + '}';
        }
    }

    /**
     * 异步文本生成图像 - 利用 Spring Boot 3.x 的现代特性
     */
    public CompletableFuture<GenerationResult> generateImageFromTextAsync(String prompt, String workflowId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("开始异步文本生成图像，prompt: {}, workflowId: {}", prompt, workflowId);

                var input = Map.of("prompt", prompt);  // Java 17+ 的 var 和 Map.of

                var request = ComfyPromptRequest.builder()
                        .workflowId(workflowId)
                        .aliasId("main")
                        .inputs(input)
                        .randomiseSeeds(true)
                        .build();

                var result = speedPixClient.run(request, GenerationResult.class);

                logger.info("异步文本生成图像完成，预测ID: {}", result.getId());
                return result.getOutput();

            } catch (Exception e) {
                logger.error("异步图像生成失败", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 同步文本生成图像
     */
    public GenerationResult generateImageFromText(String prompt, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始文本生成图像，prompt: {}, workflowId: {}", prompt, workflowId);

        var input = Map.of("prompt", prompt);

        var request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .randomiseSeeds(true)
                .build();

        var result = speedPixClient.run(request, GenerationResult.class);

        logger.info("文本生成图像完成，预测ID: {}", result.getId());
        return result.getOutput();
    }

    /**
     * 批量图像处理
     */
    public CompletableFuture<List<GenerationResult>> batchProcess(List<String> prompts, String workflowId) {
        List<CompletableFuture<GenerationResult>> futures = prompts.stream()
                .map(prompt -> generateImageFromTextAsync(prompt, workflowId))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    /**
     * 图像风格转换 (Spring Boot 3.x)
     */
    public GenerationResult styleTransfer(String imagePath, String styleImagePath, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始图像风格转换，workflowId: {}", workflowId);

        var input = Map.of(
                "image", imagePath,
                "style_image", styleImagePath,
                "strength", 0.8
        );

        var request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .build();

        var result = speedPixClient.run(request, GenerationResult.class);

        logger.info("图像风格转换完成，预测ID: {}", result.getId());
        return result.getOutput();
    }

    /**
     * 异步图像风格转换
     */
    public CompletableFuture<GenerationResult> styleTransferAsync(String imagePath, String styleImagePath, String workflowId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return styleTransfer(imagePath, styleImagePath, workflowId);
            } catch (Exception e) {
                logger.error("异步风格转换失败", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 图像超分辨率 (Spring Boot 3.x)
     */
    public GenerationResult upscaleImage(String imagePath, int scaleFactor, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始图像超分辨率，scaleFactor: {}, workflowId: {}", scaleFactor, workflowId);

        var input = Map.of(
                "image", imagePath,
                "scale_factor", scaleFactor
        );

        var request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .build();

        var result = speedPixClient.run(request, GenerationResult.class);

        logger.info("图像超分辨率完成，预测ID: {}", result.getId());
        return result.getOutput();
    }

    /**
     * 异步图像超分辨率
     */
    public CompletableFuture<GenerationResult> upscaleImageAsync(String imagePath, int scaleFactor, String workflowId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return upscaleImage(imagePath, scaleFactor, workflowId);
            } catch (Exception e) {
                logger.error("异步图像超分辨率失败", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 背景移除 (Spring Boot 3.x)
     */
    public GenerationResult removeBackground(String imagePath, String workflowId)
            throws SpeedPixException, InterruptedException {
        logger.info("开始背景移除，workflowId: {}", workflowId);

        var input = Map.of("image", imagePath);

        var request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .build();

        var result = speedPixClient.run(request, GenerationResult.class);

        logger.info("背景移除完成，预测ID: {}", result.getId());
        return result.getOutput();
    }

    /**
     * 图像增强 (Spring Boot 3.x)
     */
    public GenerationResult enhanceImage(String imagePath, String workflowId, Map<String, Object> enhanceOptions)
            throws SpeedPixException, InterruptedException {
        logger.info("开始图像增强，workflowId: {}", workflowId);

        var input = new java.util.HashMap<String, Object>();
        input.put("image", imagePath);
        input.putAll(enhanceOptions);

        var request = ComfyPromptRequest.builder()
                .workflowId(workflowId)
                .aliasId("main")
                .inputs(input)
                .build();

        var result = speedPixClient.run(request, GenerationResult.class);

        logger.info("图像增强完成，预测ID: {}", result.getId());
        return result.getOutput();
    }
}
```

### 4. 控制器层 (Spring Boot 3.x)

**ImageController.java**
```java
package com.example.controller;

import com.example.service.ImageGenerationService;
import com.aliyun.speedpix.exception.SpeedPixException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;  // 注意：Spring Boot 3.x 使用 jakarta
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageGenerationService imageGenerationService;

    public ImageController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    /**
     * 同步文本生成图像接口
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateImage(
            @RequestParam String prompt,
            @RequestParam String workflowId,
            HttpServletResponse response) {
        try {
            var result = imageGenerationService.generateImageFromText(prompt, workflowId);

            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=generated_image.png");

            try (InputStream inputStream = result.images().getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }

            return ResponseEntity.ok().build();

        } catch (SpeedPixException e) {
            return ResponseEntity.badRequest().body("生成失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("系统错误: " + e.getMessage());
        }
    }

    /**
     * 异步文本生成图像接口 - Spring Boot 3.x 特性
     */
    @PostMapping("/generate-async")
    public CompletableFuture<ResponseEntity<?>> generateImageAsync(
            @RequestParam String prompt,
            @RequestParam String workflowId) {

        return imageGenerationService.generateImageFromTextAsync(prompt, workflowId)
                .thenApply(result -> ResponseEntity.ok()
                        .header("Content-Type", "application/json")
                        .body(Map.of(
                                "status", "success",
                                "message", "图像生成完成"
                        )))
                .exceptionally(throwable -> ResponseEntity.internalServerError()
                        .body(Map.of(
                                "status", "error",
                                "message", "生成失败: " + throwable.getMessage()
                        )));
    }

    /**
     * 批量处理接口
     */
    @PostMapping("/batch-generate")
    public CompletableFuture<ResponseEntity<?>> batchGenerate(
            @RequestBody List<String> prompts,
            @RequestParam String workflowId) {

        return imageGenerationService.batchProcess(prompts, workflowId)
                .thenApply(results -> ResponseEntity.ok()
                        .body(Map.of(
                                "status", "success",
                                "count", results.size(),
                                "message", "批量生成完成"
                        )))
                .exceptionally(throwable -> ResponseEntity.internalServerError()
                        .body(Map.of(
                                "status", "error",
                                "message", "批量生成失败: " + throwable.getMessage()
                        )));
    }

    /**
     * 图像风格转换接口 (Spring Boot 3.x)
     */
    @PostMapping("/style-transfer")
    public ResponseEntity<?> styleTransfer(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("styleImage") MultipartFile styleImageFile,
            @RequestParam String workflowId,
            HttpServletResponse response) {
        try {
            // 保存上传的文件到临时位置
            var imagePath = saveUploadedFile(imageFile);
            var styleImagePath = saveUploadedFile(styleImageFile);

            var result = imageGenerationService.styleTransfer(imagePath, styleImagePath, workflowId);

            // 设置响应头
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=style_transfer_result.png");

            // 将图像数据写入响应
            try (InputStream inputStream = result.images().getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("处理失败: " + e.getMessage());
        }
    }

    /**
     * 异步图像风格转换接口 (Spring Boot 3.x)
     */
    @PostMapping("/style-transfer-async")
    public CompletableFuture<ResponseEntity<?>> styleTransferAsync(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("styleImage") MultipartFile styleImageFile,
            @RequestParam String workflowId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                var imagePath = saveUploadedFile(imageFile);
                var styleImagePath = saveUploadedFile(styleImageFile);
                return imageGenerationService.styleTransfer(imagePath, styleImagePath, workflowId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenApply(result -> ResponseEntity.ok()
                .body(Map.of(
                        "status", "success",
                        "message", "风格转换完成",
                        "imageData", result.images().getData()
                )))
                .exceptionally(throwable -> ResponseEntity.internalServerError()
                        .body(Map.of(
                                "status", "error",
                                "message", "风格转换失败: " + throwable.getMessage()
                        )));
    }

    /**
     * 图像超分辨率接口 (Spring Boot 3.x)
     */
    @PostMapping("/upscale")
    public ResponseEntity<?> upscaleImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(defaultValue = "4") int scaleFactor,
            @RequestParam String workflowId,
            HttpServletResponse response) {
        try {
            var imagePath = saveUploadedFile(imageFile);
            var result = imageGenerationService.upscaleImage(imagePath, scaleFactor, workflowId);

            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline; filename=upscaled_image.png");

            try (InputStream inputStream = result.images().getInputStream()) {
                inputStream.transferTo(response.getOutputStream());
                response.getOutputStream().flush();
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("超分辨率处理失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "SpeedPix Image Generation",
                "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * 获取支持的工作流列表
     */
    @GetMapping("/workflows")
    public ResponseEntity<Map<String, Object>> getSupportedWorkflows() {
        var workflows = List.of(
                Map.of("id", "text-to-image", "name", "文本生成图像", "description", "根据文本描述生成图像"),
                Map.of("id", "style-transfer", "name", "风格转换", "description", "将一张图像的风格应用到另一张图像"),
                Map.of("id", "upscale", "name", "图像超分辨率", "description", "提高图像分辨率和质量"),
                Map.of("id", "background-removal", "name", "背景移除", "description", "自动移除图像背景"),
                Map.of("id", "image-enhancement", "name", "图像增强", "description", "增强图像质量和细节")
        );

        return ResponseEntity.ok(Map.of(
                "workflows", workflows,
                "total", workflows.size()
        ));
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 创建临时文件
        var fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        var uploadDir = java.nio.file.Path.of("/tmp/speedpix-uploads");
        java.nio.file.Files.createDirectories(uploadDir);

        var filePath = uploadDir.resolve(fileName);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }
}
```

### 5. 全局异常处理器 (Spring Boot 3.x)

**GlobalExceptionHandler.java**
```java
package com.example.exception;

import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 SpeedPix API 异常
     */
    @ExceptionHandler(SpeedPixException.class)
    public ResponseEntity<Map<String, Object>> handleSpeedPixException(
            SpeedPixException e, HttpServletRequest request) {
        logger.error("SpeedPix API 调用失败: {}", e.getMessage(), e);

        var errorResponse = Map.of(
                "error", "SPEEDPIX_API_ERROR",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI(),
                "errorCode", e.getErrorCode() != null ? e.getErrorCode() : "UNKNOWN",
                "apiInvokeId", e.getApiInvokeId() != null ? e.getApiInvokeId() : "N/A"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理模型预测异常
     */
    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<Map<String, Object>> handlePredictionException(
            PredictionException e, HttpServletRequest request) {
        logger.error("模型预测失败: {}", e.getMessage(), e);

        var errorResponse = Map.<String, Object>of(
                "error", "PREDICTION_ERROR",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI(),
                "predictionId", e.getPrediction() != null ? e.getPrediction().getId() : "N/A",
                "predictionStatus", e.getPrediction() != null ? e.getPrediction().getStatus() : "UNKNOWN"
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException e, HttpServletRequest request) {
        logger.warn("文件上传大小超限: {}", e.getMessage());

        var errorResponse = Map.of(
                "error", "FILE_SIZE_EXCEEDED",
                "message", "上传的文件大小超过限制",
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI(),
                "maxSize", "10MB"
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("非法参数: {}", e.getMessage());

        var errorResponse = Map.of(
                "error", "INVALID_PARAMETER",
                "message", e.getMessage(),
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        logger.error("未知错误: {}", e.getMessage(), e);

        var errorResponse = Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", "服务器内部错误，请稍后重试",
                "timestamp", Instant.now().toString(),
                "path", request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
```

### 6. 异步配置 (Spring Boot 3.x)

**AsyncConfiguration.java**
```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "imageProcessingExecutor")
    public Executor imageProcessingExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ImageProcessing-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
```

## 配置文件

### application.yml (适用于两个版本)

```yaml
# SpeedPix 配置
speedpix:
  endpoint: https://openai.edu-aliyun.com
  app-key: ${SPEEDPIX_APP_KEY:your-app-key}
  app-secret: ${SPEEDPIX_APP_SECRET:your-app-secret}
  timeout-seconds: 120

# Spring Boot 配置
spring:
  application:
    name: speedpix-demo
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# 日志配置
logging:
  level:
    com.example: DEBUG
    com.aliyun.speedpix: DEBUG

# 服务器配置
server:
  port: 8080
```

### application-spring-boot-3.yml (Spring Boot 3.x 专用配置)

```yaml
# Spring Boot 3.x 专用配置
spring:
  application:
    name: speedpix-demo
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  task:
    execution:
      pool:
        core-size: 4
        max-size: 8
        queue-capacity: 100
        keep-alive: 60s
      thread-name-prefix: "SpeedPix-"

# 管理端点配置 (Spring Boot 3.x)
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# SpeedPix 配置
speedpix:
  endpoint: https://openai.edu-aliyun.com
  app-key: ${SPEEDPIX_APP_KEY:your-app-key}
  app-secret: ${SPEEDPIX_APP_SECRET:your-app-secret}
  timeout-seconds: 120

# 日志配置 (Spring Boot 3.x 改进)
logging:
  level:
    com.example: DEBUG
    com.aliyun.speedpix: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}"

# 服务器配置
server:
  port: 8080
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain,image/png,image/jpeg
```

## 启动类

### Application.java (适用于两个版本)

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // Spring Boot 3.x 中启用异步功能
public class SpeedPixDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeedPixDemoApplication.class, args);
    }
}
```

## 使用示例

### 启动应用

```bash
# 设置环境变量
export SPEEDPIX_APP_KEY="your-app-key"
export SPEEDPIX_APP_SECRET="your-app-secret"

# Spring Boot 2.x (Java 11)
mvn spring-boot:run

# Spring Boot 3.x (Java 17)
mvn spring-boot:run
```

### API 调用示例

```bash
# 文本生成图像
curl -X POST "http://localhost:8080/api/images/generate" \
  -d "prompt=一只可爱的小猫坐在花园里" \
  -d "workflowId=your-workflow-id" \
  --output generated_image.png

# 异步生成 (仅 Spring Boot 3.x)
curl -X POST "http://localhost:8080/api/images/generate-async" \
  -d "prompt=美丽的日落风景" \
  -d "workflowId=your-workflow-id"

# 批量生成 (仅 Spring Boot 3.x)
curl -X POST "http://localhost:8080/api/images/batch-generate?workflowId=your-workflow-id" \
  -H "Content-Type: application/json" \
  -d '["山水画", "城市夜景", "森林小径"]'

# 图像风格转换 (Spring Boot 3.x)
curl -X POST "http://localhost:8080/api/images/style-transfer" \
  -F "image=@/path/to/content.jpg" \
  -F "styleImage=@/path/to/style.jpg" \
  -F "workflowId=your-workflow-id" \
  --output style_transfer_result.png

# 异步风格转换 (仅 Spring Boot 3.x)
curl -X POST "http://localhost:8080/api/images/style-transfer-async" \
  -F "image=@/path/to/content.jpg" \
  -F "styleImage=@/path/to/style.jpg" \
  -F "workflowId=your-workflow-id"

# 图像超分辨率 (Spring Boot 3.x)
curl -X POST "http://localhost:8080/api/images/upscale" \
  -F "image=@/path/to/low_res.jpg" \
  -F "scaleFactor=4" \
  -F "workflowId=your-workflow-id" \
  --output upscaled_image.png

# 健康检查
curl -X GET "http://localhost:8080/api/images/health"

# 获取支持的工作流
curl -X GET "http://localhost:8080/api/images/workflows"
```

## 主要差异总结

| 特性 | Spring Boot 2.x | Spring Boot 3.x |
|-----|----------------|----------------|
| **Java 版本** | Java 8-17 | Java 17+ |
| **Jakarta EE** | `javax.*` | `jakarta.*` |
| **依赖注入** | `@Autowired` 构造函数 | 推荐构造函数注入 |
| **现代 Java** | 有限支持 | 全面支持 (records, var, pattern matching) |
| **异步处理** | `@Async` | 增强的 `@Async` + CompletableFuture |
| **配置验证** | `@Valid` | `@Validated` |
| **性能** | 标准性能 | 改进的启动时间和内存使用 |

## 迁移指南

如果您需要从 Spring Boot 2.x 迁移到 3.x：

1. **升级 Java 版本**到 17 或更高
2. **更新依赖包名**：`javax.*` → `jakarta.*`
3. **检查第三方依赖**兼容性
4. **利用新特性**：records, pattern matching, 改进的异步处理
5. **测试应用**确保功能正常

## 最佳实践

1. **配置外部化**：使用 `application.yml` 和环境变量
2. **异步处理**：对于耗时的图像生成任务使用异步处理
3. **错误处理**：实现全局异常处理器
4. **日志记录**：详细记录 API 调用和错误信息
5. **文件管理**：合理处理上传文件的临时存储和清理
6. **安全考虑**：保护 API Key 和 Secret，实现访问控制

这样，您就可以根据项目需求选择合适的 Spring Boot 版本，并享受 SpeedPix Java SDK 带来的强大图像生成功能。
