package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;

/**
 * @author qianliao.zhuang
 * Data Source
 * eg. PoiFile or PoiInputStream
 */
public interface Source<T> {

    /**
     * Get Data Source
     */
    T get();

    /**
     * source type
     * @return PoiExcelType
     */
    PoiExcelType type();
}
