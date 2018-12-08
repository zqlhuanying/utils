package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;
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
    public Source<?> getSource() {
        return this.reader.getSource();
    }

    @Override
    public PoiOptions getOptions() {
        return this.reader.getOptions();
    }

    @Override
    public int getRows() {
        return this.reader.getRows();
    }
}
