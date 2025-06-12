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

```java
import com.aliyun.speedpix.SpeedPixClient;
import java.util.HashMap;
import java.util.Map;

public class QuickStart {
    public static void main(String[] args) throws Exception {
        // 创建客户端（自动从环境变量读取配置）
        SpeedPixClient client = new SpeedPixClient(null, null, null);

        // 准备输入参数
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", "A beautiful landscape");

        // 运行模型并获取结果
        Object output = client.run("your-workflow-id", input);

        System.out.println("结果: " + output);
    }
}
```

## 详细使用方法

### 方法 1：直接运行（推荐）

```java
import com.aliyun.speedpix.SpeedPixClient;

SpeedPixClient client = new SpeedPixClient(
    "your-endpoint.com",
    "your-app-key",
    "your-app-secret"
);

Map<String, Object> input = new HashMap<>();
input.put("prompt", "A magical forest");

// 直接运行并获取结果
Object output = client.run("your-workflow-id", input);

// 处理不同类型的输出
if (output instanceof List) {
    // 多个输出文件
    List<?> outputs = (List<?>) output;
    for (int i = 0; i < outputs.size(); i++) {
        System.out.println("输出 " + i + ": " + outputs.get(i));
    }
} else {
    // 单个输出
    System.out.println("结果: " + output);
}
```

### 方法 2：全局静态方法

```java
import com.aliyun.speedpix.SpeedPix;

// 使用自定义客户端
SpeedPixClient client = new SpeedPixClient(null, null, null);

Map<String, Object> input = new HashMap<>();
input.put("prompt", "A magical forest");

// 全局 run 方法
Object output = SpeedPix.run("your-workflow-id", input, client);

// 或者直接使用（需要设置环境变量）
Object output2 = SpeedPix.run("your-workflow-id", input);
```

### 方法 3：传统预测接口

```java
import com.aliyun.speedpix.model.Prediction;

SpeedPixClient client = new SpeedPixClient(null, null, null);

Map<String, Object> input = new HashMap<>();
input.put("prompt", "A futuristic city");

try {
    // 创建预测任务
    Prediction prediction = client.predictions().create("your-workflow-id", input);
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
```

### 方法 4：后台处理

```java
// 创建任务但不等待完成
Object result = client.run(
    "your-workflow-id",
    input,
    false, // wait = false
    null, null, null, null, "default", 1.0
);

// 结果是 Prediction 对象，可以稍后检查状态
Prediction prediction = (Prediction) result;
System.out.println("任务已创建: " + prediction.getId());

// 稍后手动检查状态
prediction.reload();
if (prediction.getTaskStatus().isFinished()) {
    System.out.println("任务完成: " + prediction.getOutput());
}
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

```java
import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;

SpeedPixClient client = new SpeedPixClient(null, null, null);

try {
    Object output = client.run(
        "your-workflow-id",
        input
    );

} catch (PredictionException e) {
    System.err.println("模型执行失败: " + e.getMessage());
    if (e.getPrediction() != null) {
        System.err.println("预测 ID: " + e.getPrediction().getId());
        System.err.println("错误详情: " + e.getPrediction().getError());
    }

} catch (SpeedPixException e) {
    System.err.println("API 错误: " + e.getMessage());
    System.err.println("错误代码: " + e.getErrorCode());
    System.err.println("API 调用 ID: " + e.getApiInvokeId());

} catch (Exception e) {
    System.err.println("其他错误: " + e.getMessage());
}
```

## API 参考

### SpeedPixClient

主要客户端类，提供所有 API 访问功能。

#### 构造函数

```java
// 基础构造函数
SpeedPixClient(String endpoint, String appKey, String appSecret)

// 完整构造函数
SpeedPixClient(String endpoint, String appKey, String appSecret, String userAgent, int timeoutSeconds)
```

#### 方法

- `run(workflowId, input)` - 直接运行模型（推荐）
- `run(workflowId, input, wait, ...)` - 运行模型（完整参数）
- `predictions()` - 获取预测服务
- `files()` - 获取文件服务

### PredictionsService

- `create(workflowId, input, ...)` - 创建预测任务
- `get(predictionId)` - 获取预测状态
- `cancel(predictionId)` - 取消预测任务

### FilesService

- `create(file)` - 上传文件

### 静态方法

- `SpeedPix.run(workflowId, input)` - 全局运行方法
- `SpeedPix.run(workflowId, input, client)` - 使用指定客户端运行

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
