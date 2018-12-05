package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSON;
import com.example.utils.excel.exception.PoiOverThresholdException;
import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.TaskBuilder;
import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookBigWriteSheet<T> extends WorkbookWriteSheet<T> {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(THREADS);

    private static final int THRESHOLD = 0x0000ffff;

    @Getter
    private ResultAdvice<T> advice;
    @Getter
    private ErrorHandler errorHandler;

    public WorkbookBigWriteSheet(AbstractWorkbookWriter<T> writer,
                                 Workbook workbook, Sheet sheet, PoiOptions options) {
        super(writer, workbook, sheet, options);
    }

    @Override
    public void writeContent(Iterable<T> values) {
        check(values);

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) values.iterator().next().getClass();
        WriteExcelTask<T> task = new TaskBuilder<>(sheet, options, type, values)
                .setAdvice(advice)
                .setErrorHandler(errorHandler)
                .buildWriteTask();
        Future<?> res = FORK_JOIN_POOL.submit(task);
        try {
            res.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("execution task: {} failed!", JSON.toJSONString(task), e);
        }
    }

    public WorkbookBigWriteSheet<T> setAdvice(ResultAdvice<T> advice) {
        this.advice = advice;
        return this;
    }

    public WorkbookBigWriteSheet<T> setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    private void check(Iterable<T> values) {
        int threshold = options == null ? THRESHOLD : options.getThreshold();
        if (Iterables.size(values) > threshold) {
            throw new PoiOverThresholdException(threshold);
        }
    }
}
