package com.example.utils.excel.sheet.read;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.PoiFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookFileReader<T> extends AbstractWorkbookReader<T> {

    private final PoiFile<File> file;

    public WorkbookFileReader(File file) {
        this(file, PoiOptions.settings().build());
    }

    public WorkbookFileReader(File file, PoiOptions options) {
        this.file = new PoiFile<>(file);
        check();
        this.readSheet = new WorkbookReadSheet<>(this.file, options);
    }

    private void check() {
        if (!this.file.get().exists()) {
            throw new PoiException(String.format("file[%s] not exists", this.file.name()));
        }

        PoiExcelType.from(this.file.extension());
    }
}
