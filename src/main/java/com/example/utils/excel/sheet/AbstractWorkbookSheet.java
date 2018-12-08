package com.example.utils.excel.sheet;

import com.example.utils.CollectionUtil;
import com.example.utils.excel.option.PoiOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookSheet<T> implements WorkBookSheet<T> {

    protected static final DataFormatter DATA_FORMATTER = new DataFormatter();

    protected Source<?> source;
    protected PoiOptions options;

    @Override
    public Source<?> getSource() {
        return source;
    }

    public PoiOptions getOptions() {
        return options;
    }

    protected static boolean ignoreField(String field, List<String> ignores) {
        return CollectionUtil.isNotEmpty(ignores) && ignores.contains(field);
    }
}
