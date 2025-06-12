package com.aliyun.speedpix.examples;

import com.aliyun.speedpix.SpeedPix;
import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.ComfyPromptRequest;
import com.aliyun.speedpix.model.Prediction;

import java.util.HashMap;
import java.util.Map;

/**
 * ComfyPromptRequest 使用示例
 */
public class ComfyPromptRequestExample {

    public static void main(String[] args) {
        try {
            // 创建客户端
            SpeedPixClient client = new SpeedPixClient();

            // 准备输入参数
            Map<String, Object> input = new HashMap<>();
            input.put("prompt", "A beautiful landscape painting");
            input.put("width", 1024);
            input.put("height", 1024);

            // 方法 1: 使用Builder模式创建ComfyPromptRequest
            ComfyPromptRequest request = ComfyPromptRequest.builder("01jvp41b358md06w46fz1yz78a")
                .inputs(input)
                .aliasId("main")
                .randomiseSeeds(true)
                .returnTempFiles(false)
                .build();

            // 运行并获取结果
            Prediction output = client.run(request);
            System.out.println("运行结果: " + output);

            // 方法 2: 使用静态工厂方法
            Object output2 = SpeedPix.run(request);
            System.out.println("静态方法结果: " + output2);

            // 方法 3: 不等待完成，后台运行
            ComfyPromptRequest backgroundRequest = ComfyPromptRequest.builder("01jvp41b358md06w46fz1yz78a")
                .inputs(input)
                .aliasId("main")
                .build();

            Prediction prediction = client.run(backgroundRequest, "default", false, 1.0);

            System.out.println("后台任务ID: " + prediction.getId());
            System.out.println("初始状态: " + prediction.getStatus());

            // 后续可以使用 prediction.waitForOutput() 等待完成
            prediction.waitForCompletion();
            System.out.println("最终结果: " + prediction.getOutput());

            // 方法 4: 传统方式（向后兼容）
            Prediction traditionalOutput = client.run("01jvp41b358md06w46fz1yz78a", input);
            System.out.println("传统方式结果: " + traditionalOutput);

        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
