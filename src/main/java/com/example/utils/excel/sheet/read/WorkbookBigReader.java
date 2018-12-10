package com.example.utils.excel.sheet.read;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.exception.PoiOverThresholdException;
import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
public class WorkbookBigReader<T, R> extends FilterWorkbookReader<T>{

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(THREADS);
    /**
     * rows limited
     */
    private static final int THRESHOLD = 0x0000ffff;

    @Getter
    private ResultAdvice<T> advice;
    @Getter
    private ErrorHandler<R> errorHandler;
    /**
     * small task threshold
     */
    @Getter
    private Integer taskThreshold;

    public WorkbookBigReader(WorkbookReader<T> reader) {
        super(reader);

        if (!(getReader() instanceof ForkJoin)) {
            throw new PoiException(
                    String.format("WorkbookReader[%s] can not supported fork/join", getReader().getClass().getName())
            );
        }
    }

    @Override
    public List<T> read(Class<T> type) {
        int threshold = getReader().getOptions() == null ?
                THRESHOLD : getReader().getOptions().getThreshold();
        if (getReader().getRows() > threshold) {
            throw new PoiOverThresholdException(threshold);
        }

        @SuppressWarnings("unchecked")
        ForkJoin<T, R> forkJoin = (ForkJoin<T, R>) getReader();
        int start = getReader().getOptions().getSkip();
        int end = getReader().getRows();
        ReadExcelTask<T, R> task =
                new TaskBuilder<>(
                        forkJoin, start, end, type,
                        getReader().getOptions()
                )
                .setAdvice(this.advice)
                .setErrorHandler(this.errorHandler)
                .setThreshold(this.taskThreshold)
                .build();
        Future<List<T>> res = FORK_JOIN_POOL.submit(task);
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("execution task: {} failed!", JSONObject.toJSONString(task), e);
            return Collections.emptyList();
        } finally {
            try {
                forkJoin.release();
            } catch (IOException e) {
                log.error("can not release the resource", e);
            }
        }
    }

    public WorkbookBigReader<T, R> setAdvice(ResultAdvice<T> advice) {
        this.advice = advice;
        return this;
    }

    public WorkbookBigReader<T, R> setErrorHandler(ErrorHandler<R> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public WorkbookBigReader<T, R> setTaskThreshold(Integer taskThreshold) {
        this.taskThreshold = taskThreshold;
        return this;
    }

    @Data
    private static class TaskBuilder<T, R> {
        private ForkJoin<T, R> reader;
        private int start;
        private int end;
        private Class<T> type;
        private PoiOptions options;
        private ResultAdvice<T> advice;
        private ErrorHandler<R> errorHandler;
        private Integer threshold;

        TaskBuilder(ForkJoin<T, R> reader, int start, int end, Class<T> type, PoiOptions options) {
            this.reader = reader;
            this.start = start;
            this.end = end;
            this.type = type;
            this.options = options;
        }

        TaskBuilder<T, R> setAdvice(ResultAdvice<T> advice) {
            this.advice = advice;
            return this;
        }

        TaskBuilder<T, R> setErrorHandler(ErrorHandler<R> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        TaskBuilder<T, R> setThreshold(Integer threshold) {
            this.threshold = threshold;
            return this;
        }

        public ReadExcelTask<T, R> build() {
            return new ReadExcelTask<>(this);
        }
    }

    private static class ReadExcelTask<T, R> extends RecursiveTask<List<T>> {

        private static final int DEFAULT_THRESHOLD = 20;
        private final ForkJoin<T, R> reader;
        private int start;
        private int end;
        private final Class<T> type;
        private final PoiOptions options;
        private final ResultAdvice<T> advice;
        private final ErrorHandler<R> errorHandler;
        private final Integer threshold;

        ReadExcelTask(TaskBuilder<T, R> builder) {
            this(
                    builder.getReader(),
                    builder.getStart(),
                    builder.getEnd(),
                    builder.getType(),
                    builder.getOptions(),
                    builder.getAdvice(),
                    builder.getErrorHandler(),
                    builder.getThreshold()
            );
        }

        private ReadExcelTask(
                ForkJoin<T, R> reader,
                int start,
                int end,
                Class<T> type,
                PoiOptions options,
                ResultAdvice<T> advice,
                ErrorHandler<R> errorHandler,
                Integer threshold) {
            this.reader = reader;
            this.start = start;
            this.end = end;
            this.type = type;
            this.options = options;
            this.advice = advice;
            this.errorHandler = errorHandler;
            this.threshold = threshold == null ? DEFAULT_THRESHOLD : threshold;
        }

        @Override
        protected List<T> compute() {
            List<T> result = new ArrayList<>(this.end - this.start);

            boolean canCompute = (this.end - this.start) <= this.threshold;
            if (canCompute) {
                result.addAll(doCompute());
            } else {
                int middle = (this.start + this.end) >> 1;
                ReadExcelTask<T, R> leftTask = new ReadExcelTask<>(
                        this.reader, this.start, middle, this.type,
                        this.options,
                        this.advice, this.errorHandler, this.threshold
                );
                ReadExcelTask<T, R> rightTask = new ReadExcelTask<>(
                        this.reader, middle, this.end, this.type,
                        this.options,
                        this.advice, this.errorHandler, this.threshold
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
                List<T> results = this.reader.read(this.start, this.end, this.type);
                if (this.advice != null) {
                    results = this.advice.advice(this.options, results);
                }
                return results;
            } catch (Exception e) {
                List<R> errors = this.reader.errors(this.start, this.end);
                log.error("Read values from sheet failed! Row[{}, {}]",
                        this.start, this.end, e);

                if (this.errorHandler != null) {
                    this.errorHandler.handle(this.options, errors, e);
                }
                return Collections.emptyList();
            }
        }
    }
}
