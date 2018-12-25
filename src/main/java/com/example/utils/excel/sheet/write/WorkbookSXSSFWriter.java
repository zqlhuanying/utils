package com.example.utils.excel.sheet.write;

import com.alibaba.fastjson.JSONObject;
import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.exception.PoiExcelTypeException;
import com.example.utils.excel.exception.PoiException;
import com.example.utils.excel.handler.CellStyleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
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
        TempFile.setTempFileCreationStrategy(MyTempFileCreationStrategy.getInstance());
    }

    public WorkbookSXSSFWriter<T, R> setRowAccessWindowSize(int rowAccessWindowSize) {
        this.writeSheet.setRowAccessWindowSize(rowAccessWindowSize);
        return this;
    }

    public WorkbookSXSSFWriter<T, R> setCellStyleHandler(CellStyleHandler<T> cellStyleHandler) {
        getWriteSheet().setCellStyleHandler(cellStyleHandler);
        return this;
    }

    @Override
    public R write(List<T> values, Class<T> clazz) {
        OutputStream output = null;
        try (
                SXSSFWorkbook workbook = (SXSSFWorkbook) getWriteSheet().getWorkbook()
        ) {
            output = getOutputStream();
            getWriteSheet().write(values, clazz);
            workbook.write(output);
            // release temp .xml
            workbook.dispose();
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

    /**
     * solve "java.io.IOException: Could not create temporary directory '/${dir}/temp/poifiles'"
     * 在多线程环境中，有可能存在多个线程同时尝试去创建该临时目录，所以交由这边去创建，保证只有一个线程能创建成功
     * 默认创建临时目录的代码位于: DefaultTempFileCreationStrategy.createTempDirectory()
     */
    private static class MyTempFileCreationStrategy extends DefaultTempFileCreationStrategy {

        private static final String JAVA_IO_TMPDIR = TempFile.JAVA_IO_TMPDIR;
        private static final String POIFILES = "poifiles";
        private static final File DIR;

        static {
            String tmpDir = System.getProperty(JAVA_IO_TMPDIR);
            if (tmpDir == null) {
                throw new PoiException("Systems temporary directory not defined - set the -D"+JAVA_IO_TMPDIR+" jvm property!");
            }
            DIR = new File(tmpDir, POIFILES);
            DIR.mkdir();
        }

        private MyTempFileCreationStrategy() {
            super(DIR);
        }

        public static MyTempFileCreationStrategy getInstance() {
            return Singleton.INSTANCE.getInstance();
        }

        enum Singleton {
            /**
             * Singleton
             */
            INSTANCE;

            private MyTempFileCreationStrategy strategy;

            Singleton() {
                this.strategy = new MyTempFileCreationStrategy();
            }

            MyTempFileCreationStrategy getInstance() {
                return strategy;
            }
        }
    }
}
