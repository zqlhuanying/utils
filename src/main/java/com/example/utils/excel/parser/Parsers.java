package com.example.utils.excel.parser;

import com.example.utils.DateUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author zhuangqianliao
 */
public final class Parsers {

    private static final Parser<String> STRING_PARSER = new Parser<String>() {
        @Override
        public String parse(String value) {
            return value;
        }

        @Override
        public String deParse(Object value) {
            return (String) value;
        }
    };

    private static final Parser<Date> DATE_PARSER = new Parser<Date>() {
        @Override
        public Date parse(String value) {
            return StringUtils.isBlank(value) ? null : DateUtils.parse(value);
        }

        @Override
        public String deParse(Object value) {
            return value == null ? StringUtils.EMPTY : DateUtils.parse((Date) value);
        }
    };

    private static final Parser<Integer> INT_PARSER = new Parser<Integer>() {
        @Override
        public Integer parse(String value) {
            return StringUtils.isBlank(value) ? null : Integer.parseInt(value);
        }

        @Override
        public String deParse(Object value) {
            return value == null ? StringUtils.EMPTY : Integer.toString((Integer) value);
        }
    };

    private static final Parser<Long> LONG_PARSER = new Parser<Long>() {
        @Override
        public Long parse(String value) {
            return StringUtils.isBlank(value) ? null : Long.parseLong(value);
        }

        @Override
        public String deParse(Object value) {
            return value == null ? StringUtils.EMPTY : Long.toString((Long) value);
        }
    };

    private static final Map<Class, Parser> PARSER_MAP = Maps.newHashMap();
    static {
        PARSER_MAP.put(String.class, STRING_PARSER);
        PARSER_MAP.put(Date.class, DATE_PARSER);
        PARSER_MAP.put(Integer.class, INT_PARSER);
        PARSER_MAP.put(Long.class, LONG_PARSER);
    }

    public static Parser<String> defaultParser() {
        return get(String.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> get(Class<T> clazz) {
        return PARSER_MAP.get(clazz);
    }

    public static Parser<?> getOrDefault(Class<?> clazz, Parser<?> defaultParser) {
        Parser<?> parser = get(clazz);
        return parser == null ? defaultParser : parser;
    }

    public static <T> void put(Class<T> clazz, Parser<T> parser) {
        PARSER_MAP.put(clazz, parser);
    }
}
