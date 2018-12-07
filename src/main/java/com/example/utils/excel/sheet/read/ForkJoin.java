package com.example.utils.excel.sheet.read;

import java.util.List;

/**
 * @author zhuangqianliao
 */
public interface ForkJoin<T, R> {

    /**
     * Fork/Join
     * @param start the start index
     * @param end the end index (include)
     * @param type the given type
     * @return {@code List<T>}
     */
    List<T> read(int start, int end, Class<T> type);

    /**
     * If the small task computed failed, get the errors
     * @param start the start index
     * @param end the end index (include)
     * @return the errors between start and end
     */
    List<R> errors(int start, int end);
}
