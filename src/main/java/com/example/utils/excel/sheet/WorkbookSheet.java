package com.example.utils.excel.sheet;

import com.example.utils.excel.option.PoiOptions;

/**
 * @author zhuangqianliao
 */
public interface WorkbookSheet<T> {

    Source<?> getSource();

    int getRows();

    PoiOptions getOptions();
}
