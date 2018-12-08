package com.example.utils.excel.sheet;

import com.example.utils.CollectionUtil;
import com.example.utils.excel.mapper.Mapper;
import com.example.utils.excel.option.PoiOptions;
import com.example.utils.excel.parser.Parser;
import com.example.utils.excel.parser.Parsers;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.util.List;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractWorkbookSheet<T> implements WorkbookSheet<T> {

    protected static final DataFormatter DATA_FORMATTER = new DataFormatter();

    protected Source<?> source;
    protected PoiOptions options;

    @Override
    public Source<?> getSource() {
        return source;
    }

    @Override
    public PoiOptions getOptions() {
        return options;
    }

    protected void writeToInstance(Mapper<T> mapper, String value, T instance) {
        if (!ignoreField(mapper.getField(), getOptions().getIgnoreFields())) {
            Parser<?> parser = Parsers.getOrDefault(
                    mapper.getWriteMethodType().parameterType(0),
                    Parsers.defaultParser()
            );

            BeanUtils.doInvoke(
                    instance.getClass(), mapper.getWriteMethodName(), mapper.getWriteMethodType(),
                    instance, parser.parse(value)
            );
        }
    }

    private boolean ignoreField(String field, List<String> ignores) {
        return CollectionUtil.isNotEmpty(ignores) && ignores.contains(field);
    }
}
