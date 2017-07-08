package com.example.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Created by qianliao.zhuang on 2017/7/8.
 */
public class CollectionUtils {
    private CollectionUtils(){}

    public static <F, T> Iterable<T> map(final Iterable<F> fromIterable, final Function<? super F, ? extends T> function) {
        return Iterables.transform(fromIterable, function);
    }


    public static <T> Iterable<T> nullFilter(final Iterable<T> unfiltered) {
        return filter(unfiltered, Predicates.notNull());
    }

    public static <T> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<? super T> predicate) {
        return Iterables.filter(unfiltered, predicate);
    }
}
