package com.aliyun.speedpix.examples.dto;

import com.aliyun.speedpix.model.ImageFile;

/**
 * @author chanyuan.lb
 * @description
 * @create 2025-06-12 11:11
 */
public class ResultDTO {

    private ImageFile images;

    public ResultDTO() {
    }

    public ImageFile getImages() {
        return images;
    }

    public void setImages(ImageFile images) {
        this.images = images;
    }
}
