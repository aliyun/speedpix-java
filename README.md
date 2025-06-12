# SpeedPix Java SDK

智作工坊 Java SDK，提供简洁易用的 API 接口，专注于 AI 图像生成和处理工作流。

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
    <groupId>com.aliyun</groupId>
    <artifactId>speedpix-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.aliyun:speedpix-java:1.0.0'
```

## 快速开始

### 设置环境变量

```bash
export SPEEDPIX_ENDPOINT="your-endpoint.com"
export SPEEDPIX_APP_KEY="your-app-key"
export SPEEDPIX_APP_SECRET="your-app-secret"
```

### 基础使用

#### 方法 1：直接运行（推荐新手）

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
        // 创建客户端（自动从环境变量读取配置）
        SpeedPixClient client = new SpeedPixClient();

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

### ComfyPromptRequest Builder 模式详解

```java
ComfyPromptRequest request = ComfyPromptRequest.builder()
    .workflowId("your-workflow-id")         // 必需：工作流ID
    .aliasId("main")                        // 可选：别名ID
    .versionId("v1.0")                      // 可选：版本ID
    .inputs(inputMap)                       // 必需：输入参数
    .randomiseSeeds(true)                   // 可选：是否随机化种子
    .returnTempFiles(false)                 // 可选：是否返回临时文件
    .build();
```
### 图像处理功能

SpeedPix SDK 提供强大的图像处理功能，支持直接保存和流式处理：

```java
import com.aliyun.speedpix.model.ImageOutput;
import java.io.InputStream;
import java.io.FileOutputStream;

// 运行图像处理工作流
Prediction<ResultDTO> result = client.run(request, ResultDTO.class);

// 方法 1：直接保存到文件
result.getOutput().getImages().save("output.png");

// 方法 2：获取输入流进行自定义处理
try (InputStream inputStream = result.getOutput().getImages().getInputStream()) {
    // 保存到自定义位置
    try (FileOutputStream fos = new FileOutputStream("/custom/path/result.png")) {
        inputStream.transferTo(fos);
    }
}

// 方法 3：获取图像数据进行进一步处理
byte[] imageData = result.getOutput().getImages().getData();
// 进行图像分析、压缩等操作...
```

### 高级用法示例

```java
public class AdvancedExample {
    public static void main(String[] args) throws Exception {
        SpeedPixClient client = new SpeedPixClient();

        // 图像风格转换
        Map<String, Object> styleTransferInput = new HashMap<>();
        styleTransferInput.put("image", "/path/to/content/image.jpg");
        styleTransferInput.put("style_image", "/path/to/style/image.jpg");
        styleTransferInput.put("strength", 0.8);

        Prediction<ResultDTO> styleResult = client.run(ComfyPromptRequest.builder()
            .workflowId("style-transfer-workflow")
            .aliasId("main")
            .inputs(styleTransferInput)
            .build(), ResultDTO.class);

        styleResult.getOutput().getImages().save("styled_image.png");

        // 图像超分辨率
        Map<String, Object> upscaleInput = new HashMap<>();
        upscaleInput.put("image", "/path/to/low/res/image.jpg");
        upscaleInput.put("scale_factor", 4);

        Prediction<ResultDTO> upscaleResult = client.run(ComfyPromptRequest.builder()
            .workflowId("upscale-workflow")
            .aliasId("main")
            .inputs(upscaleInput)
            .build(), ResultDTO.class);

        upscaleResult.getOutput().getImages().save("upscaled_image.png");
    }
}
```

## 完整示例

基于 `BasicUsageExample.java` 的完整代码示例：

```java
package com.aliyun.speedpix.examples;

import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.ImageOutput;
import com.aliyun.speedpix.model.Prediction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SpeedPix Java SDK 基础使用示例
 * 演示三种主要的使用方法
 */
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

    public static void main(String[] args) {
        try {
            // 方法 1：直接运行（推荐新手）
            directRunExample();

            // 方法 2：使用全局静态方法
            globalFunctionExample();

            // 方法 3：传统预测接口
            traditionalPredictionExample();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法 1：直接运行示例
     */
    private static void directRunExample() throws SpeedPixException, InterruptedException, IOException {
        System.out.println("=== 方法 1：直接运行示例 ===");

        // 创建客户端（自动从环境变量读取配置）
        SpeedPixClient client = new SpeedPixClient();

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("image", "/Users/libin/Downloads/p850622.png");

        // 直接运行并获取结果
        Prediction<ResultDTO> r = client.run(ComfyPromptRequest.builder()
            .workflowId("01jvp41b358md06w46fz1yz78a")
            .aliasId("main")
            .inputs(input)
            .build(), ResultDTO.class);

        System.out.println("输出结果: " + r);

        // 保存生成的图像
        r.getOutput().getImages().save("result.png");

        // 或者使用输入流: result.getImages().getInputStream()
    }

    /**
     * 方法 2：全局函数示例
     */
    private static void globalFunctionExample() throws SpeedPixException, InterruptedException {
        System.out.println("=== 方法 2：全局函数示例 ===");

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

    /**
     * 方法 3：传统预测接口示例
     */
    private static void traditionalPredictionExample() throws SpeedPixException, InterruptedException {
        System.out.println("=== 方法 3：传统预测接口示例 ===");

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

### 环境变量配置示例

在项目根目录创建 `.env` 文件或设置系统环境变量：

```bash
# .env 文件或环境变量
SPEEDPIX_ENDPOINT=your-endpoint.com
SPEEDPIX_APP_KEY=your-app-key
SPEEDPIX_APP_SECRET=your-app-secret
```

### 快速启动脚本

```bash
#!/bin/bash
# 设置环境变量
export SPEEDPIX_ENDPOINT="your-endpoint.com"
export SPEEDPIX_APP_KEY="your-app-key"
export SPEEDPIX_APP_SECRET="your-app-secret"

# 编译并运行示例
mvn compile exec:java -Dexec.mainClass="com.aliyun.speedpix.examples.BasicUsageExample"
```

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

## 系统要求

- Java 8 或更高版本
- Maven 3.6 或更高版本

## 许可证

MIT License

## 获取帮助

如有任何问题或需要技术支持，请联系智作工坊团队。
