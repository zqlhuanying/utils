package com.example.utils;

import org.junit.Test;

/**
 * @author qianliao.zhuang
 */
public class HttpExecutorsTest {

    @Test
    public void httpsGetTest() {
        String url = "https://www.baidu.com";

        for (int i = 0; i < 10; i++) {
            String res = HttpExecutors.create(url)
                    .build()
                    .httpGet();
            System.out.println(res);
        }
    }
}
