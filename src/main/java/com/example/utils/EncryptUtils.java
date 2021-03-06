package com.example.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author qianliao.zhuang
 */
public class EncryptUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptUtils.class);
    private static final char[] HEX_DIGITS =
            {
                    '0', '1', '2', '3',
                    '4', '5', '6', '7',
                    '8', '9', 'a', 'b',
                    'c', 'd', 'e', 'f'
            };
    private static final String ENCODING = "UTF-8";
    private static final BASE64Encoder BASE64ENCODER = new BASE64Encoder();
    private static final BASE64Decoder BASE64DECODER = new BASE64Decoder();

    private EncryptUtils() {
    }

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

    public static String md5(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = getBytes(str);
        return bytes == null ? null : md5(bytes);
    }

    public static String md5(byte[] md5ByteArray) {
        try {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(md5ByteArray);
            return bytesToHex(arrayOfByte);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MessageDigest不支持MD5", e);
        }
    }

    public static String base64Encode(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = getBytes(str);
        return bytes == null ? null : base64Encode(bytes);
    }

    public static String base64Encode(byte[] bytes) {
        return BASE64ENCODER.encodeBuffer(bytes).trim();
    }

    public static byte[] base64Decode(String str) {
        if (str == null) {
            return null;
        }
        try {
            return BASE64DECODER.decodeBuffer(str);
        } catch (IOException e) {
            LOGGER.error("BASE64解码失败，字符串为：{}", str, e);
            return null;
        }
    }

    public static byte[] base64Decode(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        base64Decode(inputStream, outputStream);
        return outputStream.toByteArray();
    }

    public static void base64Decode(InputStream inputStream, OutputStream outputStream) {
        checkNotNull(inputStream, "inputStream must be not null");

        try {
            BASE64DECODER.decodeBuffer(inputStream, outputStream);
        } catch (IOException e) {
            LOGGER.error("BASE64解码失败", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] resultCharArray = new char[bytes.length * 2];

        int index = 0;
        for (byte b : bytes) {
            resultCharArray[index++] = HEX_DIGITS[b >>> 4 & 0xf];
            resultCharArray[index++] = HEX_DIGITS[b & 0xf];
        }

        return new String(resultCharArray);
    }

    private static byte[] getBytes(String str) {
        try {
            // 有坑，如果字符串采用的编码格式不一样，即使是同一个字符串，所获得的MD5值是不一样的
            // 所以最好在获得 byte 数组时，采用统一的编码格式
            return str.getBytes(ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("不支持 {} 解码", ENCODING, e);
            return null;
        }
    }
}
