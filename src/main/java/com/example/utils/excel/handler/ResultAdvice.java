package com.example.utils.excel.handler;


import com.example.utils.excel.option.PoiOptions;

import java.util.List;

/**
 * @author zhuangqianliao
 * 结果增强
 * 读取完 Excel 并转换成对应的 Class 之后
 * 可以继续后续处理逻辑
 *
 * eg: 可以对读取后的数据进行数据校验
 */
public interface ResultAdvice<T> {

    /**
     * 对从Excel读取后的原始数据，进行增强处理
     * @param options: POIOptions
     * @param results: excel 读取的原始数据
     * @return: 增强后的数据
     * @throws Exception
     */
    List<T> advice(PoiOptions options, List<T> results) throws Exception;
}
