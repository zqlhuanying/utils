package com.example.utils.excel;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.read.AbstractWorkbookReader;
import com.example.utils.excel.sheet.read.WorkbookFileReader;
import com.example.utils.excel.sheet.read.WorkbookStreamReader;
/*import com.example.utils.excel.sheet.write.AbstractWorkbookWriter;
import com.example.utils.excel.sheet.write.WorkbookFileWriter;
import com.example.utils.excel.storage.StorageService;*/
import com.example.utils.excel.sheet.write1.AbstractWorkbookWriter1;
import com.example.utils.excel.sheet.write1.WorkbookFileWriter1;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public final class POI {

    public static <T> AbstractWorkbookReader<T> fromExcel(final File file) {
        return new WorkbookFileReader<>(file);
    }

    public static <T> AbstractWorkbookReader<T> fromExcel(final File file, final PoiOptions options) {
        return new WorkbookFileReader<>(file, options);
    }

    public static <T> AbstractWorkbookReader<T> fromExcel(final InputStream inputStream, PoiExcelType excelType) {
        return new WorkbookStreamReader<>(inputStream, excelType);
    }

    public static <T> AbstractWorkbookReader<T> fromExcel(final InputStream inputStream, PoiExcelType excelType, final PoiOptions options) {
        return new WorkbookStreamReader<>(inputStream, excelType, options);
    }

    public static <T> AbstractWorkbookWriter1<T, String> writeExcel(final File file) {
        return new WorkbookFileWriter1<>(file);
    }

    public static <T> AbstractWorkbookWriter1<T, String> writeExcel(final File file, PoiOptions options) {
        return new WorkbookFileWriter1<>(file, options);
    }

    public static <T> AbstractWorkbookWriter1<T, String> writeExcel(final File file, PoiOptions options, final StorageService storageService) {
        return new WorkbookFileWriter1<>(file, options, storageService);
    }
}
