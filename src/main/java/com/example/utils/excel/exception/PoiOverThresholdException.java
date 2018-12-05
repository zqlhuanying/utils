package com.example.utils.excel.exception;

/**
 * @author zhuangqianliao
 */
public class PoiOverThresholdException extends RuntimeException {

    public PoiOverThresholdException(int threshold) {
        super("Over threshold " + threshold);
    }

    public PoiOverThresholdException(int threshold, Throwable cause) {
        super("Over threshold " + threshold, cause);
    }
}
