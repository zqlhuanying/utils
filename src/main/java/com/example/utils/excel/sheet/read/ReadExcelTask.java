package com.example.utils.excel.sheet.read;

import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.TaskBuilder;
import com.google.common.collect.FluentIterable;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class ReadExcelTask<T> extends RecursiveTask<List<T>> {

    private static final int THRESHOLD = 20;
    private final Sheet sheet;
    private final Class<T> type;
    private final PoiOptions options;
    private final ResultAdvice<T> advice;
    private final ErrorHandler errorHandler;
    private int start;
    private int end;

    public ReadExcelTask(TaskBuilder<T> builder) {
        this(
                builder.getSheet(),
                builder.getType(),
                builder.getOptions(),
                builder.getAdvice(),
                builder.getErrorHandler(),
                builder.getOptions().getSkip(),
                builder.getSheet() == null ? 0 : builder.getSheet().getLastRowNum()
        );
    }

    private ReadExcelTask(Sheet sheet, Class<T> type, PoiOptions options,
                          ResultAdvice<T> advice, ErrorHandler errorHandler,
                          int start, int end) {
        this.sheet = sheet;
        this.type = type;
        this.options = options;
        this.advice = advice;
        this.errorHandler = errorHandler;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<T> compute() {
        List<T> result = new ArrayList<>(this.end - this.start);

        boolean canCompute = (this.end - this.start) <= THRESHOLD;
        if (canCompute) {
            result.addAll(doCompute());
        } else {
            int middle = (this.start + this.end) >> 1;
            ReadExcelTask<T> leftTask = new ReadExcelTask<>(
                    sheet, this.type, this.options,
                    this.advice, this.errorHandler,
                    this.start, middle
            );
            ReadExcelTask<T> rightTask = new ReadExcelTask<>(
                    sheet, this.type, this.options,
                    this.advice, this.errorHandler,
                    middle + 1, this.end
            );
            // fork
            invokeAll(leftTask, rightTask);
            // wait
            List<T> leftRes = leftTask.join();
            List<T> rightRes = rightTask.join();
            result.addAll(leftRes);
            result.addAll(rightRes);
        }
        return result;
    }

    /**
     * 小任务计算
     * 如果有异常，则此次小任务全部失败，但不会影响到整体失败
     * @return List<T>
     */
    private List<T> doCompute() {
        if (this.end < this.start) {
            return Collections.emptyList();
        }
        try {
            List<T> results = new WorkbookReadSheet<T>(this.sheet, this.options)
                    .read(this.start, this.end, this.type);
            if (this.advice != null) {
                results = this.advice.advice(this.options, results);
            }
            return results;
        } catch (Exception e) {
            List<Row> errorRows = getErrorRows();
            log.error("read values from sheet failed! Values: {}",
                     errorRows, e);

            if (this.errorHandler != null) {
                this.errorHandler.handle(this.options, errorRows, e);
            }
            return Collections.emptyList();
        }
    }

    private List<Row> getErrorRows() {
        return FluentIterable.from(this.sheet)
                .skip(this.start)
                .limit(this.end - this.start + 1)
                .toList();
    }
}
