package com.example.utils.java8;

import com.example.utils.DateUtils;
import com.google.common.base.Splitter;
import jdk.nashorn.internal.runtime.ParserException;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author qianliao.zhuang
 */
public final class FunctionUtils {
    private FunctionUtils() {}

    private static final Function<String, Long> STRING_TO_LONG_FUNCTION = Long::parseLong;

    private static final Function<String, Integer> STRING_TO_INTEGER_FUNCTION = Integer::parseInt;

    private static final Function<String, Boolean> STRING_TO_BOOLEAN_FUNCTION = Boolean::valueOf;

    private static final Function<String, Map<String, String>> STRING_TO_MAP = new Function<String, Map<String, String>>() {
        private final Splitter.MapSplitter mapSplitter = Splitter.on(",").withKeyValueSeparator("=");
        @Override
        public Map<String, String> apply(String input) {
            return mapSplitter.split(input);
        }
    };

    public static Function<String, Long> stringToLong() {
        return STRING_TO_LONG_FUNCTION;
    }

    public static Function<String, Integer> stringToInteger() {
        return STRING_TO_INTEGER_FUNCTION;
    }

    public static Function<String, Boolean> stringToBoolean() {
        return STRING_TO_BOOLEAN_FUNCTION;
    }

    public static Function<String, Date> stringToDate() {
        return StringToDateFunction.INSTANCE;
    }

    public static Function<String, Date> stringToDateWithFormat(String format) {
        return FunctionUtils.<String>identity()
                .andThen(StringToDateFunction.INSTANCE);
    }

    public static Function<String, Map<String, String>> stringToMap() {
        return STRING_TO_MAP;
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    public static <T> Function<T, T> identity() {
        return Function.identity();
    }

    /**
     * Function 将数字转成字符串
     * @param <F> Number
     * @return Function
     */
    @SuppressWarnings("unchecked")
    public static <F extends Number> Function<F, String> numberToString() {
        return (Function<F, String>) NumberToStringFunction.INSTANCE;
    }

    private enum NumberToStringFunction implements Function<Number, String> {
        /**
         * NumberToString Instance
         */
        INSTANCE;

        @Override
        public String apply(Number input) {
            return String.valueOf(input);
        }
    }

    private enum StringToDateFunction implements Function<String, Date>{
        /**
         * Date Parser Instance
         */
        INSTANCE;

        private static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
        private static final String MEDIUM_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

        @Override
        public Date apply(String input) {
            return parse(input);
        }

        private Date parse(String input) throws ParserException {
            input = input.trim();
            int length = input.length();

            if (length == LONG_DATE_FORMAT.length()) {
                return parse(input, LONG_DATE_FORMAT);
            }

            if (length == MEDIUM_DATE_FORMAT.length()) {
                return parse(input, MEDIUM_DATE_FORMAT);
            }

            return parse(input, SHORT_DATE_FORMAT);
        }

        private Date parse(String input, String format) throws ParserException {
            return DateTimeFormat
                    .forPattern(format)
                    .withLocale(Locale.US)
                    .parseDateTime(input)
                    .toDate();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Function<String, Date> s = String_to_date_with_format.apply("yyyy-MM-dd");
            System.out.println(s.apply("2017-09-21"));
        }
    }

    static Function<String, Function<String, Date>> String_to_date_with_format = format -> dateStr -> DateUtils.parse(dateStr, format);
}
