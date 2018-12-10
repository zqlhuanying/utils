package com.example.utils.poi;

import com.example.utils.excel.POI;
import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public class PoiPeopleTest {

    private static final String dir = "D:\\1\\";
    private static final String smallFilePath = dir + "people.xlsx";
    private static final int excelSize = 9;
    private static final int taskThreshold = 2;

    @Before
    public void init() {
        PeopleMapper.init();
    }

    @Test
    public void smallFileRead() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void bigFileRead() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .bigReader()
                .setTaskThreshold(taskThreshold)
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void smallFileEventRead() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .eventReader()
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void bigFileEventRead() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .eventReader()
                .bigReader()
                .setTaskThreshold(taskThreshold)
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void smallStreamRead() throws Exception {
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(smallFilePath), PoiExcelType.XLSX)
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void bigStreamRead() throws Exception {
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(smallFilePath), PoiExcelType.XLSX)
                .bigReader()
                .setTaskThreshold(taskThreshold)
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void smallStreamEventRead() throws Exception {
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(smallFilePath), PoiExcelType.XLSX)
                .eventReader()
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void bigStreamEventRead() throws Exception {
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(smallFilePath), PoiExcelType.XLSX)
                .eventReader()
                .bigReader()
                .setTaskThreshold(taskThreshold)
                .read(People.class);
        assert peoples.size() == excelSize;
    }

    @Test
    public void smallFileWrite() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .eventReader()
                .read(People.class);

        String out = dir + "outFile.xlsx";
        String path = POI.<People>writeExcel(new File(out))
                .write(peoples, People.class);

        List<People> peoples0 = POI.<People>fromExcel(new File(path))
                .eventReader()
                .read(People.class);
        assert peoples0.size() == excelSize;
    }

    @Test
    public void smallFileSXSSFWrite() {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .eventReader()
                .read(People.class);

        String out = dir + "outFileSXSSF.xlsx";
        String path = POI.<People>writeExcel(new File(out))
                .sxssfWriter()
                .write(peoples, People.class);

        List<People> peoples0 = POI.<People>fromExcel(new File(path))
                .eventReader()
                .read(People.class);
        assert peoples0.size() == excelSize;
    }

    @Test
    public void smallStreamWrite() throws Exception {
        List<People> peoples = POI.<People>fromExcel(new File(smallFilePath))
                .eventReader()
                .read(People.class);

        OutputStream byteArrayOutputStream =
                POI.<People>writeExcel(new ByteArrayOutputStream(), PoiExcelType.XLSX)
                .sxssfWriter()
                .write(peoples, People.class);

        // 输出到文件
        String out = dir + "outStream.xlsx";
        OutputStream fileOutput = new FileOutputStream(out);
        ((ByteArrayOutputStream) byteArrayOutputStream).writeTo(fileOutput);
        fileOutput.flush();
        fileOutput.close();

        List<People> peoples0 = POI.<People>fromExcel(new File(out))
                .eventReader()
                .read(People.class);
        assert peoples0.size() == excelSize;
    }

    private static class PeopleMapper extends Mapper<People> {

        static void init() {
            Mappers.registry(new PeopleMapper(0, "姓名", "name"));
            Mappers.registry(new PeopleMapper(1, "姓氏", "firstName"));
            Mappers.registry(new PeopleMapper(2, "名字", "lastName"));
        }

        PeopleMapper(int columnIndex, String columnName, String fieldName) {
            super(columnIndex, columnName, fieldName, People.class);
        }
    }
}
