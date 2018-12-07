package com.example.utils.excel.sheet.read;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.exception.PoiOverThresholdException;
import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.google.common.collect.FluentIterable;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * @author zhuangqianliao
 * Use Fork/Join to read
 */
@Slf4j
public class WorkbookBigReader<T> extends FilterWorkbookReader<T>{

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(THREADS);
    private static final int THRESHOLD = 0x0000ffff;

    @Getter
    private ResultAdvice<T> advice;
    @Getter
    private ErrorHandler errorHandler;

    public WorkbookBigReader(WorkbookReader<T> reader) {
        super(reader);

        int threshold = getReadSheet().options == null ?
                THRESHOLD : getReadSheet().options.getThreshold();
        if (getReadSheet().getRows() > threshold) {
            throw new PoiOverThresholdException(threshold);
        }
        if (!(getReadSheet() instanceof ForkJoin)) {
            throw new PoiException(
                    String.format("WorkbookReadSheet[%s] can not supported fork/join", getReadSheet().getClass().getName())
            );
        }
    }

    @Override
    public List<T> read(Class<T> type) {
        ReadExcelTask<T> task = new TaskBuilder<>(getReadSheet(), getReadSheet().options, type)
                .setAdvice(advice)
                .setErrorHandler(errorHandler)
                .build();
        Future<List<T>> res = FORK_JOIN_POOL.submit(task);
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("execution task: {} failed!", JSONObject.toJSONString(task), e);
            return Collections.emptyList();
        }
    }

    public WorkbookBigReader<T> setAdvice(ResultAdvice<T> advice) {
        this.advice = advice;
        return this;
    }

    public WorkbookBigReader<T> setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Data
    private static class TaskBuilder<T> {
        private WorkbookReadSheet<T> sheet;
        private Class<T> type;
        private PoiOptions options;
        private ResultAdvice<T> advice;
        private ErrorHandler errorHandler;

        public TaskBuilder(WorkbookReadSheet<T> sheet, PoiOptions options, Class<T> type) {
            this.sheet = sheet;
            this.options = options;
            this.type = type;
        }

        public TaskBuilder<T> setAdvice(ResultAdvice<T> advice) {
            this.advice = advice;
            return this;
        }

        public TaskBuilder<T> setErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public ReadExcelTask<T> build() {
            return new ReadExcelTask<>(this);
        }
    }

    @Slf4j
    private static class ReadExcelTask<T> extends RecursiveTask<List<T>> {

        private static final int THRESHOLD = 20;
        private final WorkbookReadSheet<T> sheet;
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
                    builder.getSheet().getRows()
            );
        }

        private ReadExcelTask(
                WorkbookReadSheet<T> sheet,
                Class<T> type,
                PoiOptions options,
                ResultAdvice<T> advice,
                ErrorHandler errorHandler,
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
                List<T> results = this.sheet.read(this.start, this.end, this.type);
                if (this.advice != null) {
                    results = this.advice.advice(this.options, results);
                }
                return results;
            } catch (Exception e) {
                List<Row> errorRows = getErrorRows();
                log.error("Read values from sheet failed! Row[{}, {}]",
                        this.start, this.end, e);

                if (this.errorHandler != null) {
                    this.errorHandler.handle(this.options, errorRows, e);
                }
                return Collections.emptyList();
            }
        }

        private List<Row> getErrorRows() {
            return FluentIterable.from(this.sheet.getSheet())
                    .skip(this.start)
                    .limit(this.end - this.start + 1)
                    .toList();
        }
    }
}
