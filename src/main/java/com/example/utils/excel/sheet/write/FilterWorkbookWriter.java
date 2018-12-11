package com.example.utils.excel.sheet.write;


import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;
import com.example.utils.excel.storage.StorageService;

import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public class FilterWorkbookWriter<T, R> implements WorkbookWriter<T, R> {

    private WorkbookWriter<T, R> writer;

    public FilterWorkbookWriter(WorkbookWriter<T, R> writer) {
        this.writer = writer;
    }

    @Override
    public Source<?> getSource() {
        return this.writer.getSource();
    }

    @Override
    public PoiOptions getOptions() {
        return this.writer.getOptions();
    }

    @Override
    public R write(List<T> values, Class<T> clazz) {
        return this.writer.write(values, clazz);
    }

    @Override
    public OutputStream getOutputStream() {
        return this.writer.getOutputStream();
    }

    @Override
    public StorageService getStorage() {
        return this.writer.getStorage();
    }

    @Override
    public R save(OutputStream outputStream) {
        return this.writer.save(outputStream);
    }

    public WorkbookWriter<T, R> getWriter() {
        return writer;
    }
}
