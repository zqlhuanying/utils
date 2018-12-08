package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import lombok.Getter;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public class FilterWorkbookReader<T> implements WorkbookReader<T> {

    @Getter
    protected WorkbookReader<T> reader;

    public FilterWorkbookReader(WorkbookReader<T> reader) {
        this.reader = reader;
    }

    @Override
    public List<T> read(Class<T> type) {
        return this.reader.read(type);
    }

    @Override
    public WorkbookReadSheet<T> getReadSheet() {
        return this.reader.getReadSheet();
    }

    @Override
    public PoiOptions getOptions() {
        return this.reader.getOptions();
    }
}
