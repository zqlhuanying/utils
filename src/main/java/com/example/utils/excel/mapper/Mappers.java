package com.example.utils.excel.mapper;

import com.example.utils.excel.enums.PoiCellStyle;
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
    private static final Map<String, Mapper> COLUMN_MAPPER_MAPPING = Maps.newHashMap();

    public static <T> void registry(Mapper<T> mapper) {
        registry(mapper.getColumnIndex(), mapper.getColumnName(),
                mapper.getField(), mapper.getClazz(), mapper.getCellStyle());
    }

    public static <T> void registry(int columnIndex, String columnName,
                                    String fieldName, Class<T> type, List<PoiCellStyle> cellStyle) {
        String columnKey = generateKey(Integer.toString(columnIndex), type);
        String fieldKey = generateKey(fieldName, type);
        COLUMN_FIELD_MAPPING.put(columnKey, fieldKey);
        COLUMN_MAPPER_MAPPING.put(columnKey, new Mapper<>(columnIndex, columnName, fieldName, type).setCellStyle(cellStyle));
    }

    @SuppressWarnings("unchecked")
    public static <T> Mapper<T> getMapper(int columnIndex, Class<T> type) {
        return COLUMN_MAPPER_MAPPING.get(generateKey(Integer.toString(columnIndex), type));
    }

    @SuppressWarnings("unchecked")
    public static <T> Mapper<T> getMapper(String fieldName, Class<T> type) {
        String index = COLUMN_FIELD_MAPPING.inverse().get(generateKey(fieldName, type));
        return COLUMN_MAPPER_MAPPING.get(index);
    }

    private static <T> String generateKey(String index, Class<T> type) {
        List<String> args = Lists.newArrayList(
                type.getName(),
                index
        );
        return KEY_JOINER.join(args);
    }
}
