package com.example.utils.poi;

import com.example.utils.excel.POI;
import com.example.utils.excel.enums.PoiExcelType;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author zhuangqianliao
 */
public class PoiPeopleTest {

    private static final String dir = "D:\\1\\";

    @Before
    public void init() {
        PeopleMapper.init();
    }

    /**
     * 小文件读取
     */
    @Test
    public void smallFileRead() {
        String path = dir + "people.xlsx";
        List<People> peoples = POI.<People>fromExcel(new File(path))
                .read(People.class);
        assert peoples.size() == 2;
    }

    @Test
    public void bigFileRead() {
        String path = dir + "people.xlsx";
        List<People> peoples = POI.<People>fromExcel(new File(path))
                .bigReader()
                .setTaskThreshold(1)
                .read(People.class);
        assert peoples.size() == 2;
    }

    /**
     * 小文件读取
     */
    @Test
    public void smallStreamRead() throws Exception {
        String path = dir + "people.xlsx";
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(path), PoiExcelType.XLSX)
                .read(People.class);
        assert peoples.size() == 2;
    }

    @Test
    public void bigStreamRead() throws Exception {
        String path = dir + "people.xlsx";
        List<People> peoples = POI.<People>fromExcel(new FileInputStream(path), PoiExcelType.XLSX)
                .bigReader()
                .read(People.class);
        assert peoples.size() == 2;
    }

    private static class PeopleMapper extends Mapper<People> {

        public static void init() {
            Mappers.registry(new PeopleMapper(0, "姓名", "name"));
            Mappers.registry(new PeopleMapper(1, "姓氏", "firstName"));
            Mappers.registry(new PeopleMapper(2, "名字", "lastName"));
        }

        public PeopleMapper(int columnIndex, String columnName, String fieldName) {
            super(columnIndex, columnName, fieldName, People.class);
        }
    }
}
