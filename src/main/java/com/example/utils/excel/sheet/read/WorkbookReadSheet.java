package com.example.utils.excel.sheet.read;

import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.BeanUtils;
import com.example.utils.excel.sheet.Source;
import com.example.utils.excel.sheet.WorkbookHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookReadSheet<T> extends AbstractWorkbookSheet<T> {

    private volatile Workbook workbook;
    private volatile Sheet sheet;

    public WorkbookReadSheet(Source<?> source, PoiOptions options) {
        this.source = source;
        this.options = options;
    }

    protected List<T> read(Class<T> type) {
        if (getRows() <= 0) {
            return Collections.emptyList();
        }
        return read(options.getSkip(), getRows(), type);
    }

    protected List<T> read(final int startRow, final int endRow, final Class<T> type) {
        checkArgument(startRow >= 0, "start must be negative");
        checkArgument(endRow >= startRow, "end must be glt start");

        List<T> res = Lists.newArrayListWithCapacity(endRow - startRow);
        for (int i = startRow; i < endRow; i++) {
            if (isValidRow(getSheet().getRow(i))) {
                T row = doConvert(getSheet().getRow(i), type);
                res.add(row);
            }
        }
        return res;
    }

    public Workbook getWorkbook() {
        if (this.workbook == null) {
            synchronized (this) {
                if (this.workbook == null) {
                    this.workbook = WorkbookHelper.createWorkbook(this.source);
                }
            }
        }
        return this.workbook;
    }

    public Sheet getSheet() {
        this.sheet = getWorkbook().getSheetAt(this.options.getSheetIndex());
        return this.sheet;
    }

    @Override
    public int getRows() {
        return getSheet() == null ? 0 : getSheet().getLastRowNum() + 1;
    }

    private T doConvert(Row row, Class<T> type) {
        T instance = BeanUtils.newInstance(type);

        for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
            Cell cell = iterator.next();
            String cellValue = DATA_FORMATTER.formatCellValue(cell);
            Mapper<T> mapper = Mappers.getMapper(cell.getColumnIndex(), type);
            if (mapper == null) {
                log.warn("can not find suitable mapper for column: {}.", cell.getColumnIndex());
                continue;
            }
            writeToInstance(mapper, cellValue, instance);
        }
        return instance;
    }

    private boolean isValidRow(Row row) {
        return row != null;
    }
}
