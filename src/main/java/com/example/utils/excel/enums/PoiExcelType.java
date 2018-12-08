package com.example.utils.excel.enums;

import com.example.utils.excel.exception.PoiExcelTypeException;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.sheet.PoiFile;
import com.example.utils.excel.sheet.PoiInputStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhuangqianliao
 */
@Slf4j
public enum PoiExcelType {

    /**
     * xls 表格
     */
    XLS("xls") {
        @Override
        public Workbook createWorkbook() {
            return new HSSFWorkbook();
        }
    },

    /**
     * xlsx 表格
     */
    XLSX("xlsx") {
        @Override
        public Workbook createWorkbook() {
            return new XSSFWorkbook();
        }
    };

    @Getter
    private String type;

    PoiExcelType(String type) {
        this.type = type;
    }

    /**
     * 创建一个空的 Workbook
     */
    public abstract Workbook createWorkbook();

    /**
     * 创建以 File 为内容的 Workbook
     */
    public Workbook createWorkbook(PoiFile<? extends File> file) {
        try {
            return WorkbookFactory.create(file.file());
        } catch (IOException | InvalidFormatException e) {
            log.error("can not create workbook for file: {}", file.name(), e);
            throw new PoiException("can not create workbook", e);
        }
    }

    /**
     * 创建以 InputStream 为内容的 Workbook
     */
    public Workbook createWorkbook(PoiInputStream<? extends InputStream> inputStream) {
        try {
            return WorkbookFactory.create(inputStream.stream());
        } catch (IOException | InvalidFormatException e) {
            log.error("can not create workbook", e);
            throw new PoiException("can not create workbook", e);
        }
    }

    public static PoiExcelType from(String type) {
        if (StringUtils.isBlank(type)) {
            throw new PoiExcelTypeException("type must be not blank");
        }

        for (PoiExcelType excelType : PoiExcelType.values()) {
            if (excelType.getType().equals(type)) {
                return excelType;
            }
        }
        throw new PoiExcelTypeException("Unsupported Excel Type");
    }
}
