package com.example.utils.excel.sheet;

import com.example.utils.excel.option.PoiOptions;

/**
 * @author zhuangqianliao
 */
public interface WorkBookSheet<T> {

    Source<?> getSource();

    PoiOptions getOptions();
}
