package com.example.utils.excel.sheet.write1;

import com.example.utils.CollectionUtil;
import com.example.utils.excel.enums.PoiCellStyle;
import com.example.utils.excel.handler.CellStyleHandler;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.Source;
import com.example.utils.excel.sheet.WorkbookHelper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookWriteSheet1<T> extends AbstractWorkbookSheet<T> {

    protected volatile Workbook workbook;
    protected volatile Sheet sheet;
    private CellStyleHandler<T> cellStyleHandler;

    public WorkbookWriteSheet1(Source<?> source, PoiOptions options) {
        this.source = source;
        this.options = options;
    }

    public WorkbookWriteSheet1<T> setCellStyleHandler(CellStyleHandler<T> cellStyleHandler) {
        this.cellStyleHandler = cellStyleHandler;
        return this;
    }

    public Workbook getWorkbook() {
        if (this.workbook == null) {
            synchronized (this) {
                if (this.workbook == null) {
                    this.workbook = WorkbookHelper.createWorkbook(this.source.type());
                }
            }
        }
        return this.workbook;
    }

    public Sheet getSheet() {
        if (this.sheet == null) {
            synchronized (this) {
                if (this.sheet == null) {
                    this.sheet = getWorkbook().createSheet();
                }
            }
        }
        return this.sheet;
    }

    /**
     * 写入Excel
     * @param values: 输出到表格内容
     * @param clazz: 用来输出表头标题
     */
    protected void write(final Iterable<T> values, final Class<T> clazz) {
        if (!Iterables.isEmpty(values)) {
            writeHeader(clazz);
            writeContent(values);
        }
    }

    @Override
    public int getRows() {
        return 0;
    }

    protected void writeHeader(final Class<T> clazz) {
        int skip = this.options.getSkip();
        createHeader(clazz, getSheet().createRow(skip));
    }

    protected void writeContent(final Iterable<T> values) {
        int skip = this.options.getSkip() + 1;
        write(0, Iterables.size(values), skip,
                values);
    }

    /**
     * 写入Excel
     * @param start: 数据起始处
     * @param end: 数据终止点(exclude)
     * @param startRow: 写入表格中行的起始点
     * @param values: 待写入的数据
     */
    void write(final int start, final int end,
               final int startRow,
               final Iterable<T> values) {
        checkArgument(start >= 0, "start must be negative");
        checkArgument(end >= start, "end must be glt start");

        int _startRow = startRow;
        Iterable<T> iterable = FluentIterable.from(values).skip(start).limit(end - start);
        for (T value : iterable) {
            doConvert(value, getSheet().createRow(_startRow));
            _startRow++;
        }
    }

    private void doConvert(T instance, Row row) {
        if (instance instanceof Row) {
            for (Cell cell : (Row) instance) {
                CellUtil.createCell(row, cell.getColumnIndex(), DATA_FORMATTER.formatCellValue(cell));
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
            String cellValue = getFromInstance(mapper, instance);
            cell.setCellValue(cellValue);
            Map<String, Object> properties = getCellStyle(instance, mapper, cell, cellValue);
            CellUtil.setCellStyleProperties(cell, properties);
        }
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
            CellUtil.createCell(row, mapper.getColumnIndex(), mapper.getColumnName());
        }
    }

    /**
     * 标题自适应宽度
     */
    private void autoColumnWidth(Mapper mapper) {
        getSheet().setColumnWidth(mapper.getColumnIndex(), mapper.getColumnName().getBytes().length * 256);
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
}
