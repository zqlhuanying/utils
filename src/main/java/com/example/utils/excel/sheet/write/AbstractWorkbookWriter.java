package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSON;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.storage.LocalStorage;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookWriter<T> {

    protected static final StorageService DEFAULT_STORAGE_SERVICE = new LocalStorage();

    protected final PoiOptions options;
    protected final StorageService storageService;

    private WorkbookWriteSheet<T> writeSheet;
    private Workbook workbook;

    public AbstractWorkbookWriter(PoiOptions options, StorageService storageService) {
        this.options = options;
        this.storageService = storageService;
    }

    public String write(final List<T> values, final Class clazz) {
        try (
                Workbook workbook = getWorkbook();
                OutputStream output = getOutputStream()
        ) {
            getSheet().write(values, clazz);
            workbook.write(output);
        } catch (IOException e) {
            log.error("can not auto-close workbook", e);
            return null;
        } catch (Exception e) {
            log.error("write values: {} failed!",
                    JSON.toJSONString(values), e);
            return null;
        }
        return doSave(storageService);
    }

    public WorkbookWriteSheet<T> getSheet() {
        if (this.writeSheet == null) {
            this.writeSheet = createWriteSheet();
        }
        return this.writeSheet;
    }

    protected Workbook getWorkbook() {
        if (this.workbook == null) {
            this.workbook = createWorkbook();
        }
        return this.workbook;
    }

    protected void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    protected void setSheet(WorkbookWriteSheet<T> writeSheet) {
        this.writeSheet = writeSheet;
    }

    protected abstract Workbook createWorkbook();

    protected abstract OutputStream getOutputStream();

    protected abstract String doSave(StorageService storageService);

    private WorkbookWriteSheet<T> createWriteSheet() {
        return new WorkbookWriteSheet<>(this, getWorkbook(), getWorkbook().createSheet(), this.options);
    }
}
