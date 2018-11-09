package com.example.utils.validation;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zhuangqianliao
 */
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Separator {

    /**
     * 分隔后的单个对象回调逻辑
     */
    Class<? extends Predicate<Object>> predicate() default DEFAULT.class;

    /**
     * 分隔后的数组大小
     */
    int maxSize() default Integer.MAX_VALUE;

    class DEFAULT implements Predicate<Object> {
        @Override
        public boolean apply(@Nullable Object input) {
            return true;
        }
    }
}
