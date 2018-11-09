package com.example.utils.validation;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class SimpleSeparatorValidator extends AbstractSeparatorValidator<SimpleSeparator, CharSequence> {

    private static final String DEFAULT_SEPARATOR = "/";
    private static final Splitter DEFAULT_SPLITTER = Splitter.on(DEFAULT_SEPARATOR);

    private SimpleSeparator constraintAnnotation;
    private String separator;
    private Pattern pattern;
    private Splitter splitter;

    @Override
    protected void init(SimpleSeparator constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
        this.separator = this.constraintAnnotation.separator();
        this.pattern = StringUtils.isBlank(this.constraintAnnotation.pattern()) ?
                null : Pattern.compile(this.constraintAnnotation.pattern());
        this.splitter = DEFAULT_SEPARATOR.equals(this.separator) ?
                DEFAULT_SPLITTER : Splitter.on(this.separator);
    }

    @Override
    protected Iterable<?> iterable(CharSequence value) {
        return this.splitter.split(value);
    }

    @Override
    protected boolean predicate(Predicate<Object> predicate, Object o) {
        if (this.pattern != null
                && !this.pattern.matcher((String) o).matches()) {
            return false;
        }
        return super.predicate(predicate, o);
    }
}
