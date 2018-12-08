package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author zhuangqianliao
 */
public final class PoiFile<T extends File> implements Source<T> {

    private final T t;

    public PoiFile(T t) {
        this.t = t;
    }

    public String extension() {
        return FilenameUtils.getExtension(t.getName());
    }

    public String name() {
        return t.getName();
    }

    @Override
    public T get() {
        return t;
    }

    @Override
    public PoiExcelType type() {
        return PoiExcelType.from(extension());
    }
}
