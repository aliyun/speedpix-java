package com.aliyun.speedpix.exception;

import com.aliyun.speedpix.model.Prediction;

/**
 * 预测任务执行错误
 */
public class PredictionException extends SpeedPixException {

    private Prediction prediction;

    public PredictionException(String message) {
        super(message);
    }

    public PredictionException(Prediction prediction) {
        super(prediction.getError() != null ? prediction.getError() : "Prediction execution failed");
        this.prediction = prediction;
    }

    public PredictionException(String message, Prediction prediction) {
        super(message);
        this.prediction = prediction;
    }

    public Prediction getPrediction() {
        return prediction;
    }
}
