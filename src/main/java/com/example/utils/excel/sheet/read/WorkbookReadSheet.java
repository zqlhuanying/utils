package com.example.utils.excel.sheet.read;

import com.example.utils.excel.handler.ErrorHandler;
import com.example.utils.excel.handler.ResultAdvice;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.parser.Parser;
import com.example.utils.excel.parser.Parsers;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookReadSheet<T> extends AbstractWorkbookSheet<T> {

    private static final DataFormatter STRING_FORMATTER = new DataFormatter();
    final PoiOptions options;
    final AbstractWorkbookReader<T> reader;

    public WorkbookReadSheet(AbstractWorkbookReader<T> reader, Sheet sheet, PoiOptions options) {
        this.reader = reader;
        this.sheet = sheet;
        this.options = options;
        checkNotNull(this.sheet, "sheet must be not null");
    }

    /**
     * 不要在其他包中使用该构造器
     */
    protected WorkbookReadSheet() {
        this.sheet = null;
        this.options = null;
        this.reader = null;
    }

    protected WorkbookReadSheet(Sheet sheet, PoiOptions options) {
        this(null, sheet, options);
    }

    protected List<T> read(Class<T> type) {
        if (sheet.getLastRowNum() < 0) {
            return Collections.emptyList();
        }
        return read(options.getSkip(), sheet.getLastRowNum(), type);
    }

    List<T> read(final int startRow, final int endRow, final Class<T> type) {
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

    public WorkbookReadSheet<T> bigSheet() {
        if (this instanceof WorkbookBigSheet) {
            return this;
        } else {
            return new WorkbookBigSheet<>(reader, sheet, options);
        }
    }

    public WorkbookReadSheet<T> bigSheet(ResultAdvice<T> advice) {
        WorkbookBigSheet<T> sheet = (WorkbookBigSheet<T>) bigSheet();
        return sheet.setAdvice(advice);
    }

    public WorkbookReadSheet<T> bigSheet(ErrorHandler errorHandler) {
        WorkbookBigSheet<T> sheet = (WorkbookBigSheet<T>) bigSheet();
        return sheet.setErrorHandler(errorHandler);
    }

    public AbstractWorkbookReader<T> getReader() {
        this.reader.setSheet(this);
        return this.reader;
    }

    private T doConvert(Row row, Class<T> type) {
        T instance = newInstance(type);

        for (Iterator<Cell> iterator = row.cellIterator(); iterator.hasNext();) {
            Cell cell = iterator.next();
            String cellValue = STRING_FORMATTER.formatCellValue(cell);
            Mapper<?> mapper = Mappers.getMapper(cell.getColumnIndex(), type);
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
