package com.example.utils;

import com.example.utils.excel.POI;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.mapper.Mappers;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qianliao.zhuang
 * @date 2018/11/5
 */
public class PoiTest {

    @Before
    public void init() {
        Mappers.registry(new PeopleMapper(0, "姓名", "name"));
        Mappers.registry(new PeopleMapper(1, "年龄", "age"));
    }

    @Test
    public void testWriteExcel() {
        List<People> peoples = get10000();
        POI.<People>writeExcel(new File("D:\\Workspace\\Idea\\Deamon\\testDatas\\out.xlsx"))
                .write(peoples, People.class);
    }

    private List<People> get5000() {
        List<People> peoples = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            peoples.add(instance());
        }
        return peoples;
    }

    private List<People> get10000() {
        List<People> peoples = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            peoples.add(instance());
        }
        return peoples;
    }

    private People instance() {
        People people = new People();
        people.name = "test";
        people.age = "12";
        return people;
    }

    private class PeopleMapper extends Mapper<People> {
        public PeopleMapper(int columnIndex, String columnName, String fieldName) {
            super(columnIndex, columnName, fieldName, People.class);
        }
    }

    @Data
    public static class People {
        private String name;
        private String age;
    }
}
