package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookReader<T> {

    protected final PoiOptions options;

    private WorkbookReadSheet<T> readSheet;
    private Workbook workbook;

    public AbstractWorkbookReader(PoiOptions options) {
        this.options = options;
    }

    /**
     * 仅支持读取小文件
     */
    public List<T> read(Class<T> type) {
        try (Workbook workbook = getWorkbook()) {
            return getSheet().read(type);
        } catch (IOException e) {
            log.error("can not auto-close workbook", e);
        } catch (Exception e) {
            log.error("read file failed!", e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取 ReadSheet
     * 此时可以支持大文件的读取
     */
    public WorkbookReadSheet<T> getSheet() {
        if (this.readSheet == null) {
            this.readSheet = createReadSheet();
        }
        return this.readSheet;
    }

    protected void setSheet(WorkbookReadSheet<T> readSheet) {
        this.readSheet = readSheet;
    }

    protected Workbook getWorkbook() {
        if (this.workbook == null) {
            this.workbook = createWorkbook();
        }
        return this.workbook;
    }

    private WorkbookReadSheet<T> createReadSheet() {
        return new WorkbookReadSheet<>(this, getWorkbook().getSheetAt(options.getSheetIndex()), options);
    }

    protected abstract Workbook createWorkbook();
}
