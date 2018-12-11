package com.example.utils.excel.sheet.write;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.PoiOutputStream;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookStreamWriter<T> extends AbstractWorkbookWriter<T, OutputStream> {

    private final PoiOutputStream<OutputStream> outputStream;

    public WorkbookStreamWriter(OutputStream outputStream, PoiExcelType excelType) {
        this(outputStream, excelType, PoiOptions.settings().setSkip(0).build(), DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookStreamWriter(OutputStream outputStream, PoiExcelType excelType,
                                PoiOptions options) {
        this(outputStream, excelType, options, DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookStreamWriter(OutputStream outputStream, PoiExcelType excelType,
                                PoiOptions options, StorageService storageService) {
        super(storageService);
        this.outputStream = new PoiOutputStream<>(outputStream, excelType);
        this.writeSheet = new WorkbookWriteSheet<>(this.outputStream, options);
    }

    @Override
    public OutputStream save(OutputStream outputStream) {
        return outputStream;
    }
}
