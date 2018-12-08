package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;

import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
public final class PoiInputStream<T extends InputStream> implements Source<T> {

    private final T t;
    private final PoiExcelType type;

    public PoiInputStream(T t, PoiExcelType type) {
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
