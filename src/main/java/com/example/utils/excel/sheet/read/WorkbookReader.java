package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface WorkbookReader<T> {

    /**
     * delegate
     * @return readSheet
     */
    WorkbookReadSheet<T> getReadSheet();

    /**
     * get options
     * @return PoiOptions
     */
    PoiOptions getOptions();

    /**
     * Read from DataSource to the given type
     * @param type the given type
     * @return the list of the given type instance
     */
    List<T> read(Class<T> type);
}
