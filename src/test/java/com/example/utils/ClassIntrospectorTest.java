package com.example.utils;

import com.example.utils.objectSize.ClassIntrospector;
import com.example.utils.objectSize.ObjectInfo;
import com.example.utils.poi.Policy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 * @author qianliao.zhuang
 * @date 2018/11/5
 */
public class ClassIntrospectorTest {
    @Test
    public void testObjectSize() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("1");
        for (int i = 0; i < 2500; i++) {
            sheet.createRow(i);
        }
/*        objectSize(sheet);
        objectSize(row);*/
        objectSize(cell);
    }

    private void objectSize(Object o) throws Exception {
        final ClassIntrospector ci = new ClassIntrospector();
        ObjectInfo res = ci.introspect(o);
        System.out.println( res.getDeepSize() );
    }
}
