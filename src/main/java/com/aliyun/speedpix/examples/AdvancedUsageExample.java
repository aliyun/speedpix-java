package com.aliyun.speedpix.examples;

import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.FileObject;
import com.aliyun.speedpix.model.Prediction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 完整的使用示例，展示文件上传和模型推理
 */
public class AdvancedUsageExample {

    public static void main(String[] args) {        try {
            // 示例 1: 使用SpeedPix全局方法
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", "A beautiful sunset over the mountains");
            input.put("width", 1024);
            input.put("height", 1024);

            // 需要先创建客户端
            SpeedPixClient globalClient = new SpeedPixClient(
                "your-app-key",
                "your-app-secret",
                "https://your-endpoint.com"
            );

            Object result = SpeedPix.run("flux-schnell", input, globalClient);
            System.out.println("生成结果: " + result);

            // 示例 2: 使用客户端实例
            SpeedPixClient client = new SpeedPixClient(
                "your-app-key",
                "your-app-secret",
                "https://your-endpoint.com"
            );

            // 文件上传示例
            File imageFile = new File("path/to/your/image.jpg");
            if (imageFile.exists()) {
                FileObject uploadedFile = client.files().create(imageFile);
                System.out.println("文件上传成功: " + uploadedFile.getUrl());

                // 使用上传的文件进行图像处理
                Map<String, Object> imageInput = new HashMap<>();
                imageInput.put("image", uploadedFile.getUrl());
                imageInput.put("prompt", "Remove background");

                Object imageResult = client.run("background-removal", imageInput);
                System.out.println("图像处理结果: " + imageResult);
            }

            // 示例 3: 异步预测管理
            Prediction prediction = client.predictions().create("flux-schnell", input);
            System.out.println("预测ID: " + prediction.getId());
            System.out.println("初始状态: " + prediction.getStatus());

            // 轮询直到完成
            while (!prediction.getTaskStatus().isFinished()) {
                Thread.sleep(1000);
                prediction = client.predictions().get(prediction.getId());
                System.out.println("当前状态: " + prediction.getStatus());
            }

            if (prediction.getTaskStatus() == com.aliyun.speedpix.model.TaskStatus.SUCCEEDED) {
                System.out.println("预测成功完成!");
                System.out.println("结果: " + prediction.getOutput());
            } else {
                System.out.println("预测失败: " + prediction.getError());
            }

            // 示例 4: 错误处理
            try {
                client.predictions().get("non-existent-id");
            } catch (SpeedPixException e) {
                System.out.println("捕获到API错误: " + e.getMessage());
                if (e.getApiInvokeId() != null) {
                    System.out.println("API调用ID: " + e.getApiInvokeId());
                }
            }

        } catch (Exception e) {
            System.err.println("示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
