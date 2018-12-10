package com.example.utils.excel.sheet.read;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiExcelTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.opc.OPCPackage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookEventReader<T> extends FilterWorkbookReader<T>
        implements ForkJoin<T, Object> {

    private WorkbookEventSheet<T> eventSheet;

    public WorkbookEventReader(WorkbookReader<T> reader) {
        super(reader);

        if (PoiExcelType.XLSX != getReader().getSource().type()) {
            throw new PoiExcelTypeException("Event Reader can just be supported .xlsx");
        }

        this.eventSheet = new WorkbookEventSheet<>(getSource(), getOptions());
    }

    @Override
    public List<T> read(Class<T> type) {
        try (OPCPackage pkg = this.eventSheet.getOPCPackage()){
            return this.eventSheet.read(type);
        } catch (IOException e) {
            log.error("can not auto-close OPCPackage", e);
        } catch (Exception e) {
            log.error("read file failed", e);
        }
        return Collections.emptyList();
    }

    public WorkbookBigReader<T, Object> bigReader() {
        return new WorkbookBigReader<>(this);
    }

    @Override
    public List<T> read(int start, int end, Class<T> type) {
        return this.eventSheet.read(start, end, type);
    }

    /**
     * Assume event mode can not happen this situation
     * // todo
     */
    @Override
    public List<Object> errors(int start, int end) {
        return Collections.emptyList();
    }

    @Override
    public void release() throws IOException {
        this.eventSheet.getOPCPackage().close();
    }

    @Override
    public int getRows() {
        return this.eventSheet.getRows();
    }
}
