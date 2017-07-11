package com.example.utils;

import com.google.common.base.Function;
import org.junit.Test;

import java.util.*;

import static com.example.utils.CollectionUtils.*;

/**
 * Created by qianliao.zhuang on 2017/7/11.
 */
public class CollectionUtilsTest {
    private static List<String> list;
    private static List<String> nullList;
    private static Set<String> set;
    private static Map<String, String> map;

    static {
        list = new ArrayList<>();
        list.add("1");
        list.add("2");

        nullList = new ArrayList<>();
        nullList.add("1");
        nullList.add("2");
        nullList.add(null);

        set = new HashSet<>();
        set.add("1");
        set.add("2");

        map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
    }

    @Test
    public void mapTest(){
        print(map(list, new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input + "1";
            }
        }));
    }

    @Test
    public void nullFilterTest(){
        print(nullFilter(nullList));
    }

    private <T> void print(Iterable<T> iterable){
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
