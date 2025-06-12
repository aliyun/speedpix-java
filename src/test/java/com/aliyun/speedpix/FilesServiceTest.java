package com.aliyun.speedpix;

import com.aliyun.speedpix.exception.SpeedPixException;
import com.aliyun.speedpix.service.FilesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FilesServiceTest {

    private SpeedPixClient client;
    private FilesService filesService;

    @BeforeEach
    void setUp() {
        client = new SpeedPixClient("test-key", "test-secret", "http://test.endpoint");
        filesService = client.files();
    }

    @Test
    void testCreateWithNonExistentFile() {
        File nonExistentFile = new File("non-existent-file.jpg");

        SpeedPixException exception = assertThrows(SpeedPixException.class, () -> {
            filesService.create(nonExistentFile);
        });

        assertTrue(exception.getMessage().contains("文件不存在"));
    }

    @Test
    void testCreateWithInputStream() {
        String testContent = "test file content";
        InputStream inputStream = new ByteArrayInputStream(testContent.getBytes());

        // 由于这是单元测试，我们期望抛出异常（因为没有真实的服务端）
        assertThrows(SpeedPixException.class, () -> {
            filesService.create(inputStream, "test.txt");
        });
    }

    @Test
    void testCreateWithPath() throws IOException {
        // 创建临时文件
        Path tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, "test content".getBytes());

        try {
            // 由于这是单元测试，我们期望抛出异常（因为没有真实的服务端）
            assertThrows(SpeedPixException.class, () -> {
                filesService.create(tempFile);
            });
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testServiceNotNull() {
        assertNotNull(filesService);
    }
}
