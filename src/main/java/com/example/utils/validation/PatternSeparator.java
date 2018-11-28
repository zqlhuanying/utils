package com.example.utils.validation;

import com.google.common.base.Predicate;
import org.springframework.core.annotation.AliasFor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zhuangqianliao
 * 分隔符校验
 * 1. 先用分隔符对字符串分隔
 * 2. 对分隔后的每个字符串依次进行校验
 * 3. 所有校验通过后，校验器才通过
 */
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {PatternSeparatorValidator.class})
@Separator
public @interface PatternSeparator {

    /**
     * 分隔后的字符串校验规则
     */
    String pattern() default "";

    /**
     * 分隔后的字符串回调逻辑
     */
    @AliasFor(annotation = Separator.class, value = "predicate")
    Class<? extends Predicate<Object>> predicate() default Separator.DEFAULT.class;

    /**
     * 分隔后的数组大小
     */
    @AliasFor(annotation = Separator.class, value = "maxSize")
    int maxSize() default Integer.MAX_VALUE;

    /**
     * 分隔符
     */
    String separator() default "/";

    String message() default "{com.jd.galaxy.validation.annotation.Separator.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({FIELD, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @interface List {
        PatternSeparator[] value();
    }
}
