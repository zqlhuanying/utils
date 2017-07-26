package com.example.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by qianliao.zhuang on 2017/7/18.
 */
public class EncryptUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);

    public static String sign(Map<String, String> signMap, String signKey) {
        Map<String, String> sortedMap = new TreeMap<>(Ordering.natural());
        sortedMap.putAll(signMap);

        StringBuilder signBuilder = new StringBuilder();
        // 一般只会在后面加上 signKey
        signBuilder.append(signKey);
        Joiner.on("")
                .withKeyValueSeparator("")
                .appendTo(signBuilder, sortedMap)
                .append(signKey);

        return md5(signBuilder.toString());
    }

    public static String md5(String str){
        try {
            // 有坑，如果字符串采用的编码格式不一样，即使是同一个字符串，所获得的MD5值是不一样的
            // 所以最好在获得 byte 数组时，采用统一的编码格式
            return md5(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("不支持UTF-8解码", e);
        }
        return "";
    }

    public static String md5(byte[] md5ByteArray) {
        try {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(md5ByteArray);
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