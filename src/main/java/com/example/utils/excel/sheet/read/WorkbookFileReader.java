package com.example.utils.excel.sheet.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookFileReader<T> extends AbstractWorkbookReader<T> {

    private final File file;

    public WorkbookFileReader(File file) {
        this(file, PoiOptions.settings().build());
    }

    public WorkbookFileReader(File file, PoiOptions options) {
        super(options);
        this.file = file;
        check(this.file);
    }

    @Override
    protected Workbook createWorkbook() {
        return WorkbookHelper.createWorkbook(file);
    }

    private void check(File file) {
        if (!file.exists()) {
            throw new PoiException("file not exists");
        }

        String extension = FilenameUtils.getExtension(file.getName());
        PoiExcelType.from(extension);
    }
}
