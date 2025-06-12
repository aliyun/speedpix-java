package com.aliyun.speedpix.examples;

import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.exception.PredictionException;
import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.ImageOutput;
import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.util.OutputConverterUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础使用示例
 */
public class BasicUsageExample {

    public static class ResultDTO {
        private ImageOutput images;

        public ImageOutput getImages() {
            return images;
        }

        public void setImages(ImageOutput images) {
            this.images = images;
        }

        public ResultDTO() {
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
        r.getOutput().getImages().save("result.png");
        // or write to another stream using inputStream result.getImages().getInputStream()
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
            Prediction prediction = client.predictions().create(ComfyPromptRequest.builder()
                .workflowId("01jvp41b358md06w46fz1yz78a")
                .aliasId("main")
                .inputs(input)
                .build());
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
