# SpeedPix Java SDK

智作工坊 SpeedPix Java SDK，```xml
<dependency>
    <groupId>com.aliyun.speedpix</groupId>
    <artifactId>speedpix-java</artifactId>
    <version>1.0.0</version>
</dependency>
``` API 接口，专注于 AI 图像生成和处理工作流。

## 📚 关于智作工坊

智作工坊（AIGC Service Lab）是阿里云教育推出的 AIGC 生成服务，主要为泛教育、设计业务企业提供高效的 AIGC（人工智能生成内容）PAAS 服务。

### 🎯 核心功能
- **文生图**：根据文本描述生成高质量图像
- **图生图**：基于输入图像进行风格转换或内容变换
- **文转视频**：将文本描述转换为动态视频内容
- **图转视频**：将静态图像转换为动态视频

### 🔧 技术支持
- 支持**通义万相**以及开源的 **Stable Diffusion** 模型
- 提供 **WEB UI** 和 **ComfyUI** 两种模式
- 集成阿里云严格的**内容安全检测服务**
- 支持自定义界面部署和权限管理

### 📖 详细文档
- [智作工坊产品文档](https://help.aliyun.com/document_detail/2804197.html)
- [产品概述](https://help.aliyun.com/document_detail/2804199.html)
- [智作工坊控制台](https://eduplatform-sp.console.aliyun.com/)

---

## 特性

- 🚀 **简洁易用** - 直观的 API 设计，开箱即用
- 🔧 **代码规范** - 遵循 Java 最佳编码实践
- 🎯 **一键运行** - `run()` 方法直接获取结果
- 📎 **多文件格式** - 支持路径、File 对象、InputStream 等多种输入
- 🔐 **完整认证** - 阿里云 API 网关 HMAC-SHA256 认证
- 🛡️ **类型安全** - 完整的 Java 类型系统支持
- ⚡ **高性能** - 基于 OkHttp 的现代 HTTP 客户端
- 📁 **文件上传** - 支持多种文件格式的上传功能
- 🧪 **全面测试** - 包含单元测试和集成测试
- 💾 **图像保存** - 直接将生成的图像保存到本地文件

## 安装

### Maven

```xml
<dependency>
    <groupId>com.aliyun.speedpix</groupId>
    <artifactId>speedpix-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.aliyun.speedpix:speedpix-java:1.0.0'
```

## 快速开始

### 设置环境变量

```bash
export SPEEDPIX_ENDPOINT="your-endpoint.com"  # 可选，默认为 https://openai.edu-aliyun.com
export SPEEDPIX_APP_KEY="your-app-key"        # 必需
export SPEEDPIX_APP_SECRET="your-app-secret"  # 必需
```

### 基础使用

SpeedPix Java SDK 提供多种方式创建客户端，选择最适合您的方式：

#### 方法 1：最简方式（推荐）

```java
import com.aliyun.speedpix.SpeedPixClient;

// 仅需提供必需的认证信息，使用默认endpoint
SpeedPixClient client = new SpeedPixClient("your-app-key", "your-app-secret");
```

#### 方法 2：从环境变量创建

```java
// 从环境变量自动读取配置（需要设置环境变量）
SpeedPixClient client = new SpeedPixClient();
```

#### 方法 3：自定义endpoint

```java
// 使用自定义endpoint
SpeedPixClient client = new SpeedPixClient(
    "https://your-custom-endpoint.com",
    "your-app-key",
    "your-app-secret"
);
```

#### 方法 4：Builder模式（高级用法）

```java
// 使用Builder模式进行灵活配置
SpeedPixClient client = SpeedPixClient.builder()
    .appKey("your-app-key")
    .appSecret("your-app-secret")
    .endpoint("https://custom-endpoint.com")  // 可选
    .userAgent("my-app/1.0.0")               // 可选
    .timeoutSeconds(60)                      // 可选
    .build();

// 或者从环境变量构建
SpeedPixClient client = SpeedPixClient.builder()
    .fromEnv()  // 自动从环境变量读取
    .timeoutSeconds(120)  // 覆盖特定配置
    .build();
```

### 完整示例

#### 直接运行（推荐新手）

```java
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.model.ImageOutput;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class BasicUsageExample {

    // 定义结果数据结构
    public static class ResultDTO {
        private ImageOutput images;

        public ImageOutput getImages() {
            return images;
        }

        public void setImages(ImageOutput images) {
            this.images = images;
        }

        @Override
        public String toString() {
            return "ResultDTO{images=" + images + '}';
        }
    }

    public static void main(String[] args) throws Exception {
        // 创建客户端（推荐：使用最简方式）
        SpeedPixClient client = new SpeedPixClient("your-app-key", "your-app-secret");

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/path/to/your/input/image.png");

        // 直接运行并获取结果
        Prediction<ResultDTO> result = client.run(ComfyPromptRequest.builder()
            .workflowId("your_workflow_id")
            .aliasId("main")
            .inputs(input)
            .build(), ResultDTO.class);

        System.out.println("输出结果: " + result);

        // 保存生成的图像到本地
        result.getOutput().getImages().save("result.png");

        // 或者获取输入流进行其他处理
        // InputStream inputStream = result.getOutput().getImages().getInputStream();
    }
}
```

#### 方法 2：使用全局静态方法

```java
import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import java.util.HashMap;
import java.util.Map;

public class GlobalFunctionExample {
    public static void main(String[] args) throws Exception {
        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/path/to/your/input/image.png");

        // 使用全局 run 函数
        Prediction<ResultDTO> output = SpeedPix.run(ComfyPromptRequest.builder()
            .workflowId("your_workflow_id")
            .aliasId("main")
            .inputs(input)
            .build(), ResultDTO.class);

        System.out.println("输出结果: " + output);
    }
}
```

#### 方法 3：传统预测接口（完全控制）

```java
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.exception.PredictionException;
import java.util.HashMap;
import java.util.Map;

public class TraditionalExample {
    public static void main(String[] args) throws Exception {
        SpeedPixClient client = new SpeedPixClient(null, null, null);

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/path/to/your/input/image.png");

        try {
            // 创建预测任务
            Prediction<ResultDTO> prediction = client.predictions().create(ComfyPromptRequest.builder()
                .workflowId("your_workflow_id")
                .aliasId("main")
                .inputs(input)
                .build(), ResultDTO.class);
            System.out.println("创建预测任务: " + prediction.getId());

            // 等待完成
            prediction = prediction.waitForCompletion();
            System.out.println("最终状态: " + prediction.getStatus());

            if (prediction.getOutput() != null) {
                System.out.println("输出结果: " + prediction.getOutput());
            }

        } catch (PredictionException e) {
            System.err.println("模型执行失败: " + e.getMessage());
            if (e.getPrediction() != null) {
                System.err.println("预测 ID: " + e.getPrediction().getId());
                System.err.println("错误详情: " + e.getPrediction().getError());
            }
        }
    }
}
```


#### 方法4: Spring Boot 集成

SpeedPix Java SDK 与 Spring Boot 无缝集成，支持 Spring Boot 2.x 和 3.x 版本。

**快速集成**

```xml
<!-- 添加依赖 -->
<dependency>
    <groupId>com.aliyun.speedpix</groupId>
    <artifactId>speedpix-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
@Service
public class ImageService {

    @Autowired
    private SpeedPixProperties speedPixProperties;

    public String generateImage(String prompt) {
        SpeedPix client = new SpeedPix(
            speedPixProperties.getApiKey(),
            speedPixProperties.getApiSecret(),
            speedPixProperties.getBaseUrl()
        );

        return client.txt2img(prompt).run();
    }
}
```

📖 **完整集成指南**: [Spring Boot 集成文档](doc/SPRING_BOOT_INTEGRATION.md)
- Spring Boot 2.x 和 3.x 完整案例
- 配置类、服务层、控制器层示例
- 全局异常处理和最佳实践
- 版本迁移指南

## 详细使用方法

### 自定义结果数据结构

您可以定义自己的数据结构来接收 API 返回的结果：

```java
import com.aliyun.speedpix.model.ImageOutput;

public class ResultDTO {
    private ImageOutput images;

    public ImageOutput getImages() {
        return images;
    }

    public void setImages(ImageOutput images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ResultDTO{images=" + images + '}';
    }
}
```

### 方法 1：直接运行示例（推荐新手）

```java
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import java.io.IOException;

public class DirectRunExample {
    public static void main(String[] args) throws Exception {
        // 创建客户端（自动从环境变量读取配置）
        SpeedPixClient client = new SpeedPixClient();

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/Users/libin/Downloads/p850622.png");

        // 直接运行并获取结果
        Prediction<ResultDTO> result = client.run(ComfyPromptRequest.builder()
            .workflowId("your_workflow_id")
            .aliasId("main")
            .inputs(input)
            .build(), ResultDTO.class);

        System.out.println("输出结果: " + result);

        // 保存生成的图像
        result.getOutput().getImages().save("result.png");

        // 或者使用输入流进行其他处理
        // InputStream stream = result.getOutput().getImages().getInputStream();
    }
}
```

### 方法 2：全局函数示例

```java
import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;

public class GlobalFunctionExample {
    public static void main(String[] args) throws Exception {
        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/Users/libin/Downloads/p850622.png");

        // 使用全局 run 函数
        Prediction<ResultDTO> output = SpeedPix.run(ComfyPromptRequest.builder()
            .workflowId("01jvp41b358md06w46fz1yz78a")
            .aliasId("main")
            .inputs(input)
            .build(), ResultDTO.class);

        System.out.println("输出结果: " + output);
    }
}
```

### 方法 3：传统预测接口示例

```java
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;

public class TraditionalPredictionExample {
    public static void main(String[] args) throws Exception {
        SpeedPixClient client = new SpeedPixClient(null, null, null);

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/Users/libin/Downloads/p850622.png");

        try {
            // 创建预测任务
            Prediction<ResultDTO> prediction = client.predictions().create(ComfyPromptRequest.builder()
                .workflowId("01jvp41b358md06w46fz1yz78a")
                .aliasId("main")
                .inputs(input)
                .build(), ResultDTO.class);
            System.out.println("创建预测任务: " + prediction.getId());

            // 等待完成
            prediction = prediction.waitForCompletion();
            System.out.println("最终状态: " + prediction.getStatus());

            if (prediction.getOutput() != null) {
                System.out.println("输出结果: " + prediction.getOutput());
            }

        } catch (PredictionException e) {
            System.err.println("模型执行失败: " + e.getMessage());
            if (e.getPrediction() != null) {
                System.err.println("预测 ID: " + e.getPrediction().getId());
                System.err.println("错误详情: " + e.getPrediction().getError());
            }
        } catch (SpeedPixException e) {
            System.err.println("其他错误: " + e.getMessage());
        }
    }
}
```

## 🚀 资源配置

### 共享算力 vs 独享资源

智作工坊支持两种资源类型：

- **共享算力**：默认使用，成本较低，适合一般业务场景
- **独享资源**：推荐对延迟和成功率敏感的业务使用，提供更稳定的性能保障

### 配置方式

默认情况下，如果不指定资源配置，系统会使用共享算力资源。如果您对延迟和成功率有较高要求，推荐配置独享资源。

```java
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;
import java.util.HashMap;
import java.util.Map;

// 使用共享算力（默认）
Map<String, Object> input = new HashMap<>();
input.put("prompt", "一个美丽的风景");

Prediction<ResultDTO> result = client.run(ComfyPromptRequest.builder()
    .workflowId("your-workflow-id")
    .aliasId("main")
    .inputs(input)
    .build(), ResultDTO.class);
    // 不指定 resourceConfigId 时自动使用共享算力

// 使用独享资源
Prediction<ResultDTO> result = client.run(ComfyPromptRequest.builder()
    .workflowId("your-workflow-id")
    .aliasId("main")
    .inputs(input)
    .resourceConfigId("your-dedicated-resource-id")  // 指定独享资源ID
    .build(), ResultDTO.class);

// 使用静态方法指定独享资源
Prediction<ResultDTO> result = SpeedPix.run(
    ComfyPromptRequest.builder()
        .workflowId("your-workflow-id")
        .inputs(input)
        .build(),
    "your-dedicated-resource-id",  // 独享资源ID参数
    ResultDTO.class
);
```

### 相关文档

- [独享资源管理](https://help.aliyun.com/document_detail/2834512.html)
- [资源配置参数说明](https://help.aliyun.com/document_detail/2844596.html)

---

## 文件处理

SpeedPix SDK 提供完整的文件处理功能，支持文件上传和多种输入格式：

### 文件上传

```java
import com.aliyun.speedpix.model.FileObject;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

SpeedPixClient client = new SpeedPixClient("app-key", "app-secret", "endpoint");

// 方法 1: 使用 File 对象
File imageFile = new File("/path/to/image.jpg");
FileObject uploadedFile = client.files().create(imageFile);
System.out.println("文件上传成功: " + uploadedFile.getUrl());

// 方法 2: 使用 Path 对象
FileObject uploadedFile2 = client.files().create(Paths.get("/path/to/image.png"));

// 方法 3: 使用 InputStream
try (FileInputStream fis = new FileInputStream("/path/to/image.gif")) {
    FileObject uploadedFile3 = client.files().create(fis, "image.gif");
    System.out.println("上传完成: " + uploadedFile3.getUrl());
}
```

### 在工作流中使用上传的文件

```java
// 上传文件
FileObject inputImage = client.files().create(new File("/path/to/input.jpg"));

// 在工作流中使用
Map<String, Object> input = new HashMap<>();
input.put("image", inputImage.getUrl());
input.put("prompt", "Remove background from this image");

Object result = client.run("background-removal", input);
```

### 支持的文件格式

SDK 自动检测文件类型，支持以下格式：
- **图像**: JPG, JPEG, PNG, GIF, WebP
- **视频**: MP4
- **文档**: PDF
- **其他**: 通用二进制文件

## 错误处理

SpeedPix SDK 提供详细的错误处理机制，根据代码案例的最佳实践：

```java
import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        SpeedPixClient client = new SpeedPixClient(null, null, null);

        Map<String, Object> input = new HashMap<>();
        input.put("image", "/Users/libin/Downloads/p850622.png");

        try {
            // 创建预测任务
            Prediction<ResultDTO> prediction = client.predictions().create(ComfyPromptRequest.builder()
                .workflowId("01jvp41b358md06w46fz1yz78a")
                .aliasId("main")
                .inputs(input)
                .build(), ResultDTO.class);
            System.out.println("创建预测任务: " + prediction.getId());

            // 等待完成
            prediction = prediction.waitForCompletion();
            System.out.println("最终状态: " + prediction.getStatus());

            if (prediction.getOutput() != null) {
                System.out.println("输出结果: " + prediction.getOutput());
                // 保存结果
                prediction.getOutput().getImages().save("result.png");
            }

        } catch (PredictionException e) {
            // 模型执行失败
            System.err.println("模型执行失败: " + e.getMessage());
            if (e.getPrediction() != null) {
                System.err.println("预测 ID: " + e.getPrediction().getId());
                System.err.println("错误详情: " + e.getPrediction().getError());
                System.err.println("预测状态: " + e.getPrediction().getStatus());
            }
        } catch (SpeedPixException e) {
            // API 调用失败
            System.err.println("其他错误: " + e.getMessage());
            if (e.getErrorCode() != null) {
                System.err.println("错误代码: " + e.getErrorCode());
            }
            if (e.getApiInvokeId() != null) {
                System.err.println("API 调用 ID: " + e.getApiInvokeId());
            }
        } catch (InterruptedException e) {
            // 等待被中断
            System.err.println("操作被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            // 文件保存失败
            System.err.println("文件操作失败: " + e.getMessage());
        } catch (Exception e) {
            // 其他未知错误
            System.err.println("未知错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### 简化的错误处理（用于直接运行方法）

```java
public class SimpleErrorHandling {
    public static void main(String[] args) {
        try {
            // 方法 1：直接运行示例
            directRunExample();

            // 方法 2：全局函数示例
            globalFunctionExample();

            // 方法 3：传统预测接口
            traditionalPredictionExample();

        } catch (Exception e) {
            System.err.println("执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void directRunExample() throws Exception {
        SpeedPixClient client = new SpeedPixClient();
        // ... 业务逻辑
    }

    // ... 其他方法
}
```

## API 参考

### SpeedPixClient

主要客户端类，提供所有 API 访问功能。

#### 构造函数

```java
// 自动从环境变量读取配置（推荐）
SpeedPixClient client = new SpeedPixClient();

// 手动指定配置
SpeedPixClient client = new SpeedPixClient(
    "your-endpoint.com",
    "your-app-key",
    "your-app-secret"
);

// 传递 null 使用环境变量（向后兼容）
SpeedPixClient client = new SpeedPixClient(null, null, null);
```

#### 主要方法

```java
// 方法 1：直接运行（推荐）
Prediction<T> run(ComfyPromptRequest request, Class<T> outputType)

// 方法 2：全局函数
SpeedPix.run(ComfyPromptRequest request, Class<T> outputType)

// 方法 3：传统预测接口
client.predictions().create(ComfyPromptRequest request, Class<T> outputType)
```

### ComfyPromptRequest

工作流请求构建器：

```java
ComfyPromptRequest request = ComfyPromptRequest.builder()
    .workflowId("your-workflow-id")    // 必需：工作流ID
    .aliasId("main")                   // 可选：别名ID，默认 "main"
    .inputs(inputMap)                  // 必需：输入参数 Map
    .randomiseSeeds(true)              // 可选：随机化种子
    .returnTempFiles(false)            // 可选：返回临时文件
    .build();
```

### Prediction<T>

预测结果对象：

```java
// 获取预测状态
String status = prediction.getStatus();
String id = prediction.getId();

// 获取输出结果
T output = prediction.getOutput();

// 等待完成（仅在传统方法中需要）
prediction = prediction.waitForCompletion();
```

### ImageOutput

图像输出处理：

```java
ImageOutput images = result.getOutput().getImages();

// 保存到文件
images.save("output.png");

// 获取输入流
InputStream stream = images.getInputStream();

// 获取原始数据
byte[] data = images.getData();
```

### 静态工厂方法

```java
// SpeedPix 全局方法
import com.aliyun.speedpix.SpeedPix;

// 使用默认客户端（需要设置环境变量）
Prediction<ResultDTO> result = SpeedPix.run(request, ResultDTO.class);
```

## 开发

```bash
# 克隆仓库
git clone <repository-url>
cd speedpix-java

# 编译项目
mvn compile

# 运行测试
mvn test

# 打包
mvn package

# 安装到本地仓库
mvn install
```

### 发布到 Maven 中央仓库

如需发布新版本到 Maven 中央仓库，请参考 [PUBLISHING.md](doc/PUBLISHING.md) 获取详细指南。

## 系统要求

- Java 8 或更高版本
- Maven 3.6 或更高版本

## 许可证

MIT License


## 获取帮助

如有任何问题或需要技术支持，请联系智作工坊团队。
