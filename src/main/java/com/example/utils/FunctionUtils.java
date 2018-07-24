package com.example.utils;

import com.google.common.base.Function;

/**
 * @author qianliao.zhuang
 */
public final class FunctionUtils {
    private FunctionUtils() {}

    private static final Function<String, Long> STRING_TO_LONG_FUNCTION = new Function<String, Long>() {
        @Override
        public Long apply(String input) {
            return Long.parseLong(input);
        }
    };

    private static final Function<String, Integer> STRING_TO_INTEGER_FUNCTION = new Function<String, Integer>() {
        @Override
        public Integer apply(String input) {
            return Integer.parseInt(input);
        }
    };

    public static Function<String, Long> stringToLong() {
        return STRING_TO_LONG_FUNCTION;
    }

    public static Function<String, Integer> stringToInteger() {
        return STRING_TO_INTEGER_FUNCTION;
    }

    /**
     * Function 将数字转成字符串
     * @param <F>: Number
     * @return: Function
     */
    @SuppressWarnings("unchecked")
    public static <F extends Number> Function<F, String> numberToString() {
        return (Function<F, String>) NumberToStringFunction.INSTANCE;
    }

    private enum NumberToStringFunction implements Function<Number, String> {
        INSTANCE;

        @Override
        public String apply(Number input) {
            return String.valueOf(input);
        }
    }
}
