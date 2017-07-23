package com.example.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.utils.EncryptUtils.sign;

/**
 * Created by qianliao.zhuang on 2017/7/18.
 */
public class EncryptUtilsTest {

    @Test
    public void signForGetMethodTest(){
        Map<String, String> localMap = new HashMap<>();
        localMap.put("timestamp", "1499922054161");
        localMap.put("app-key", "android");
        localMap.put("accept-version", "2.0.0");
        localMap.put("deviceid", "ffffffff-bfa1-da5f-ffff-ffff8c9c13e2");
        localMap.put("app-version", "3.5.0");

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("assortmenttype", "1");
        paramMap.put("keyword", "422");

        String sign_key = "m8y9uKNrhwVu1Euc";
        Map<String, String> allMap = new HashMap<>();
        allMap.putAll(localMap);
        allMap.putAll(paramMap);
        String sign = sign(allMap, sign_key);
        System.out.println(sign);

        String shouldSign = "469f618b7800368b414881223f2fe50d";
        assert shouldSign.equals(sign);
    }

    @Test
    public void signForPostMethodTest(){
        Map<String, String> localMap = new HashMap<>();
        localMap.put("timestamp", "1499922312338");
        localMap.put("app-key", "ios");
        localMap.put("accept-version", "2.0.0");
        localMap.put("deviceid", "ffffffff-bfa1-da5f-ffff-ffff8c9c13e2");
        localMap.put("app-version", "3.5.0");
        // 虽然 Content-Type不参与sign计算，但最后还是需要传递
        //localMap.put("Content-Type", "application/json; charset=utf-8");

        String str = "{\"pageindex\":0,\"pagesize\":20,\"sort\":{\"type\":1},\"assortmenttype\":1,\"filter\":[],\"subkeyword\":\"城野医生\",\"keyword\":\"241\"}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        Map<String, String> paramMap = parse(jsonObject);

        String sign_key = "m8y9uKNrhwVu1Euc";
        Map<String, String> allMap = new HashMap<>();
        allMap.putAll(localMap);
        allMap.putAll(paramMap);
        String sign = sign(allMap, sign_key);
        System.out.println(sign);

        String shouldSign = "7f9c885310b1886df16fcc8fb897ba31";
        assert shouldSign.equals(sign);
    }

    private Map<String, String> parse(JSONObject paramJSONObject){
        Map<String, String> localHashMap = new HashMap<>();
        try {
            Set<String> sets = paramJSONObject.keySet();
            for(String str : sets){
                if ((paramJSONObject.get(str) instanceof JSONArray)) {
                    JSONArray localJSONArray = (JSONArray)paramJSONObject.get(str);
                    for (int i = 0; i < localJSONArray.size(); i++) {
                        Object[] arrayOfObject = new Object[1];
                        arrayOfObject[0] = Integer.valueOf(i);
                        localHashMap.put(String.format("[%d]", arrayOfObject), localJSONArray.getString(i));
                    }
                } else {
                    localHashMap.put(str, paramJSONObject.getString(str));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localHashMap;
    }
}
