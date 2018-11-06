package com.example.utils.excel.sheet.write1;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookSXSSFWriter<T> implements WorkbookWriter<T> {

    private AbstractWorkbookWriter1<T> writer;

    public WorkbookSXSSFWriter(WorkbookWriter<T> writer) {
        this.writer = (AbstractWorkbookWriter1<T>) writer;
    }

    public WorkbookSXSSFWriter<T> setRowAccessWindowSize(int rowAccessWindowSize) {
        ((WorkbookStreamWriteSheet1<T>) this.writer.getWriteSheet())
                .setRowAccessWindowSize(rowAccessWindowSize);
        return this;
    }

    @Override
    public String write(List<T> values, Class clazz) {
        return this.writer.write(values, clazz);
    }
}
