/*package com.example.utils.excel.sheet.write1;

import com.example.utils.excel.exception.PoiExcelTypeException;
import com.example.utils.excel.option.PoiOptions;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

*//**
 * @author qianliao.zhuang
 *//*
public class WorkbookStreamWriteSheet1<T> extends WorkbookWriteSheet1<T> {

    private static final int DEFAULT_WINDOW_SIZE = 100;

    private int rowAccessWindowSize = DEFAULT_WINDOW_SIZE;

    public WorkbookStreamWriteSheet1(AbstractWorkbookWriter1<T> writer, PoiOptions options) {
        super(writer, options);
    }

    public int getRowAccessWindowSize() {
        return rowAccessWindowSize;
    }

    public void setRowAccessWindowSize(int rowAccessWindowSize) {
        this.rowAccessWindowSize = rowAccessWindowSize;
    }

    @Override
    protected Workbook createWorkbook() {
        if (this.writer.getWriteSheet().getWorkbook() == null) {
            return new SXSSFWorkbook(DEFAULT_WINDOW_SIZE);
        }
        if (!(this.writer.getWriteSheet().getWorkbook() instanceof XSSFWorkbook)) {
            throw new PoiExcelTypeException("Streaming can not supported .xlx");
        }
        return new SXSSFWorkbook((XSSFWorkbook) this.workbook, DEFAULT_WINDOW_SIZE);
    }
}*/
