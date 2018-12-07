package com.example.utils.excel.handler;

import com.example.utils.excel.option.PoiOptions;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * @author zhuangqianliao
 * 错误处理
 * 线程池环境下，如果出现异常，可以通过该接口，进行后续处理
 */
public interface ErrorHandler<T> {

    /**
     * 对出错的记录，进行处理
     * @param options: POIOptions
     * @param errors: 出错的数据
     * @param e: 出错信息
     */
    void handle(PoiOptions options, List<T> errors, Exception e);
}
