package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface WorkbookReader<T> {

    /**
     * DataSource
     */
    Source<?> getSource();

    PoiOptions getOptions();

    int getRows();

    /**
     * Read from DataSource to the given type
     * @param type the given type
     * @return the list of the given type instance
     */
    List<T> read(Class<T> type);
}
