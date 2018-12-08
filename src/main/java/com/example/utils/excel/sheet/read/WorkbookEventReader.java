package com.example.utils.excel.sheet.read;

import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiExcelTypeException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookEventReader<T> extends FilterWorkbookReader<T> {

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
        // todo 资源释放
        return this.eventSheet.read(type);
    }

    @Override
    public int getRows() {
        return this.eventSheet.getRows();
    }
}
