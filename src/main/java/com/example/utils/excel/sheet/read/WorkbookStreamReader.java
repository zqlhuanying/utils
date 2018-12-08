package com.example.utils.excel.sheet.read;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.PoiInputStream;
import com.example.utils.excel.sheet.WorkbookHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookStreamReader<T> extends AbstractWorkbookReader<T> {

    private final PoiInputStream<InputStream> inputStream;

    public WorkbookStreamReader(InputStream inputStream, PoiExcelType excelType) {
        this(inputStream, excelType, PoiOptions.settings().build());
    }

    public WorkbookStreamReader(InputStream inputStream, PoiExcelType excelType, PoiOptions options) {
        super(options);
        this.inputStream = new PoiInputStream<>(inputStream, excelType);
        this.readSheet = new WorkbookReadSheet<>(createWorkbook(), this.options);
    }

    private Workbook createWorkbook() {
        return WorkbookHelper.createWorkbook(inputStream);
    }
}
