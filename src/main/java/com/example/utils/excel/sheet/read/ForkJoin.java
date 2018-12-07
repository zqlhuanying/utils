package com.example.utils.excel.sheet.read;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface ForkJoin<T> {

    /**
     * Fork/Join
     * @param start the start index
     * @param end the end index (include)
     * @param type the given type
     * @return {@code List<T>}
     */
    List<T> read(int start, int end, Class<T> type);
}
