package com.aliyun.speedpix.examples;

import com.aliyun.speedpix.SpeedPixClient;
import com.aliyun.speedpix.model.FileObject;
import com.aliyun.speedpix.model.FileOutput;
import com.aliyun.speedpix.model.FileUploadOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * FilesService 重构功能演示
 * 展示两步上传、MIME 检测和多种文件上传方式
 */
public class FilesServiceDemo {



    public static void main(String[] args) {
        try {
            // 创建客户端（请替换为真实的配置）
            SpeedPixClient client = new SpeedPixClient();

            System.out.println("=== SpeedPix FilesService 重构功能演示 ===\n");

            // 创建测试文件
            createTestFiles();

            // 1. 基本文件上传演示
            demonstrateBasicFileUpload(client);

            // 2. MIME 类型检测演示
            demonstrateMimeDetection(client);

            // 3. InputStream 上传演示
            demonstrateInputStreamUpload(client);

            // 4. 字节数组上传演示
            demonstrateByteArrayUpload(client);

            // 5. 自动文件处理演示
            demonstrateAutomaticFileHandling(client);

            // 6. 输出转换演示
            demonstrateOutputTransformation();

            // 清理测试文件
            cleanupTestFiles();

            System.out.println("\n✅ 所有演示完成！");

        } catch (Exception e) {
            System.err.println("❌ 演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 1. 基本文件上传演示
     */
    private static void demonstrateBasicFileUpload(SpeedPixClient client) {
        System.out.println("1️⃣ 基本文件上传演示");
        System.out.println("================");

        try {
            // 使用 File 对象上传
            File imageFile = new File("test_image.jpg");
            if (imageFile.exists()) {
                FileObject uploadedFile = client.files().create(imageFile);
                System.out.println("✅ File 对象上传成功:");
                System.out.println("   文件名: " + uploadedFile.getName());
                System.out.println("   内容类型: " + uploadedFile.getContentType());
                System.out.println("   访问 URL: " + uploadedFile.getAccessUrl());
                System.out.println("   文件大小: " + uploadedFile.getSize() + " bytes");
            }

            // 使用 Path 对象上传
            Path textPath = Paths.get("test_text.txt");
            if (Files.exists(textPath)) {
                FileObject uploadedText = client.files().create(textPath);
                System.out.println("\n✅ Path 对象上传成功:");
                System.out.println("   内容类型: " + uploadedText.getContentType());
                System.out.println("   访问 URL: " + uploadedText.getAccessUrl());
            }

        } catch (Exception e) {
            System.out.println("⚠️ 基本上传演示跳过 (需要真实的 API 配置): " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 2. MIME 类型检测演示
     */
    private static void demonstrateMimeDetection(SpeedPixClient client) {
        System.out.println("2️⃣ MIME 类型自动检测演示");
        System.out.println("===================");

        try {
            // 创建不同类型的测试文件
            createMimeTestFiles();

            String[] testFiles = {
                "test_jpeg.jpg",
                "test_png.png",
                "test_pdf.pdf",
                "test_unknown.bin"
            };

            for (String filename : testFiles) {
                File testFile = new File(filename);
                if (testFile.exists()) {
                    try {
                        FileObject uploaded = client.files().create(testFile);
                        System.out.println("✅ " + filename + ":");
                        System.out.println("   检测类型: " + uploaded.getContentType());
                        System.out.println("   文件大小: " + uploaded.getSize() + " bytes");
                    } catch (Exception e) {
                        System.out.println("⚠️ " + filename + " 上传失败: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ MIME 检测演示跳过: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 3. InputStream 上传演示
     */
    private static void demonstrateInputStreamUpload(SpeedPixClient client) {
        System.out.println("3️⃣ InputStream 上传演示");
        System.out.println("==================");

        try {
            // 方式 1: InputStream + 文件名
            File testFile = new File("test_image.jpg");
            if (testFile.exists()) {
                try (FileInputStream fis = new FileInputStream(testFile)) {
                    FileObject uploaded1 = client.files().create(fis, "stream_upload.jpg");
                    System.out.println("✅ InputStream + 文件名上传:");
                    System.out.println("   内容类型: " + uploaded1.getContentType());
                    System.out.println("   文件名: " + uploaded1.getName());
                }
            }

            // 方式 2: InputStream + FileUploadOptions
            if (testFile.exists()) {
                try (FileInputStream fis = new FileInputStream(testFile)) {
                    FileUploadOptions options = FileUploadOptions.builder()
                        .filename("custom_name.jpeg")
                        .contentType("image/jpeg");

                    FileObject uploaded2 = client.files().create(fis, options);
                    System.out.println("\n✅ InputStream + Options 上传:");
                    System.out.println("   内容类型: " + uploaded2.getContentType());
                    System.out.println("   文件名: " + uploaded2.getName());
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️ InputStream 上传演示跳过: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 4. 字节数组上传演示
     */
    private static void demonstrateByteArrayUpload(SpeedPixClient client) {
        System.out.println("4️⃣ 字节数组上传演示");
        System.out.println("===============");

        try {
            // 创建测试数据
            String textContent = "这是一个文本文件的内容，用于演示字节数组上传功能。";
            byte[] textBytes = textContent.getBytes("UTF-8");

            // 上传字节数组
            FileObject uploaded = client.files().create(textBytes, "byte_array.txt", "text/plain");
            System.out.println("✅ 字节数组上传成功:");
            System.out.println("   内容类型: " + uploaded.getContentType());
            System.out.println("   文件大小: " + uploaded.getSize() + " bytes");
            System.out.println("   访问 URL: " + uploaded.getAccessUrl());

        } catch (Exception e) {
            System.out.println("⚠️ 字节数组上传演示跳过: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 5. 自动文件处理演示
     */
    private static void demonstrateAutomaticFileHandling(SpeedPixClient client) {
        System.out.println("5️⃣ 自动文件处理演示");
        System.out.println("===============");

        try {
            // 创建包含文件路径的输入
            Map<String, Object> input = new HashMap<>();
            input.put("image", "test_image.jpg");  // 文件路径会自动上传
            input.put("prompt", "处理这张图片");
            input.put("style", "artistic");

            System.out.println("✅ 原始输入:");
            input.forEach((k, v) -> System.out.println("   " + k + ": " + v));

            // 在实际应用中，client.run() 会自动处理文件上传
            System.out.println("\n📝 说明: 在 client.run() 调用时，'image' 字段的文件路径");
            System.out.println("       会自动上传并替换为访问 URL");

        } catch (Exception e) {
            System.out.println("⚠️ 自动文件处理演示跳过: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 6. 输出转换演示
     */
    private static void demonstrateOutputTransformation() {
        System.out.println("6️⃣ 输出转换演示");
        System.out.println("=============");

        try {
            // 模拟 API 返回的输出（包含 URL）
            Map<String, Object> mockOutput = new HashMap<>();
            mockOutput.put("result_image", "https://example.com/result.jpg");
            mockOutput.put("thumbnail", "https://example.com/thumb.png");
            mockOutput.put("metadata", "处理完成");

            System.out.println("✅ 模拟 API 输出:");
            mockOutput.forEach((k, v) -> System.out.println("   " + k + ": " + v));

            // 使用手动转换演示输出转换功能
            Map<String, Object> transformedOutput = new HashMap<>();
            for (Map.Entry<String, Object> entry : mockOutput.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof String && isHttpUrl((String) value)) {
                    transformedOutput.put(key, new FileOutput((String) value));
                } else {
                    transformedOutput.put(key, value);
                }
            }

            System.out.println("\n✅ 转换后的输出:");
            transformedOutput.forEach((k, v) -> {
                if (v instanceof FileOutput) {
                    FileOutput fileOutput = (FileOutput) v;
                    System.out.println("   " + k + ": FileOutput(" + fileOutput.getUrl() + ")");
                    System.out.println("     可以调用 .read() 下载内容");
                    System.out.println("     可以调用 .save(path) 保存文件");
                } else {
                    System.out.println("   " + k + ": " + v);
                }
            });

        } catch (Exception e) {
            System.out.println("⚠️ 输出转换演示出错: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * 创建测试文件
     */
    private static void createTestFiles() throws IOException {
        // 创建测试图片文件 (JPEG 签名)
        byte[] jpegSignature = {(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};
        byte[] jpegData = new byte[1024];
        System.arraycopy(jpegSignature, 0, jpegData, 0, jpegSignature.length);
        // 填充随机数据
        for (int i = jpegSignature.length; i < jpegData.length; i++) {
            jpegData[i] = (byte) ThreadLocalRandom.current().nextInt(256);
        }
        Files.write(Paths.get("test_image.jpg"), jpegData);

        // 创建测试文本文件
        String textContent = "这是一个测试文本文件\n用于演示 MIME 类型检测\n包含中文内容";
        Files.write(Paths.get("test_text.txt"), textContent.getBytes("UTF-8"));
    }

    /**
     * 创建 MIME 检测测试文件
     */
    private static void createMimeTestFiles() throws IOException {
        // JPEG 文件
        byte[] jpegData = {(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10};
        Files.write(Paths.get("test_jpeg.jpg"), jpegData);

        // PNG 文件
        byte[] pngData = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        Files.write(Paths.get("test_png.png"), pngData);

        // PDF 文件
        byte[] pdfData = {0x25, 0x50, 0x44, 0x46, 0x2D, 0x31, 0x2E, 0x34};
        Files.write(Paths.get("test_pdf.pdf"), pdfData);

        // 未知格式文件
        byte[] unknownData = {0x12, 0x34, 0x56, 0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE, (byte)0xF0};
        Files.write(Paths.get("test_unknown.bin"), unknownData);
    }

    /**
     * 清理测试文件
     */
    private static void cleanupTestFiles() {
        String[] filesToDelete = {
            "test_image.jpg", "test_text.txt",
            "test_jpeg.jpg", "test_png.png",
            "test_pdf.pdf", "test_unknown.bin"
        };

        for (String filename : filesToDelete) {
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                System.err.println("清理文件失败: " + filename);
            }
        }
    }

    /**
     * 检查字符串是否为 HTTP/HTTPS URL
     */
    private static boolean isHttpUrl(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        String trimmed = str.trim().toLowerCase();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://");
    }
}
