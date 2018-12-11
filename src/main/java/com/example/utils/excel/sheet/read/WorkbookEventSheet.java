package com.example.utils.excel.sheet.read;

import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.BeanUtils;
import com.example.utils.excel.sheet.OPCPackageHelper;
import com.example.utils.excel.sheet.Source;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookEventSheet<T> extends AbstractWorkbookSheet<T> {

    private volatile OPCPackage pkg;
    /**
     * 标识当前 Sheet 是否已经解析完成
     */
    private volatile boolean completed = false;

    /**
     * 单元格值之间的分隔符
     */
    private final String separator = ">";
    private final Splitter splitter = Splitter.on(separator).omitEmptyStrings();
    /**
     * 当前 sheet 的结果缓存
     * rowNum -> String(columnIndex: columnValue${separator} columnIndexN: columnValue${separator})
     */
    private Map<Integer, String> resultCache;

    /**
     * 当前结果数的缓存
     */
    private Integer rows;

    public WorkbookEventSheet(Source<?> source, PoiOptions options) {
        this.source = source;
        this.options = options;
    }

    protected List<T> read(Class<T> type) {
        return doRead().values()
                .stream()
                .map(column -> convert(column, type))
                .collect(Collectors.toList());
    }

    protected List<T> read(int start, int end, Class<T> type) {
        Map<Integer, String> result = doRead();
        return IntStream.range(start, end)
                .mapToObj(result::get)
                .map(column -> convert(column, type))
                .collect(Collectors.toList());
    }

    public OPCPackage getOPCPackage() {
        if (pkg == null) {
            synchronized (this) {
                if (pkg == null) {
                    pkg = OPCPackageHelper.open(getSource());
                }
            }
        }
        return pkg;
    }

    @Override
    public int getRows() {
        load();
        return this.rows;
    }

    private Map<Integer, String> doRead() {
        load();
        return this.resultCache;
    }

    private void load() {
        if (!completed) {
            synchronized (this) {
                if (!completed) {
                    doLoad();
                    completed = true;
                }
            }
        }
    }

    private void doLoad() {
        InputStream sheetInputStream = null;
        try {
            XSSFReader reader = new XSSFReader(getOPCPackage());
            sheetInputStream = Iterators.get(reader.getSheetsData(), getOptions().getSheetIndex());

            processSheet(
                    reader.getStylesTable(),
                    new ReadOnlySharedStringsTable(getOPCPackage()),
                    sheetInputStream,
                    getOptions().getSkip(), -1);
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            log.error("read values from sheet failed by using event mode", e);
            throw new PoiException("parsing sheet failed", e);
        } finally {
            IOUtils.closeQuietly(sheetInputStream);
        }
    }

    private void processSheet(StylesTable stylesTable,
                                   ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                   InputStream sheetInputStream,
                                   int start, int end)
            throws ParserConfigurationException, SAXException, IOException {
        SheetContentHandler sheetContentHandler = new SheetContentHandler(start, end);
        ContentHandler contentHandler = new XSSFSheetXMLHandler(stylesTable, readOnlySharedStringsTable, sheetContentHandler, false);
        XMLReader parser = SAXHelper.newXMLReader();
        parser.setContentHandler(contentHandler);
        parser.parse(new InputSource(sheetInputStream));
        this.resultCache = sheetContentHandler.getResult();
        this.rows = sheetContentHandler.getTotal();
    }

    private T convert(String column, Class<T> type) {
        T instance = BeanUtils.newInstance(type);

        Iterable<String> cellIterable = splitter.split(column);
        for (Iterator<String> iterator = cellIterable.iterator(); iterator.hasNext();) {
            String cell = iterator.next();
            int index = cell.indexOf(":");
            int columnIndex = Integer.parseInt(cell.substring(0, index));
            String columnValue = StringEscapeUtils.unescapeXml(cell.substring(index + 1));
            Mapper<T> mapper = Mappers.getMapper(columnIndex, type);
            if (mapper == null) {
                log.warn("can not find suitable mapper for column: {}.", columnIndex);
                continue;
            }
            writeToInstance(mapper, columnValue, instance);
        }
        return instance;
    }

    /**
     * sheet content handler
     * Get content[start, end](exclude end) from sheet
     * if end = -1; then load all contents
     */
    private class SheetContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final int start;
        private final int end;
        private final Map<Integer, String> result;
        private final Range<Integer> range;
        private int total;
        private StringBuilder sb;

        SheetContentHandler(int start, int end) {
            this.start = start;
            this.end = end;
            this.result = Maps.newHashMapWithExpectedSize(128);
            if (this.end > 0 && this.end < this.start) {
                throw new IllegalArgumentException(
                        String.format("endIndex:%s must be glt startIndex: %s", this.end, this.start)
                );
            }
            this.range = Range.closedOpen(this.start, this.end < 0 ? Integer.MAX_VALUE : this.end);
            this.sb = new StringBuilder();
        }

        @Override
        public void startRow(int rowNum) {
            total = rowNum + 1;
        }

        @Override
        public void endRow(int rowNum) {
            if (this.sb.length() > 0) {
                result.put(rowNum, this.sb.toString());
                this.sb.setLength(0);
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            CellAddress cellAddress = new CellAddress(cellReference);
            if (inRange(cellAddress.getRow())) {
                int columnIndex = cellAddress.getColumn();
                this.sb.append(columnIndex);
                this.sb.append(":");
                this.sb.append(StringEscapeUtils.escapeXml11(formattedValue));
                this.sb.append(separator);
            }
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // nothing to do
        }

        boolean inRange(int rowNum) {
            return this.range.contains(rowNum);
        }

        Map<Integer, String> getResult() {
            return result;
        }

        int getTotal() {
            return total;
        }
    }
}
