package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.DateUtils;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.parser.Parser;
import com.example.utils.excel.parser.Parsers;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class WorkbookWriteSheet<T> extends AbstractWorkbookSheet<T> {

    private static final DataFormatter STRING_FORMATTER = new DataFormatter();
    final AbstractWorkbookWriter<T> writer;
    final PoiOptions options;

    public WorkbookWriteSheet(AbstractWorkbookWriter<T> writer, Sheet sheet, PoiOptions options) {
        this.writer = writer;
        this.sheet = sheet;
        this.options = options;
    }

    protected WorkbookWriteSheet(Sheet sheet, PoiOptions options) {
        this(null, sheet, options);
    }

    /**
     * 写入Excel
     * @param values: 输出到表格内容
     * @param clazz: 用来输出表头标题
     */
    protected void write(final Iterable<T> values, final Class clazz) {
        if (!Iterables.isEmpty(values)) {
            long start = System.currentTimeMillis();
            allocate(Iterables.size(values) + 1);
            long end = System.currentTimeMillis();
            System.out.println("预先分配耗时: " + (end - start));
            writeHeader(clazz);
            long start1 = System.currentTimeMillis();
            writeContent(values);
            long end1 = System.currentTimeMillis();
            System.out.println("写入内容耗时: " + (end1 - start1));
        }
    }

    protected void writeHeader(final Class clazz) {
        int skip = this.options.getSkip();
        createHeader(clazz, sheet.getRow(skip));
    }

    protected void writeContent(final Iterable<T> values) {
        int skip = this.options.getSkip();
        write(0, Iterables.size(values), skip + 1,
                values);
    }

    public WorkbookWriteSheet<T> bigSheet() {
        return new WorkbookBigWriteSheet<>(this.writer, this.sheet, this.options);
    }

    public AbstractWorkbookWriter<T> getWriter() {
        this.writer.setSheet(this);
        return this.writer;
    }

    /**
     * 写入Excel
     * @param start: 数据起始处
     * @param end: 数据终止点
     * @param startRow: 写入表格中行的起始点
     * @param values: 待写入的数据
     */
    void write(final int start, final int end,
               final int startRow,
               final Iterable<T> values) {
        checkArgument(start >= 0, "start must be negative");
        checkArgument(end >= start, "end must be glt start");

        int _startRow = startRow;
        Iterable<T> iterable = FluentIterable.from(values).skip(start).limit(end - start + 1);
        long start1 = System.currentTimeMillis();
        for (T value : iterable) {
            doConvert(value, sheet.getRow(_startRow));
            _startRow++;
        }
        long end1 = System.currentTimeMillis();
        //System.out.println("start: " + start + " end: " + end + "耗时：" + (end1 - start1));
    }

    private void allocate(int size) {
        // 预先分配空间
        // createRow 不是线程安全的
        int skip = this.options.getSkip();
        for (int i = 0; i < size; i++) {
            this.sheet.createRow(i + skip);
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

        Class<?> type = instance.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (ignoreField(field.getName(), options.getIgnoreFields())) {
                continue;
            }
            Mapper<?> mapper = Mappers.getMapper(field.getName(), type);
            if (mapper == null) {
                log.warn("can not find suitable mapper for field: {}.", field.getName());
                continue;
            }
            long start = System.currentTimeMillis();
            String cellValue = getFromInstance(instance, mapper);
            long end = System.currentTimeMillis();
            if (end - start > 10) {
                System.out.println("反射耗时: " + (end - start));
            }

            long start1 = System.currentTimeMillis();
            Cell cell = row.createCell(mapper.getColumnIndex());
            long end1 = System.currentTimeMillis();
            if (end1 - start1 > 20) {
                System.out.println(JSONObject.toJSONString(mapper));
                System.out.println(DateUtils.now() + "单元格耗时: " + (end1 - start1));
            }
            cell.setCellValue(cellValue);
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

    private void createHeader(final Class<?> clazz, final Row row) {
        for (Field field : clazz.getDeclaredFields()) {
            if (ignoreField(field.getName(), options.getIgnoreFields())) {
                continue;
            }

            Mapper<?> mapper = Mappers.getMapper(field.getName(), clazz);
            if (mapper == null) {
                log.warn("can not find suitable mapper for field: {}.", field.getName());
                continue;
            }
            autoColumnWidth(mapper);
            Cell cell = row.createCell(mapper.getColumnIndex());
            cell.setCellValue(mapper.getColumnName());
        }
    }

    /**
     * 标题自适应宽度
     */
    private void autoColumnWidth(Mapper mapper) {
        sheet.setColumnWidth(mapper.getColumnIndex(), mapper.getColumnName().getBytes().length * 256);
    }
}
