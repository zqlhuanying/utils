package com.example.utils.excel.sheet.write1;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiExcelTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public class WorkbookSXSSFWriter<T, R> extends FilterWorkbookWriter<T, R> {

    private WorkbookSXSSFWriteSheet<T> writeSheet;

    public WorkbookSXSSFWriter(WorkbookWriter<T, R> writer) {
        super(writer);

        if (writer.getSource().type() != PoiExcelType.XLSX) {
            throw new PoiExcelTypeException("Streaming can not supported .xls");
        }

        this.writeSheet = new WorkbookSXSSFWriteSheet<>(getSource(), getOptions());
    }

    public WorkbookSXSSFWriter<T, R> setRowAccessWindowSize(int rowAccessWindowSize) {
        this.writeSheet.setRowAccessWindowSize(rowAccessWindowSize);
        return this;
    }

    @Override
    public R write(List<T> values, Class<T> clazz) {
        OutputStream output = null;
        try (
                Workbook workbook = getWriteSheet().getWorkbook()
        ) {
            output = getOutputStream();
            getWriteSheet().write(values, clazz);
            workbook.write(output);
        } catch (IOException e) {
            log.error("can not auto-close workbook", e);
            return null;
        } catch (Exception e) {
            log.error("write values: {} failed!",
                    JSONObject.toJSONString(values), e);
            return null;
        }
        return save(output);
    }

    public WorkbookSXSSFWriteSheet<T> getWriteSheet() {
        return this.writeSheet;
    }
}
