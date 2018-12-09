package com.example.utils.excel.sheet.read;

import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.sheet.AbstractWorkbookSheet;
import com.example.utils.excel.sheet.BeanUtils;
import com.example.utils.excel.sheet.OPCPackageHelper;
import com.example.utils.excel.sheet.Source;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookEventSheet<T> extends AbstractWorkbookSheet<T> {

    private volatile OPCPackage pkg;

    public WorkbookEventSheet(Source<?> source, PoiOptions options) {
        this.source = source;
        this.options = options;
    }

    protected List<T> read(Class<T> type) {
        String sheetId = "rId" + String.valueOf(getOptions().getSheetIndex() + 1);
        try {
            XSSFReader reader = new XSSFReader(getOPCPackage());
            // todo 释放
            InputStream sheetInputStream = reader.getSheet(sheetId);

            return processSheet(
                    reader.getStylesTable(),
                    new ReadOnlySharedStringsTable(getOPCPackage()),
                    sheetInputStream,
                    type, getOptions().getSkip(), getRows());
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            log.error("read values from sheet failed by using event mode", e);
        }
        return Collections.emptyList();
    }

    @Override
    public int getRows() {
        // todo
        return 1000;
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

    private List<T> processSheet(StylesTable stylesTable,
                                 ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                 InputStream sheetInputStream,
                                 Class<T> type, int start, int end)
            throws ParserConfigurationException, SAXException, IOException {
        SheetHandler sheetHandler = new SheetHandler(start, end, type);
        ContentHandler contentHandler = new XSSFSheetXMLHandler(stylesTable, readOnlySharedStringsTable, sheetHandler, false);
        XMLReader parser = SAXHelper.newXMLReader();
        parser.setContentHandler(contentHandler);
        parser.parse(new InputSource(sheetInputStream));
        return sheetHandler.getResult();
    }

    private class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final int start;
        private final int end;
        private final Class<T> type;
        private final List<T> result;
        private T instance;

        SheetHandler(int start, int end, Class<T> type) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.result = Lists.newArrayListWithCapacity(this.end - this.start);
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
                result.add(instance);
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

        }

        boolean inRange(int rowNum) {
            return rowNum + 1 > this.start && rowNum + 1 <= this.end;
        }

        List<T> getResult() {
            return result;
        }
    }
}
