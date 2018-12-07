package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import com.google.common.collect.FluentIterable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookReader<T>
        implements WorkbookReader<T>, ForkJoin<T, Row> {

    @Getter
    protected final PoiOptions options;

    protected WorkbookReadSheet<T> readSheet;

    public AbstractWorkbookReader(PoiOptions options) {
        this.options = options;
    }

    @Override
    public List<T> read(Class<T> type) {
        try (Workbook workbook = getReadSheet().getWorkbook()) {
            return getReadSheet().read(type);
        } catch (IOException e) {
            log.error("can not auto-close workbook", e);
        } catch (Exception e) {
            log.error("read file failed!", e);
        }
        return Collections.emptyList();
    }

    public <R> WorkbookBigReader<T, R> bigReader() {
        return new WorkbookBigReader<>(this);
    }

    @Override
    public List<T> read(int start, int end, Class<T> type) {
        return getReadSheet().read(start, end, type);
    }

    @Override
    public List<Row> errors(int start, int end) {
        return FluentIterable.from(getReadSheet().getSheet())
                .skip(start)
                .limit(end - start + 1)
                .toList();
    }

    @Override
    public WorkbookReadSheet<T> getReadSheet() {
        return readSheet;
    }

    public void setReadSheet(WorkbookReadSheet<T> readSheet) {
        this.readSheet = readSheet;
    }
}
