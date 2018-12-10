package com.example.utils.excel.sheet.read;

import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.Source;
import com.google.common.collect.FluentIterable;
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

    protected WorkbookReadSheet<T> readSheet;

    /**
     * 资源在读取完毕后，会及时释放
     * @param type the given type
     */
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

    public WorkbookEventReader<T> eventReader() {
        return new WorkbookEventReader<>(this);
    }

    /**
     * 由于是片段式读取，所以资源的释放需要由调用方来维护
     */
    @Override
    public List<T> read(int start, int end, Class<T> type) {
        return getReadSheet().read(start, end, type);
    }

    @Override
    public List<Row> errors(int start, int end) {
        return FluentIterable.from(getReadSheet().getSheet())
                .skip(start)
                .limit(end - start)
                .toList();
    }

    @Override
    public void release() throws IOException {
        getReadSheet().getWorkbook().close();
    }

    public WorkbookReadSheet<T> getReadSheet() {
        return readSheet;
    }

    public void setReadSheet(WorkbookReadSheet<T> readSheet) {
        this.readSheet = readSheet;
    }

    @Override
    public Source<?> getSource() {
        return getReadSheet().getSource();
    }

    @Override
    public PoiOptions getOptions() {
        return getReadSheet().getOptions();
    }

    @Override
    public int getRows() {
        return getReadSheet().getRows();
    }
}
