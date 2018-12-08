package com.example.utils.excel.sheet;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiException;
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

    public static Workbook createWorkbook(Source<?> source) {
        if (source instanceof PoiFile) {
            return createWorkbook((PoiFile<?>) source);
        }
        if (source instanceof PoiInputStream) {
            return createWorkbook((PoiInputStream<?>) source);
        }
        throw new PoiException("source is neither PoiFile nor PoiInputStream type");
    }

    public static Workbook createWorkbook(PoiFile<? extends File> file) {
        return PoiExcelType.from(file.extension()).createWorkbook(file);
    }

    public static Workbook createWorkbook(PoiInputStream<? extends InputStream> inputStream) {
        return inputStream.type().createWorkbook(inputStream);
    }
}
