/*package com.example.utils.excel.sheet.read;

import com.alibaba.fastjson.JSON;
import com.example.utils.excel.exception.PoiOverThresholdException;
import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.TaskBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

*//**
 * @author zhuangqianliao
 *//*
@Slf4j
public class WorkbookBigSheet<T> extends WorkbookReadSheet<T> {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(THREADS);

    public WorkbookBigSheet(WorkbookBigReader<T> bigReader) {
        super(bigReader.getReader()., sheet, options);
    }

    @Override
    public List<T> read(Class<T> type) {
        ReadExcelTask<T> task = new TaskBuilder<>(, options, type)
                .setAdvice(advice)
                .setErrorHandler(errorHandler)
                .buildReadTask();
        Future<List<T>> res = FORK_JOIN_POOL.submit(task);
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("execution task: {} failed!", JSON.toJSONString(task), e);
            return Collections.emptyList();
        }
    }
}*/
