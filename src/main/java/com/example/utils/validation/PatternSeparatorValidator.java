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
public class PatternSeparatorValidator extends AbstractSeparatorValidator<PatternSeparator, CharSequence> {

    private PatternSeparator constraintAnnotation;
    private String separator;
    private Pattern separatorPattern;
    private Pattern pattern;
    private Splitter splitter;

    @Override
    protected void init(PatternSeparator constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
        this.separator = this.constraintAnnotation.separator();
        this.separatorPattern = Pattern.compile(this.separator);
        this.pattern = StringUtils.isBlank(this.constraintAnnotation.pattern()) ?
                null : Pattern.compile(this.constraintAnnotation.pattern());
        this.splitter = Splitter.on(this.separatorPattern);
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
