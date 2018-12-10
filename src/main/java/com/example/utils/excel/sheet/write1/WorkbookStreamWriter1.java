package com.example.utils.excel.sheet.write1;

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
public class WorkbookStreamWriter1<T> extends AbstractWorkbookWriter1<T, OutputStream> {

    private final PoiOutputStream<OutputStream> outputStream;

    public WorkbookStreamWriter1(OutputStream outputStream, PoiExcelType excelType) {
        this(outputStream, excelType, PoiOptions.settings().setSkip(0).build(), DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookStreamWriter1(OutputStream outputStream, PoiExcelType excelType,
                                 PoiOptions options) {
        this(outputStream, excelType, options, DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookStreamWriter1(OutputStream outputStream, PoiExcelType excelType,
                                 PoiOptions options, StorageService storageService) {
        super(storageService);
        this.outputStream = new PoiOutputStream<>(outputStream, excelType);
        this.writeSheet = new WorkbookWriteSheet1<>(this.outputStream, options);
    }

    @Override
    public OutputStream save(OutputStream outputStream) {
        return outputStream;
    }
}
