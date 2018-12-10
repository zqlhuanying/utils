package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhuangqianliao
 */
public final class PoiOutputStream<T extends OutputStream> implements Source<T> {

    private final T t;
    private final PoiExcelType type;

    public PoiOutputStream(T t, PoiExcelType type) {
        this.t = t;
        this.type = type;
    }

    @Override
    public PoiExcelType type() {
        return type;
    }

    @Override
    public T get() {
        return t;
    }
}
