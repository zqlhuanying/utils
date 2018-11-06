package com.example.utils.excel.sheet.write1;

import java.util.List;

/**
 * @author qianliao.zhuang
 */
public interface WorkbookWriter<T> {

    String write(final List<T> values, final Class clazz);
}
