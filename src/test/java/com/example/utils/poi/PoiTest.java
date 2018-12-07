package com.example.utils.poi;

import com.example.utils.excel.POI;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qianliao.zhuang
 */
public class PoiTest {

    private static final String dir = "D:\\Workspace\\Idea\\Deamon\\testDatas\\";
    private static Policy policy;

    @Before
    public void init() {
        PolicyMapper.init();
    }

/*    @Test
    public void testWriteExcel() {
        List<Policy> peoples = get5000();
        POI.<Policy>writeExcel(new File(dir + "out.xlsx"))
                .getSheet()
                .getWriter()
                .write(peoples, Policy.class);
    }*/

/*
    @Test
    public void testWriteExcel1() {
        List<Policy> peoples = get5000();
        POI.<Policy>writeExcel(new File(dir + "out.xlsx"))
                .sxssfWriter()
                .setRowAccessWindowSize(1000)
                .write(peoples, Policy.class);
    }
*/

    private List<Policy> get5000() {
        List<Policy> policies = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            policies.add(instance());
        }
        return policies;
    }

    private List<Policy> get10000() {
        List<Policy> policies = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            policies.add(instance());
        }
        return policies;
    }

    private Policy instance() {
        if (policy == null) {
            policy = POI.<Policy>fromExcel(new File(dir + "JDV1.3.xlsx"))
                    .read(Policy.class).get(0);
        }
        return policy;
    }
}
