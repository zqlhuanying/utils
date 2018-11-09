package com.example.utils.validation;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangqianliao
 */
@Slf4j
public abstract class AbstractSeparatorValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    protected int maxSize;
    protected Class<? extends Predicate<Object>> predicate;
    private Predicate<Object> predicateInstance;

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(Object, ConstraintValidatorContext)} calls.
     * It will be called only once cause it will be cached by Spring Validator
     * Cache key: {constraintAnnotation, valueType}
     */
    @Override
    public final void initialize(A constraintAnnotation) {
        Separator separator = synthesize(constraintAnnotation);
        this.maxSize = separator.maxSize();
        this.predicate = separator.predicate();
        try {
            this.predicateInstance = Separator.DEFAULT.class.equals(this.predicate) ?
                    null : this.predicate.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("instant predicate instance failed", e);
            throw new RuntimeException("instant predicate instance failed");
        }
        init(constraintAnnotation);
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Iterable<?> iterable = iterable(value);
        if (Iterables.size(iterable) > this.maxSize) {
            return false;
        }

        for (Object o : iterable) {
            if (!predicate(predicateInstance, o)) {
                return false;
            }
        }
        return true;
    }

    protected abstract Iterable<?> iterable(T value);

    /**
     * 初始化字段
     */
    protected abstract void init(A constraintAnnotation);

    protected boolean predicate(Predicate<Object> predicate, Object o) {
        return predicate == null
                || predicate.apply(o);
    }

    private Separator synthesize(A constraintAnnotation) {
        Separator separator = AnnotatedElementUtils.findMergedAnnotation(constraintAnnotation.annotationType(), Separator.class);
        if (separator == null) {
            throw new RuntimeException(
                    "Please use @Separator annotation as meta-annotation for type: " + constraintAnnotation.annotationType().getName()
            );
        }
        AnnotationAttributes metaAttributes = AnnotationUtils.getAnnotationAttributes(separator, false, false);
        AnnotationAttributes constraintAttributes = AnnotationUtils.getAnnotationAttributes(constraintAnnotation, false, false);
        Map<String, String> aliasName = getAliasName(constraintAnnotation);
        overrideAttributes(metaAttributes, constraintAttributes, aliasName);
        return AnnotationUtils.synthesizeAnnotation(metaAttributes, Separator.class, separator.annotationType());
    }

    private Map<String, String> getAliasName(A constraintAnnotation) {
        Map<String, String> alias = new HashMap<>(16);
        for (Method method : constraintAnnotation.annotationType().getDeclaredMethods()) {
            AliasFor aliasFor = AnnotationUtils.findAnnotation(method, AliasFor.class);
            if (aliasFor != null && Separator.class.equals(aliasFor.annotation())) {
                alias.put(method.getName(), aliasFor.value());
            }
        }
        return alias;
    }

    private void overrideAttributes(
            AnnotationAttributes overrideAnnotation,
            AnnotationAttributes sourceAnnotation,
            Map<String, String> alias) {
        for (Map.Entry<String, String> entry : alias.entrySet()) {
            String override = entry.getValue();
            String source = entry.getKey();
            overrideAnnotation.put(override, sourceAnnotation.get(source));
        }
    }
}
