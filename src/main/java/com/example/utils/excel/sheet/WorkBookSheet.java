package com.example.utils.excel.sheet;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author zhuangqianliao
 */
public interface WorkBookSheet<T> {

    Workbook getWorkbook();

    Sheet getSheet();

    int getRows();
}
