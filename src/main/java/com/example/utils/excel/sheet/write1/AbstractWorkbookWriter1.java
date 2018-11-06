package com.example.utils.excel.sheet.write1;

import com.alibaba.fastjson.JSON;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.storage.LocalStorage;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookWriter1<T> implements WorkbookWriter<T> {

    protected static final StorageService DEFAULT_STORAGE_SERVICE = new LocalStorage();

    protected final PoiOptions options;
    protected final StorageService storageService;

    private WorkbookWriteSheet1<T> writeSheet;

    public AbstractWorkbookWriter1(PoiOptions options, StorageService storageService) {
        this.options = options;
        this.storageService = storageService;
    }

    @Override
    public String write(final List<T> values, final Class clazz) {
        try (
                Workbook workbook = getWriteSheet().getWorkbook();
                OutputStream output = getOutputStream()
        ) {
            getWriteSheet().write(values, clazz);
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

    public WorkbookWriteSheet1<T> getWriteSheet() {
        if (this.writeSheet == null) {
            this.writeSheet = defaultWriteSheet();
        }
        return this.writeSheet;
    }

    public AbstractWorkbookWriter1<T> setSheet(WorkbookWriteSheet1<T> writeSheet) {
        this.writeSheet = writeSheet;
        return this;
    }

    public WorkbookSXSSFWriter<T> sxssfWriter() {
        this.setSheet(new WorkbookStreamWriteSheet1<>(this, this.options));
        return new WorkbookSXSSFWriter<>(this);
    }

    protected abstract Workbook createWorkbook();

    protected abstract OutputStream getOutputStream();

    protected abstract String doSave(StorageService storageService);

    private WorkbookWriteSheet1<T> defaultWriteSheet() {
        return new WorkbookWriteSheet1<>(this, this.options);
    }
}
