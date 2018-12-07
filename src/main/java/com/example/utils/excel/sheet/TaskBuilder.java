/*package com.example.utils.excel.sheet;

import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.read.ReadExcelTask;
import com.example.utils.excel.sheet.write.WriteExcelTask;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;


*//**
 * @author zhuangqianliao
 *//*
@Data
public class TaskBuilder<T> {

    private Sheet sheet;
    private Class<T> type;
    private PoiOptions options;
    private Iterable<T> values;
    private ResultAdvice<T> advice;
    private ErrorHandler errorHandler;

    public TaskBuilder(Sheet sheet, PoiOptions options, Class<T> type) {
        this.sheet = sheet;
        this.options = options;
        this.type = type;
    }

    public TaskBuilder(Sheet sheet, PoiOptions options, Class<T> type, Iterable<T> values) {
        this.sheet = sheet;
        this.options = options;
        this.type = type;
        this.values = values;
    }

    public TaskBuilder<T> setAdvice(ResultAdvice<T> advice) {
        this.advice = advice;
        return this;
    }

    public TaskBuilder<T> setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public ReadExcelTask<T> buildReadTask() {
        return new ReadExcelTask<>(this);
    }

    public WriteExcelTask<T> buildWriteTask() {
        return new WriteExcelTask<>(this);
    }
}*/
