package com.aliyun.speedpix.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * MIME 类型检测工具
 * 基于文件的魔术字节（Magic Numbers）检测文件类型
 */
public class MimeTypeDetector {

    /**
     * 从 InputStream 检测 MIME 类型
     *
     * @param inputStream 输入流
     * @return 检测到的 MIME 类型，如果无法识别则返回 "application/octet-stream"
     */
    public static String detectMimeType(InputStream inputStream) {
        if (inputStream == null) {
            return "application/octet-stream";
        }

        try {
            // 标记流，以便稍后重置
            inputStream.mark(32);

            // 读取前 32 字节用于检测
            byte[] buffer = new byte[32];
            int bytesRead = inputStream.read(buffer);

            // 重置流到标记位置
            inputStream.reset();

            if (bytesRead < 2) {
                return "application/octet-stream";
            }

            return detectMimeTypeFromBytes(buffer, bytesRead);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    /**
     * 从字节数组检测 MIME 类型
     *
     * @param bytes 字节数组
     * @return 检测到的 MIME 类型
     */
    public static String detectMimeType(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "application/octet-stream";
        }

        return detectMimeTypeFromBytes(bytes, bytes.length);
    }

    /**
     * 基于魔术字节检测 MIME 类型
     */
    private static String detectMimeTypeFromBytes(byte[] buffer, int length) {
        if (length < 2) {
            return "application/octet-stream";
        }

        // JPEG 检测 (FF D8 FF)
        if (length >= 3 &&
            (buffer[0] & 0xFF) == 0xFF &&
            (buffer[1] & 0xFF) == 0xD8 &&
            (buffer[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        // PNG 检测 (89 50 4E 47 0D 0A 1A 0A)
        if (length >= 8 &&
            (buffer[0] & 0xFF) == 0x89 &&
            (buffer[1] & 0xFF) == 0x50 &&
            (buffer[2] & 0xFF) == 0x4E &&
            (buffer[3] & 0xFF) == 0x47 &&
            (buffer[4] & 0xFF) == 0x0D &&
            (buffer[5] & 0xFF) == 0x0A &&
            (buffer[6] & 0xFF) == 0x1A &&
            (buffer[7] & 0xFF) == 0x0A) {
            return "image/png";
        }

        // GIF 检测 (47 49 46 38)
        if (length >= 4 &&
            (buffer[0] & 0xFF) == 0x47 &&
            (buffer[1] & 0xFF) == 0x49 &&
            (buffer[2] & 0xFF) == 0x46 &&
            (buffer[3] & 0xFF) == 0x38) {
            return "image/gif";
        }

        // WebP 检测 (52 49 46 46 ... 57 45 42 50)
        if (length >= 12 &&
            (buffer[0] & 0xFF) == 0x52 &&
            (buffer[1] & 0xFF) == 0x49 &&
            (buffer[2] & 0xFF) == 0x46 &&
            (buffer[3] & 0xFF) == 0x46 &&
            (buffer[8] & 0xFF) == 0x57 &&
            (buffer[9] & 0xFF) == 0x45 &&
            (buffer[10] & 0xFF) == 0x42 &&
            (buffer[11] & 0xFF) == 0x50) {
            return "image/webp";
        }

        // MP4 检测 (... 66 74 79 70)
        if (length >= 8) {
            for (int i = 0; i <= length - 4; i++) {
                if ((buffer[i] & 0xFF) == 0x66 &&
                    (buffer[i + 1] & 0xFF) == 0x74 &&
                    (buffer[i + 2] & 0xFF) == 0x79 &&
                    (buffer[i + 3] & 0xFF) == 0x70) {
                    return "video/mp4";
                }
            }
        }

        // PDF 检测 (25 50 44 46)
        if (length >= 4 &&
            (buffer[0] & 0xFF) == 0x25 &&
            (buffer[1] & 0xFF) == 0x50 &&
            (buffer[2] & 0xFF) == 0x44 &&
            (buffer[3] & 0xFF) == 0x46) {
            return "application/pdf";
        }

        // ZIP 检测 (50 4B 03 04 或 50 4B 05 06 或 50 4B 07 08)
        if (length >= 4 &&
            (buffer[0] & 0xFF) == 0x50 &&
            (buffer[1] & 0xFF) == 0x4B &&
            ((buffer[2] & 0xFF) == 0x03 || (buffer[2] & 0xFF) == 0x05 || (buffer[2] & 0xFF) == 0x07)) {
            return "application/zip";
        }

        // BMP 检测 (42 4D)
        if (length >= 2 &&
            (buffer[0] & 0xFF) == 0x42 &&
            (buffer[1] & 0xFF) == 0x4D) {
            return "image/bmp";
        }

        // SVG 检测 (文本形式)
        if (length >= 5) {
            String start = new String(buffer, 0, Math.min(length, 100)).toLowerCase();
            if (start.contains("<svg") || start.contains("<?xml") && start.contains("svg")) {
                return "image/svg+xml";
            }
        }

        // 检查是否可能是文本文件
        if (isPossibleTextContent(buffer, length)) {
            return "text/plain";
        }

        return "application/octet-stream";
    }

    /**
     * 检查内容是否可能是文本
     */
    private static boolean isPossibleTextContent(byte[] buffer, int length) {
        int printableCount = 0;
        int totalCount = Math.min(length, 1024); // 只检查前 1KB

        for (int i = 0; i < totalCount; i++) {
            int b = buffer[i] & 0xFF;

            // 可打印 ASCII 字符 (32-126) 或常见控制字符 (9,10,13)
            if ((b >= 32 && b <= 126) || b == 9 || b == 10 || b == 13) {
                printableCount++;
            } else if (b == 0) {
                // 如果包含 null 字节，很可能是二进制文件
                return false;
            }
        }

        // 如果超过 80% 是可打印字符，认为是文本
        return (double) printableCount / totalCount > 0.8;
    }

    /**
     * 根据文件扩展名猜测 MIME 类型（备用方法）
     */
    public static String guessMimeTypeFromFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "application/octet-stream";
        }

        String extension = filename.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else if (extension.endsWith(".bmp")) {
            return "image/bmp";
        } else if (extension.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (extension.endsWith(".mp4")) {
            return "video/mp4";
        } else if (extension.endsWith(".pdf")) {
            return "application/pdf";
        } else if (extension.endsWith(".zip")) {
            return "application/zip";
        } else if (extension.endsWith(".txt")) {
            return "text/plain";
        } else if (extension.endsWith(".json")) {
            return "application/json";
        } else if (extension.endsWith(".xml")) {
            return "application/xml";
        } else {
            return "application/octet-stream";
        }
    }
}
