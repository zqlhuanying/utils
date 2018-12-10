package com.example.utils.excel.sheet.read;

import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.BeanUtils;
import com.example.utils.excel.sheet.OPCPackageHelper;
import com.example.utils.excel.sheet.Source;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

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
    private volatile Integer rows;


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

    @Override
    public int getRows() {
        if (rows == null) {
            synchronized (this) {
                if (rows == null) {
                    this.rows = doGetRows();
                }
            }
        }
        return this.rows;
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

    private Map<Integer, String> doRead() {
        if (!completed) {
            synchronized (this) {
                if (!completed) {
                    this.resultCache = getResult();
                    completed = true;
                }
            }
        }
        return this.resultCache;
    }

    private Map<Integer, String> getResult() {
        InputStream sheetInputStream = null;
        try {
            XSSFReader reader = new XSSFReader(getOPCPackage());
            sheetInputStream = reader.getSheet(getSheetIndex());

            return processSheet(
                    reader.getStylesTable(),
                    new ReadOnlySharedStringsTable(getOPCPackage()),
                    sheetInputStream,
                    getOptions().getSkip(), getRows());
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            log.error("read values from sheet failed by using event mode", e);
        } finally {
            IOUtils.closeQuietly(sheetInputStream);
        }
        return Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    private int doGetRows() {
        InputStream sheetInputStream = null;
        XMLReader parser = null;
        try {
            XSSFReader reader = new XSSFReader(getOPCPackage());
            sheetInputStream = reader.getSheet(getSheetIndex());

            parser = SAXHelper.newXMLReader();
            parser.setContentHandler(new SheetTotalHandler());
            parser.parse(new InputSource(sheetInputStream));
        } catch (MySAXParseException e) {
            // stop parsing xml
            if (parser != null) {
                return ((SheetTotalHandler) parser.getContentHandler()).getTotal();
            }
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            log.error("read total rows from sheet failed by using event mode", e);
        } finally {
            IOUtils.closeQuietly(sheetInputStream);
        }
        log.warn("get sheet total failed");
        return 0;
    }

    private Map<Integer, String> processSheet(StylesTable stylesTable,
                                              ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                              InputStream sheetInputStream,
                                              int start, int end)
            throws ParserConfigurationException, SAXException, IOException {
        SheetContentHandler sheetContentHandler = new SheetContentHandler(start, end);
        ContentHandler contentHandler = new XSSFSheetXMLHandler(stylesTable, readOnlySharedStringsTable, sheetContentHandler, false);
        XMLReader parser = SAXHelper.newXMLReader();
        parser.setContentHandler(contentHandler);
        parser.parse(new InputSource(sheetInputStream));
        return sheetContentHandler.getResult();
    }

    private String getSheetIndex() {
        return "rId" + String.valueOf(getOptions().getSheetIndex() + 1);
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

    private class SheetTotalHandler extends DefaultHandler {

        private static final String NAME = "dimension";
        private int total;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (uri != null && ! uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
                return;
            }
            if (NAME.equals(localName)) {
                String dimension = attributes.getValue("ref");
                total = extractNum(dimension);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (uri != null && ! uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
                return;
            }
            if (NAME.equals(localName)) {
                throw new MySAXParseException("stop parsing xml for already found dimension node");
            }
        }

        int getTotal() {
            return total;
        }

        /**
         * 查找最后几位数字
         */
        private int extractNum(String str) {
            if (StringUtils.isBlank(str)) {
                return 0;
            }
            int numIndex = -1;
            for (int i = str.length() - 1; i >= 0 ; i--) {
                if (!Character.isDigit(str.charAt(i))) {
                    break;
                }
                numIndex = i;
            }
            return numIndex > 0 ? Integer.parseInt(str.substring(numIndex)) : 0;
        }
    }

    /**
     * sheet content handler
     * Get content[start, end](exclude end) from sheet
     */
    private class SheetContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final int start;
        private final int end;
        private final Map<Integer, String> result;
        private StringBuilder sb;

        SheetContentHandler(int start, int end) {
            this.start = start;
            this.end = end;
            this.result = Maps.newHashMapWithExpectedSize(64);
        }

        @Override
        public void startRow(int rowNum) {
            if (inRange(rowNum)) {
                this.sb = new StringBuilder();
            }
        }

        @Override
        public void endRow(int rowNum) {
            if (this.sb != null) {
                result.put(rowNum, this.sb.toString());
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
            return rowNum + 1 > this.start && rowNum + 1 <= this.end;
        }

        Map<Integer, String> getResult() {
            return result;
        }
    }

    private static class MySAXParseException extends RuntimeException {
        public MySAXParseException(String message) {
            super(message);
        }

        public MySAXParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
