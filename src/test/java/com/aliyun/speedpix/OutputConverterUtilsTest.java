package com.aliyun.speedpix;

import com.aliyun.speedpix.model.Prediction;
import com.aliyun.speedpix.util.OutputConverterUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OutputConverterUtils 测试类
 */
public class OutputConverterUtilsTest {

    // 测试用的数据传输对象
    public static class ImageResult {
        private List<String> images;
        private Map<String, Object> metadata;
        private String status;

        public ImageResult() {
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class SimpleResult {
        private String result;
        private int count;

        public SimpleResult() {
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    @Test
    public void testGetObjectMapper() {
        // 测试获取 ObjectMapper
        assertNotNull(OutputConverterUtils.getObjectMapper());
        assertSame(OutputConverterUtils.getObjectMapper(), OutputConverterUtils.getObjectMapper());
    }
}
