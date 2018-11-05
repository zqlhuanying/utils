package com.example.utils.excel.sheet.write;

import com.example.utils.excel.exception.PoiExcelTypeException;
import com.example.utils.excel.option.PoiOptions;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author qianliao.zhuang
 */
public class WorkbookStreamWriteSheet<T> extends WorkbookWriteSheet<T> {

    public WorkbookStreamWriteSheet(AbstractWorkbookWriter<T> writer,
                                    Workbook workbook, Sheet sheet, PoiOptions options) {
        super(writer, workbook, sheet, options);
    }

    @Override
    public AbstractWorkbookWriter<T> getWriter() {
        if (!(this.workbook instanceof XSSFWorkbook)) {
            throw new PoiExcelTypeException("Streaming can not supported .xlx");
        }
        this.workbook = new SXSSFWorkbook((XSSFWorkbook) this.workbook);
        this.sheet = this.workbook.createSheet();
        this.writer.setWorkbook(this.workbook);
        this.writer.setSheet(this);
        return this.writer;
    }
}
