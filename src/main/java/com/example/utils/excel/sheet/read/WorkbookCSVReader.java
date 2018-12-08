package com.example.utils.excel.sheet.read;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
public class WorkbookCSVReader<T> extends FilterWorkbookReader<T> {

    public WorkbookCSVReader(WorkbookReader<T> reader) {
        super(reader);
    }

    @Override
    public List<T> read(Class<T> type) {
        String path = "d:\\1\\people.xlsx";
        try {
            OPCPackage pkg = OPCPackage.open(new File(path), PackageAccess.READ);
            XSSFReader reader = new XSSFReader(pkg);
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();

            ReadOnlySharedStringsTable readOnlySharedStringsTable = new ReadOnlySharedStringsTable(pkg);
            StylesTable stylesTable = reader.getStylesTable();

            XMLReader parser = SAXHelper.newXMLReader();
            parser.setContentHandler(new XSSFSheetXMLHandler(stylesTable, readOnlySharedStringsTable, new SheetHandler(), false));
            parser.parse(new InputSource(sheetIterator.next()));
        } catch (OpenXML4JException | IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private class SheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        @Override
        public void startRow(int rowNum) {
            if (rowNum + 1 > 1) {

            }
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum + 1 > 4) {
                return;
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            CellAddress cellAddress = new CellAddress(cellReference);
            int row = cellAddress.getRow();
            int column = cellAddress.getColumn();
            System.out.println(formattedValue);
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {

        }
    }
}
