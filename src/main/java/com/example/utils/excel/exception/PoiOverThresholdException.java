package com.example.utils.excel.exception;

/**
 * @author zhuangqianliao
 */
public class PoiOverThresholdException extends RuntimeException {

    public PoiOverThresholdException(String message) {
        super(message);
    }

    public PoiOverThresholdException(String message, Throwable cause) {
        super(message, cause);
    }
}
