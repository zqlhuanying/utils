package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public final class WorkbookHelper {

    public static Workbook createWorkbook(PoiExcelType excelType) {
        return excelType.createWorkbook();
    }

    public static Workbook createWorkbook(PoiFile<? extends File> file) {
        return PoiExcelType.from(file.extension()).createWorkbook(file);
    }

    public static Workbook createWorkbook(PoiInputStream<? extends InputStream> inputStream) {
        return inputStream.type().createWorkbook(inputStream);
    }
}
