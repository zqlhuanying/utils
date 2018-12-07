/*package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.TaskBuilder;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.concurrent.RecursiveAction;

*//**
 * @author zhuangqianliao
 *//*
@Slf4j
public class WriteExcelTask<T> extends RecursiveAction {

    private static final int THRESHOLD = 20;
    private final Sheet sheet;
    private final Class<T> type;
    private final Iterable<T> values;
    private final PoiOptions options;
    private final ResultAdvice<T> advice;
    private final ErrorHandler errorHandler;
    private int start;
    private int end;
    private int startRow;

    public WriteExcelTask(TaskBuilder<T> builder) {
        this(
                builder.getSheet(),
                builder.getType(),
                builder.getValues(),
                builder.getOptions(),
                builder.getAdvice(),
                builder.getErrorHandler(),
                0,
                Iterables.size(builder.getValues()) - 1,
                builder.getOptions().getSkip() + 1
        );
    }

    private WriteExcelTask(Sheet sheet, Class<T> type, Iterable<T> values,
                           PoiOptions options, ResultAdvice<T> advice, ErrorHandler errorHandler,
                           int start, int end, int startRow) {
        this.sheet = sheet;
        this.type = type;
        this.values = values;
        this.options = options;
        this.advice = advice;
        this.errorHandler = errorHandler;
        this.start = start;
        this.end = end;
        this.startRow = startRow;
    }

    @Override
    protected void compute() {
        boolean canCompute = (this.end - this.start) <= THRESHOLD;
        if (canCompute) {
            doCompute();
        } else {
            int middle = (this.start + this.end) >> 1;
            WriteExcelTask<T> leftTask = new WriteExcelTask<>(
                    sheet, this.type, this.values,
                    this.options, this.advice, this.errorHandler,
                    this.start, middle, this.startRow
            );
            WriteExcelTask<T> rightTask = new WriteExcelTask<>(
                    sheet, this.type, this.values,
                    this.options, this.advice, this.errorHandler,
                    middle + 1, this.end, middle + 1 + this.options.getSkip() + 1
            );
            // fork
            invokeAll(leftTask, rightTask);
            // wait
            leftTask.join();
            rightTask.join();
        }
    }

    *//**
     * 小任务计算
     * 如果有异常，则此次小任务全部失败，但不会影响到整体失败
     * @return List<T>
     *//*
    private void doCompute() {
        if (this.end < this.start) {
            return;
        }
        try {
            new WorkbookWriteSheet<T>(this.sheet, this.options)
                    .write(this.start, this.end, this.startRow,
                            this.values);
        } catch (Exception e) {
            List<T> errors = errors();
            log.error("write values to sheet failed! Values: {}",
                    JSONObject.toJSONString(errors), e);

*//*            if (this.errorHandler != null) {
                this.errorHandler.handle(this.options, errors, e);
            }*//*
        }
    }

    private List<T> errors() {
        return FluentIterable.from(this.values)
                .skip(this.start)
                .limit(this.end - this.start + 1)
                .toList();
    }
}*/
