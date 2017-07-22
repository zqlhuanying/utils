package com.example.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.*;

import static com.example.utils.CollectionUtils.*;

/**
 * Created by qianliao.zhuang on 2017/7/11.
 */
public class CollectionUtilsTest {
    private static List<Object> list;
    private static List<Object> list1;
    private static List<Object> nullList;
    private static Set<Object> set;
    private static Map<String, Object> map;

    private static List<Object> flattenList;

    /**
     * list: [1, 2, [5, 6]]
     * nullList: [1, 2, null]
     * list1: [5, 6]
     * flattenList: [3, 4, [1, 2, [5, 6]], 7]
     */
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
        map.put("3", "2");
    }

    static {
        list1 = new ArrayList<>();
        list1.add("5");
        list1.add("6");
        list.add(list1);

        flattenList = new ArrayList<>();
        flattenList.add("3");
        flattenList.add("4");
        flattenList.add(list);
        flattenList.add("7");
        flattenList.add(null);
        flattenList.add(nullList);
    }

    /**
     * result: [11, 21, [5, 6]1]
     */
    @Test
    public void mapTest(){
        print(map(list, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return input + "1";
            }
        }));
    }

    /**
     * result: [1, 2]
     */
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

    /**
     * flattenList: [3, 4, [1, 2, [5, 6]], 7, null, [1, 2, null]]
     * result: [3, 4, 1, 2, 5, 6, 7, null, 1, 2, null]
     */
    @Test
    public void flattenTest(){
        print(flatten(flattenList));

        List<Object> otherFlattenList = new ArrayList<>();
        otherFlattenList.add("ss");
        print(flatten(flattenList, otherFlattenList));

        List<List<String>> iterableOnIterable = Lists.newArrayList();
        List<String> l = new ArrayList<>();
        l.add("1");
        l.add("2");
        iterableOnIterable.add(l);
        List<String> l1 = new ArrayList<>();
        l1.add("3a");
        l1.add("4");
        iterableOnIterable.add(l1);
        Iterable<String> s = flatten(iterableOnIterable);
        print(s);
    }
}
