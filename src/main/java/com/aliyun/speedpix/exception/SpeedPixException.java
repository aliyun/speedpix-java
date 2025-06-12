package com.aliyun.speedpix.exception;

/**
 * SpeedPix 的基础异常类
 */
public class SpeedPixException extends Exception {

    private String errorCode;
    private String apiInvokeId;

    public SpeedPixException(String message) {
        super(message);
    }

    public SpeedPixException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpeedPixException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public SpeedPixException(String message, String errorCode, String apiInvokeId) {
        super(message);
        this.errorCode = errorCode;
        this.apiInvokeId = apiInvokeId;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getApiInvokeId() {
        return apiInvokeId;
    }
}
