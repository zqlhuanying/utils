package com.example.utils;

import org.junit.Test;

/**
 * @author qianliao.zhuang
 * @date 2017/11/8
 */
public class HttpExecutorsTest {

    @Test
    public void connectionManagerSharedTest() {
        String url = "https://www.baidu.com";

        HttpExecutors httpExecutors1 = HttpExecutors.create()
                .setUrl(url)
                .build();
        for (int i = 0; i < 1000; i++) {
            httpExecutors1.httpGet();
        }
    }
}
