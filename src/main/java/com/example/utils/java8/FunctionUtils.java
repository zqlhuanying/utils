package com.example.utils.java8;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qianliao.zhuang
 */
public final class FunctionUtils {
    private FunctionUtils() {}

    private static final Function<String, Short> STRING_TO_SHORT_FUNCTION = Short::parseShort;

    private static final Function<String, Integer> STRING_TO_INTEGER_FUNCTION = Integer::parseInt;

    private static final Function<String, Long> STRING_TO_LONG_FUNCTION = Long::parseLong;

    private static final Function<String, Boolean> STRING_TO_BOOLEAN_FUNCTION = Boolean::valueOf;

    private static final Function<String, Date> STRING_TO_DATE_FUNCTION = StringToDateFunction.INSTANCE;

    private static final BiFunction<String, String, Date> STRING_TO_DATE_WITH_FORMAT = StringToDateFunction.INSTANCE::parse;

    private static final Function<String, Map<String, String>> STRING_TO_MAP = StringToMapFunction.INSTANCE;

    private static final BiFunction<String, Splitter.MapSplitter, Map<String, String>> STRING_TO_MAP_WITH_SPLITTER = StringToMapFunction.INSTANCE::parse;

    public static Function<String, Short> stringToShort() {
        return STRING_TO_SHORT_FUNCTION;
    }

    public static Function<String, Integer> stringToInteger() {
        return STRING_TO_INTEGER_FUNCTION;
    }

    public static Function<String, Long> stringToLong() {
        return STRING_TO_LONG_FUNCTION;
    }

    public static Function<String, Boolean> stringToBoolean() {
        return STRING_TO_BOOLEAN_FUNCTION;
    }

    public static Function<String, Date> stringToDate() {
        return STRING_TO_DATE_FUNCTION;
    }

    /**
     * Curry Function by using lambda
     * Every time return a new anonymous function
     * if mind, please use {@code stringToDateWithFormat()}
     * @param format the formatter
     * @return Function<String, Date>
     */
    public static Function<String, Date> stringToDateWithFormat(String format) {
        return (dateStr) -> StringToDateFunction.INSTANCE.parse(dateStr, format);
    }

    public static BiFunction<String, String, Date> stringToDateWithFormat() {
        return STRING_TO_DATE_WITH_FORMAT;
    }

    public static Function<String, Map<String, String>> stringToMap() {
        return STRING_TO_MAP;
    }

    /**
     * Curry Function by using lambda
     * Every time return a new anonymous function
     * if mind, please use {@code stringToMapWithSplitter()}
     * @param splitter the splitter used
     * @return Function<String, Map<String, String>
     */
    public static Function<String, Map<String, String>> stringToMapWithSplitter(Splitter.MapSplitter splitter) {
        return str -> StringToMapFunction.INSTANCE.parse(str, splitter);
    }

    public static BiFunction<String, Splitter.MapSplitter, Map<String, String>> stringToMapWithSplitter() {
        return STRING_TO_MAP_WITH_SPLITTER;
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
        private static final List<String> ALL_DATE_FORMAT = Lists.newArrayList(
                LONG_DATE_FORMAT, MEDIUM_DATE_FORMAT, SHORT_DATE_FORMAT
        );

        @Override
        public Date apply(String input) {
            return parse(input);
        }

        public Date parse(String input) {
            String input0 = input.trim();
            int length = input0.length();
            return ALL_DATE_FORMAT.stream()
                    .filter(format -> format.length() == length)
                    .map(format -> parse(input0, format))
                    .collect(Collectors.toList())
                    .get(0);
        }

        public Date parse(String input, String format) {
            return DateTimeFormat
                    .forPattern(format)
                    .withLocale(Locale.US)
                    .parseDateTime(input)
                    .toDate();
        }
    }

    private enum StringToMapFunction implements Function<String, Map<String, String>>{
        /**
         * Map Parser Instance
         */
        INSTANCE;

        private static final Splitter.MapSplitter SPLITTER =
                Splitter.on(",")
                .omitEmptyStrings()
                .withKeyValueSeparator(":");

        @Override
        public Map<String, String> apply(String input) {
            return parse(input, SPLITTER);
        }

        public Map<String, String> parse(String input, Splitter.MapSplitter splitter) {
            return splitter.split(input);
        }
    }
}
