package com.example.utils.excel.sheet;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * @author zhuangqianliao
 */
public final class PoiFile<T extends File> {

    private final T t;

    public PoiFile(T t) {
        this.t = t;
    }

    public T file() {
        return t;
    }

    public String extension() {
        return FilenameUtils.getExtension(t.getName());
    }

    public String name() {
        return t.getName();
    }
}
