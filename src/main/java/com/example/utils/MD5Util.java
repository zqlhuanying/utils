package com.example.utils;

import com.google.common.base.Joiner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by qianliao.zhuang on 2017/7/18.
 */
public class MD5Util {

    public static String sign(Map<String, String> signMap, String signKey) {
        Object[] arrayOfObject = signMap.keySet().toArray();
        Arrays.sort(arrayOfObject);
        StringBuilder signBuilder = new StringBuilder();
        // 一般只会在后面加上 signKey
        signBuilder.append(signKey);
        Joiner.on("")
                .withKeyValueSeparator("")
                .appendTo(signBuilder, signMap);
        signBuilder.append(signKey);
        return md5(signBuilder.toString().getBytes());
    }

    public static String md5(byte[] paramArrayOfByte) {
        try {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramArrayOfByte);
            StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            int i = arrayOfByte.length;
            for (int j = 0; j < i; j++) {
                int k = arrayOfByte[j];
                if ((k & 0xFF) < 16) {
                    localStringBuilder.append("0");
                }
                localStringBuilder.append(Integer.toHexString(k & 0xFF));
            }
            return localStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MessageDigest不支持MD5", e);
        }
    }
}
