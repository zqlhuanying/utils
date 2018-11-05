package com.example.utils.excel.sheet.read;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public class WorkbookNullSheet<T> extends WorkbookReadSheet<T> {

    public WorkbookNullSheet(Sheet sheet, PoiOptions options) {
        super();
    }

    @Override
    public List<T> read(Class<T> type) {
        return Collections.emptyList();
    }

    @Override
    public WorkbookReadSheet<T> bigSheet() {
        return new WorkbookNullSheet<>(null, null);
    }

    @Override
    public WorkbookReadSheet<T> bigSheet(ResultAdvice<T> advice) {
        return new WorkbookNullSheet<>(null, null);
    }

    @Override
    public WorkbookReadSheet<T> bigSheet(ErrorHandler errorHandler) {
        return new WorkbookNullSheet<>(null, null);
    }
}
