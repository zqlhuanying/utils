package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.handler.CellStyleHandler;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.PoiFile;
import com.example.utils.excel.sheet.PoiOutputStream;
import com.example.utils.excel.sheet.Source;
import com.example.utils.excel.storage.LocalStorage;
import com.example.utils.excel.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookWriter<T, R> implements WorkbookWriter<T, R> {

    static final StorageService DEFAULT_STORAGE_SERVICE = new LocalStorage();

    protected WorkbookWriteSheet<T> writeSheet;
    private StorageService storageService;

    public AbstractWorkbookWriter(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public R write(final List<T> values, final Class<T> clazz) {
        OutputStream output = null;
        try (
                Workbook workbook = getWriteSheet().getWorkbook()
        ) {
            output = getOutputStream();
            getWriteSheet().write(values, clazz);
            workbook.write(output);
        } catch (IOException e) {
            log.error("can not auto-close workbook", e);
            return null;
        } catch (Exception e) {
            log.error("write values: {} failed!",
                    JSONObject.toJSONString(values), e);
            return null;
        }
        return save(output);
    }

    public WorkbookWriteSheet<T> getWriteSheet() {
        return this.writeSheet;
    }

    public AbstractWorkbookWriter<T, R> setWriteSheet(WorkbookWriteSheet<T> writeSheet) {
        this.writeSheet = writeSheet;
        return this;
    }

    public AbstractWorkbookWriter<T, R> setCellStyleHandler(CellStyleHandler<T> cellStyleHandler) {
        getWriteSheet().setCellStyleHandler(cellStyleHandler);
        return this;
    }

    public WorkbookSXSSFWriter<T, R> sxssfWriter() {
        return new WorkbookSXSSFWriter<>(this);
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            if (getSource() instanceof PoiOutputStream) {
                return ((PoiOutputStream) getSource()).get();
            }
            if (getSource() instanceof PoiFile) {
                return new FileOutputStream(((PoiFile) getSource()).get());
            }
            throw new PoiException("Source type is not be supported");
        } catch (IOException e) {
            log.error("create output stream failed.", e);
            throw new PoiException("create output stream failed");
        }
    }

    @Override
    public StorageService getStorage() {
        return this.storageService;
    }

    @Override
    public Source<?> getSource() {
        return getWriteSheet().getSource();
    }

    @Override
    public PoiOptions getOptions() {
        return getWriteSheet().getOptions();
    }
}
