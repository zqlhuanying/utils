package com.example.utils;

import org.junit.Test;

/**
 * Created by qianliao.zhuang on 2017/7/23.
 */
public class HttpUtilsTest {

    @Test
    public void httpGetTest(){
        String url = "http://www.baidu.com";
        System.out.println(HttpUtils.httpGet(url));
    }

    @Test
    public void httpGetExecutorsTest(){
        String url = "http://www.baidu.com";
        System.out.println(
                HttpExecutors.create()
                        .setUrl(url)
                        .build()
                        .httpGet()
        );
    }
}
