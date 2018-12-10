package com.example.utils.excel.sheet.write1;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.PoiFile;
import com.example.utils.excel.storage.LocalStorage;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookFileWriter1<T> extends AbstractWorkbookWriter1<T, String> {

    private final PoiFile<File> file;

    public WorkbookFileWriter1(File file) {
        this(file, PoiOptions.settings().setSkip(0).build(), DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookFileWriter1(File file, PoiOptions options) {
        this(file, options, DEFAULT_STORAGE_SERVICE);
    }

    public WorkbookFileWriter1(File file, PoiOptions options, StorageService storageService) {
        super(storageService);
        this.file = new PoiFile<>(file);
        check(this.file);

        this.writeSheet = new WorkbookWriteSheet1<>(this.file, options);
    }

    @Override
    public String save(OutputStream outputStream) {
        try {
            return getStorage().store(this.file.get());
        } finally {
            try {
                if (!(getStorage() instanceof LocalStorage)) {
                    FileUtils.forceDelete(this.file.get());
                }
            } catch (IOException e) {
                log.error("delete file: {} failed!", this.file.get().getAbsolutePath());
            }
            IOUtils.closeQuietly(outputStream);
        }
    }

    private void check(PoiFile<File> file) {
        PoiExcelType.from(file.extension());
    }
}
