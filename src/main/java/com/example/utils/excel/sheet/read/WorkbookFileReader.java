package com.example.utils.excel.sheet.read;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.WorkbookHelper;
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
        this.readSheet = new WorkbookReadSheet<>(createWorkbook(), this.options);
    }

    private Workbook createWorkbook() {
        return WorkbookHelper.createWorkbook(file);
    }

    private void check(File file) {
        if (!file.exists()) {
            throw new PoiException(String.format("file[%s] not exists", file.getName()));
        }

        String extension = FilenameUtils.getExtension(file.getName());
        PoiExcelType.from(extension);
    }
}
