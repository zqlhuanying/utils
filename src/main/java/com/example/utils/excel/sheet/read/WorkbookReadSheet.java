package com.example.utils.excel.sheet.read;

import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.parser.Parser;
import com.example.utils.excel.parser.Parsers;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookReadSheet<T> extends AbstractWorkbookSheet<T> implements ForkJoin<T> {

    final PoiOptions options;

    public WorkbookReadSheet(Workbook workbook, PoiOptions options) {
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(options.getSheetIndex());
        this.options = options;
    }

    protected List<T> read(Class<T> type) {
        if (this.getRows() <= 0) {
            return Collections.emptyList();
        }
        return read(options.getSkip(), sheet.getLastRowNum(), type);
    }

    @Override
    public List<T> read(final int startRow, final int endRow, final Class<T> type) {
        checkArgument(startRow >= 0, "start must be negative");
        checkArgument(endRow >= startRow, "end must be glt start");

        List<T> res = Lists.newArrayListWithCapacity(endRow - startRow);
        for (int i = startRow; i <= endRow; i++) {
            if (isValidRow(sheet.getRow(i))) {
                T row = doConvert(sheet.getRow(i), type);
                res.add(row);
            }
        }
        return res;
    }

    private T doConvert(Row row, Class<T> type) {
        T instance = newInstance(type);

        for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
            Cell cell = iterator.next();
            String cellValue = DATA_FORMATTER.formatCellValue(cell);
            Mapper<T> mapper = Mappers.getMapper(cell.getColumnIndex(), type);
            if (mapper == null) {
                log.warn("can not find suitable mapper for column: {}.", cell.getColumnIndex());
                continue;
            }
            if (ignoreField(mapper.getField(), options.getIgnoreFields())) {
                continue;
            }
            writeToInstance(instance, cellValue, mapper);
        }
        return instance;
    }

    private void writeToInstance(T instance, String cellValue, Mapper<?> mapper) {
        Parser<?> parser = Parsers.getOrDefault(
                mapper.getWriteMethodType().parameterType(0),
                Parsers.defaultParser()
        );

        doInvoke(instance.getClass(), mapper.getWriteMethodName(), mapper.getWriteMethodType(),
                instance, parser.parse(cellValue));
    }

    private boolean isValidRow(Row row) {
        return row != null;
    }
}
