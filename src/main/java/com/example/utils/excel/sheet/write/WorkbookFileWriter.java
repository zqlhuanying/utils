/*package com.example.utils.excel.sheet.write;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.WorkbookHelper;
import com.example.utils.excel.storage.LocalStorage;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

*//**
 * @author zhuangqianliao
 *//*
@Slf4j
public class WorkbookFileWriter<T> extends AbstractWorkbookWriter<T> {

    private final File file;

    public WorkbookFileWriter(File file) {
        this(file, PoiOptions.settings().setSkip(0).build(), DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookFileWriter(File file, PoiOptions options) {
        this(file, options, DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookFileWriter(File file, PoiOptions options, StorageService storageService) {
        super(options, storageService);
        this.file = file;
        check(this.file);
    }

    @Override
    protected Workbook createWorkbook() {
        String extension = FilenameUtils.getExtension(file.getName());
        return WorkbookHelper.createWorkbook(PoiExcelType.from(extension));
    }

    @Override
    protected OutputStream getOutputStream() {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            log.error("file: {} not found", file.getName());
            throw new PoiException("file not found");
        }
    }

    @Override
    protected String doSave(StorageService storageService) {
        try {
            return storageService.store(file);
        } finally {
            try {
                if (!(storageService instanceof LocalStorage)) {
                    FileUtils.forceDelete(file);
                }
            } catch (IOException e) {
                log.error("delete file: {} failed!", file.getAbsolutePath());
            }
        }
    }

    private void check(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        PoiExcelType.from(extension);
    }
}*/
