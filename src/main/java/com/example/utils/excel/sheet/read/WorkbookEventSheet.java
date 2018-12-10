package com.example.utils.excel.sheet.read;

import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.BeanUtils;
import com.example.utils.excel.sheet.OPCPackageHelper;
import com.example.utils.excel.sheet.Source;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
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
    private boolean completed = false;
    /**
     * 当前 sheet 的结果缓存
     * rowNum -> T
     */
    private Map<Integer, T> resultCache;

    /**
     * 当前结果数的缓存
     */
    private Integer rows;


    public WorkbookEventSheet(Source<?> source, PoiOptions options) {
        this.source = source;
        this.options = options;
    }

    protected List<T> read(Class<T> type) {
        return Lists.newArrayList(doRead(type).values());
    }

    protected List<T> read(int start, int end, Class<T> type) {
        Map<Integer, T> result = doRead(type);
        return IntStream.range(start, end)
                .mapToObj(result::get)
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

    private Map<Integer, T> doRead(Class<T> type) {
        if (!completed) {
            synchronized (this) {
                if (!completed) {
                    this.resultCache = getResult(type);
                    completed = true;
                }
            }
        }
        return this.resultCache;
    }

    private Map<Integer, T> getResult(Class<T> type) {
        InputStream sheetInputStream = null;
        try {
            XSSFReader reader = new XSSFReader(getOPCPackage());
            sheetInputStream = reader.getSheet(getSheetIndex());

            return processSheet(
                    reader.getStylesTable(),
                    new ReadOnlySharedStringsTable(getOPCPackage()),
                    sheetInputStream,
                    type, getOptions().getSkip(), getRows());
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

    private Map<Integer, T> processSheet(StylesTable stylesTable,
                                         ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                         InputStream sheetInputStream,
                                         Class<T> type, int start, int end)
            throws ParserConfigurationException, SAXException, IOException {
        SheetContentHandler sheetContentHandler = new SheetContentHandler(start, end, type);
        ContentHandler contentHandler = new XSSFSheetXMLHandler(stylesTable, readOnlySharedStringsTable, sheetContentHandler, false);
        XMLReader parser = SAXHelper.newXMLReader();
        parser.setContentHandler(contentHandler);
        parser.parse(new InputSource(sheetInputStream));
        return sheetContentHandler.getResult();
    }

    private String getSheetIndex() {
        return "rId" + String.valueOf(getOptions().getSheetIndex() + 1);
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
     * Get content[start, end] from sheet
     */
    private class SheetContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final int start;
        private final int end;
        private final Class<T> type;
        private final Map<Integer, T> result;
        private T instance;

        SheetContentHandler(int start, int end, Class<T> type) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.result = Maps.newHashMapWithExpectedSize(64);
        }

        @Override
        public void startRow(int rowNum) {
            if (inRange(rowNum)) {
                this.instance = BeanUtils.newInstance(type);
            }
        }

        @Override
        public void endRow(int rowNum) {
            if (this.instance != null) {
                result.put(rowNum, instance);
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            CellAddress cellAddress = new CellAddress(cellReference);
            if (inRange(cellAddress.getRow())) {
                int columnIndex = cellAddress.getColumn();

                Mapper<T> mapper = Mappers.getMapper(columnIndex, type);
                if (mapper == null) {
                    log.warn("can not find suitable mapper for column: {}.", columnIndex);
                    return;
                }
                writeToInstance(mapper, formattedValue, this.instance);
            }
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // nothing to do
        }

        boolean inRange(int rowNum) {
            return rowNum + 1 > this.start && rowNum + 1 <= this.end;
        }

        Map<Integer, T> getResult() {
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
