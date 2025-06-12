package com.aliyun.speedpix.model;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    RUNNING("running"),
    FAILED("failed"),
    WAITING("waiting"),
    SUCCEEDED("succeeded"),
    CANCELLED("cancelled");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isFinished() {
        return this == FAILED || this == SUCCEEDED || this == CANCELLED;
    }

    public static TaskStatus fromValue(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return RUNNING; // default
    }
}
