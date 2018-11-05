package com.example.utils.excel.mapper;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author zhuangqianliao
 */
public final class Mappers {
    private static final Joiner KEY_JOINER = Joiner.on("#");
    private static final BiMap<String, String> COLUMN_FIELD_MAPPING = HashBiMap.create();
    private static final Map<String, Mapper<?>> COLUMN_MAPPER_MAPPING = Maps.newHashMap();

    public static void registry(Mapper mapper) {
        registry(mapper.getColumnIndex(), mapper.getColumnName(),
                mapper.getField(), mapper.getClazz());
    }

    public static void registry(int columnIndex, String columnName,
                                String fieldName, Class<?> type) {
        String columnKey = generateKey(Integer.toString(columnIndex), type);
        String fieldKey = generateKey(fieldName, type);
        COLUMN_FIELD_MAPPING.put(columnKey, fieldKey);
        COLUMN_MAPPER_MAPPING.put(columnKey, new Mapper<>(columnIndex, columnName, fieldName, type));
    }

    public static Mapper<?> getMapper(int columnIndex, Class<?> type) {
        return COLUMN_MAPPER_MAPPING.get(generateKey(Integer.toString(columnIndex), type));
    }

    public static Mapper<?> getMapper(String fieldName, Class<?> type) {
        String index = COLUMN_FIELD_MAPPING.inverse().get(generateKey(fieldName, type));
        return COLUMN_MAPPER_MAPPING.get(index);
    }

    private static String generateKey(String index, Class<?> type) {
        List<String> args = Lists.newArrayList(
                type.getName(),
                index
        );
        return KEY_JOINER.join(args);
    }
}
