package com.example.utils.excel.exception;

/**
 * @author zhuangqianliao
 */
public class PoiExcelTypeException extends RuntimeException {

    public PoiExcelTypeException(String message) {
        super(message);
    }

    public PoiExcelTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
