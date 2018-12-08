/*package com.example.utils.excel.sheet.write1;

import com.example.utils.CollectionUtil;
import com.example.utils.excel.enums.PoiCellStyle;
import com.example.utils.excel.handler.CellStyleHandler;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.parser.Parser;
import com.example.utils.excel.parser.Parsers;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

*//**
 * @author zhuangqianliao
 *//*
@Slf4j
public class WorkbookWriteSheet1<T> extends AbstractWorkbookSheet<T> {

    private static final DataFormatter STRING_FORMATTER = new DataFormatter();
    protected final AbstractWorkbookWriter1<T> writer;
    protected final PoiOptions options;
    protected CellStyleHandler<T> cellStyleHandler;

    public WorkbookWriteSheet1(AbstractWorkbookWriter1<T> writer, PoiOptions options) {
        this.writer = writer;
        this.options = options;
        this.workbook = createWorkbook();
        this.sheet = createSheet();
    }

    public AbstractWorkbookWriter1<T> getWriter() {
        return this.writer;
    }

    public PoiOptions getOptions() {
        return this.options;
    }

    public WorkbookWriteSheet1<T> setCellStyleHandler(CellStyleHandler<T> cellStyleHandler) {
        this.cellStyleHandler = cellStyleHandler;
        return this;
    }

    protected Workbook createWorkbook() {
        return this.writer.createWorkbook();
    }

    protected Sheet createSheet() {
        return this.workbook.createSheet();
    }

    *//**
     * 写入Excel
     * @param values: 输出到表格内容
     * @param clazz: 用来输出表头标题
     *//*
    protected void write(final Iterable<T> values, final Class<T> clazz) {
        if (!Iterables.isEmpty(values)) {
            writeHeader(clazz);
            long start1 = System.currentTimeMillis();
            writeContent(values);
            long end1 = System.currentTimeMillis();
            System.out.println("写入内容耗时: " + (end1 - start1));
        }
    }

    protected void writeHeader(final Class<T> clazz) {
        int skip = this.options.getSkip();
        createHeader(clazz, sheet.createRow(skip));
    }

    protected void writeContent(final Iterable<T> values) {
        int skip = this.options.getSkip();
        write(0, Iterables.size(values), skip + 1,
                values);
    }

    *//**
     * 写入Excel
     * @param start: 数据起始处
     * @param end: 数据终止点
     * @param startRow: 写入表格中行的起始点
     * @param values: 待写入的数据
     *//*
    void write(final int start, final int end,
               final int startRow,
               final Iterable<T> values) {
        checkArgument(start >= 0, "start must be negative");
        checkArgument(end >= start, "end must be glt start");

        int _startRow = startRow;
        Iterable<T> iterable = FluentIterable.from(values).skip(start).limit(end - start + 1);
        for (T value : iterable) {
            doConvert(value, sheet.createRow(_startRow));
            _startRow++;
        }
    }

    private void doConvert(T instance, Row row) {
        if (instance instanceof Row) {
            for (Cell cell : (Row) instance) {
                Cell c = row.createCell(cell.getColumnIndex());
                c.setCellValue(STRING_FORMATTER.formatCellValue(cell));
            }
            return;
        }

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) instance.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (ignoreField(field.getName(), options.getIgnoreFields())) {
                continue;
            }
            Mapper<T> mapper = Mappers.getMapper(field.getName(), type);
            if (mapper == null) {
                log.warn("can not find suitable mapper for field: {}.", field.getName());
                continue;
            }

            Cell cell = row.createCell(mapper.getColumnIndex());
            String cellValue = getFromInstance(instance, mapper);
            Map<String, Object> properties = getCellStyle(instance, mapper, cell, cellValue);
            cell.setCellValue(cellValue);
            CellUtil.setCellStyleProperties(cell, properties);
        }
    }

    private String getFromInstance(T instance, Mapper<?> mapper) {
        Object returnValue = doInvoke(instance.getClass(), mapper.getReadMethodName(), mapper.getReadMethodType(),
                instance, null);

        Parser<?> parser = Parsers.getOrDefault(
                mapper.getReadMethodType().returnType(),
                Parsers.defaultParser()
        );

        return parser.deParse(returnValue);
    }

    private void createHeader(final Class<T> clazz, final Row row) {
        for (Field field : clazz.getDeclaredFields()) {
            if (ignoreField(field.getName(), options.getIgnoreFields())) {
                continue;
            }

            Mapper<T> mapper = Mappers.getMapper(field.getName(), clazz);
            if (mapper == null) {
                log.warn("can not find suitable mapper for field: {}.", field.getName());
                continue;
            }
            autoColumnWidth(mapper);
            Cell cell = row.createCell(mapper.getColumnIndex());
            cell.setCellValue(mapper.getColumnName());
        }
    }

    *//**
     * 标题自适应宽度
     *//*
    private void autoColumnWidth(Mapper mapper) {
        sheet.setColumnWidth(mapper.getColumnIndex(), mapper.getColumnName().getBytes().length * 256);
    }

    private Map<String, Object> getCellStyle(T rowData, Mapper<T> mapper, Cell cell, String cellValue) {
        Map<String, Object> properties = new HashMap<>(16);
        List<PoiCellStyle> poiCellStyles = null;
        if (this.cellStyleHandler != null) {
            poiCellStyles = this.cellStyleHandler.getCellStyle(rowData, mapper, cellValue);
        }
        if (CollectionUtil.isEmpty(poiCellStyles)) {
            poiCellStyles = mapper.getCellStyle();
        }
        if (CollectionUtil.isNotEmpty(poiCellStyles)) {
            poiCellStyles.forEach(x -> x.setCellStyle(cell, properties));
        }
        return properties;
    }
}*/
