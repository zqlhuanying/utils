package com.example.utils.excel.sheet.write;

import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;
import com.example.utils.excel.storage.StorageService;

import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface WorkbookWriter<T, R> {

    Source<?> getSource();

    PoiOptions getOptions();

    R write(final List<T> values, final Class<T> clazz);

    OutputStream getOutputStream();

    StorageService getStorage();

    R save(OutputStream outputStream);
}
