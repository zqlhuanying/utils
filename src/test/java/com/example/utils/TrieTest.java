package com.example.utils;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.junit.Test;

import java.util.Map;

/**
 * Created by qianliao.zhuang on 2017/9/11.
 */
public class TrieTest {

    @Test
    public void trieTest(){
        String[] array = new String[]{
                "二维码",
                "微信",
                "微信号",
                "时尚"
        };

        PatriciaTrie<String> trie = new PatriciaTrie<>();
        for (String str: array) {
            trie.put(str, str);
        }

        System.out.println(trie.selectValue("1微信"));
    }
}
