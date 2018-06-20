package com.example.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
                HttpExecutors.create(url)
                        .build()
                        .httpGet()
        );
    }

    @Test
    public void httpGetExecutorsWithCookieTest(){
        Map<String, String> header = new HashMap<>();
        header.put("cookie", "Hm_lvt_1fac6291d8c5ea2916c96eb3d13cde5a=1529482833; PHPSESSID=d6uvnp05cb718f8emkhj4keq30; _identity=29f6819adf8558c9351e0aab3f9e2907ea122907c97b7c134c1e62b554019320a%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22_identity%22%3Bi%3A1%3Bs%3A63%3A%22%5B9086%2C%22RkKBt0jy31shZUBOenrXgvKSktYZfrUc%22%2C2592000%2C%2218121117268%22%5D%22%3B%7D; introduce_url=19aa724f58dc12a71cbc6a5c614ef2a3070febeb0b39663d87e9a5dee25d5081a%3A2%3A%7Bi%3A0%3Bs%3A13%3A%22introduce_url%22%3Bi%3A1%3Bs%3A32%3A%22be9cda6d9a6d7d87ef16a4f04db1257d%22%3B%7D; pt_s_28623907=vt=1529482873986&cad=; Hm_lpvt_1fac6291d8c5ea2916c96eb3d13cde5a=1529482874; pt_28623907=uid=RAh2WBsaxef6StFKf3GO3w&nid=&vid=Fqg2WfuWWXA2wDUw5aBjKA&vn=2&pvn=7&sact=1529482880891&to_flag=0&pl=NvcymVu5pcDLiGniNOxNCA*pt*1529482873986");

        String url = "http://www.ad1024.com/feeds-radar/new-ads";
        String s = HttpExecutors.create(url)
                .setHeaders(header)
                .build()
                .httpGet();
        System.out.println(s);
    }
}
