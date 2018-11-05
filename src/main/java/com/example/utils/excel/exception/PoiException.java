package com.example.utils.excel.exception;

/**
 * @author zhuangqianliao
 */
public class PoiException extends RuntimeException {

    public PoiException(String message) {
        super(message);
    }

    public PoiException(String message, Throwable cause) {
        super(message, cause);
    }
}
