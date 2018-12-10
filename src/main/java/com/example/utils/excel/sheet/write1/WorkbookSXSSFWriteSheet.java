package com.example.utils.excel.sheet.write1;

import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author qianliao.zhuang
 */
public class WorkbookSXSSFWriteSheet<T> extends WorkbookWriteSheet1<T> {

    private static final int DEFAULT_WINDOW_SIZE = 100;

    private int rowAccessWindowSize = DEFAULT_WINDOW_SIZE;

    public WorkbookSXSSFWriteSheet(Source<?> source, PoiOptions options) {
        super(source, options);
    }

    public int getRowAccessWindowSize() {
        return rowAccessWindowSize;
    }

    public void setRowAccessWindowSize(int rowAccessWindowSize) {
        this.rowAccessWindowSize = rowAccessWindowSize;
    }

    @Override
    public Workbook getWorkbook() {
        return new SXSSFWorkbook((XSSFWorkbook) super.getWorkbook(), DEFAULT_WINDOW_SIZE);
    }
}
