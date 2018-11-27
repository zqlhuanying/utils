package com.example.utils.java8;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author qianliao.zhuang
 */
public final class CollectionUtil {

    private CollectionUtil() {}

    public static <T> Stream<T> of(final Iterable<T> fromIterable) {
        return of(fromIterable, false);
    }

    public static <T> Stream<T> of(final Iterable<T> fromIterable, boolean parallel) {
        return StreamSupport.stream(fromIterable.spliterator(), parallel);
    }

    public static <T, R> Iterable<R> map(final Iterable<T> fromIterable, Function<? super T, ? extends R> function) {
        return of(fromIterable)
                .map(function)
                .collect(Collectors.toList());
    }

    public static <T, R> List<R> mapList(final Iterable<T> fromIterable, Function<? super T, ? extends R> function) {
        return of(fromIterable)
                .map(function)
                .collect(Collectors.toList());
    }

    public static <T> Iterable<T> nullFilter(final Iterable<T> unfiltered) {
        return filter(unfiltered, Objects::nonNull);
    }

    public static <T> Iterable<T> filter(final Iterable<T> unfiltered, Predicate<? super T> predicate) {
        return of(unfiltered)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static <T> Iterable<T> sub(final Iterable<T> iterable, final int offset, final int limitSize) {
        return of(iterable)
                .skip(offset)
                .limit(limitSize)
                .collect(Collectors.toList());
    }

    public static <T> List<T> subList(final Iterable<T> iterable, final int offset, final int limitSize) {
        return of(iterable)
                .skip(offset)
                .limit(limitSize)
                .collect(Collectors.toList());
    }

    public static <T> Iterable<Iterable<T>> subCycle(final Iterable<T> iterable, final int size) {
        return new FluentIterable<Iterable<T>>() {
            @Override
            public Iterator<Iterable<T>> iterator() {
                return new SubIterator<>(iterable, size);
            }
        };
    }

    public static <T, R> Iterable<R> subCycle(
            final Iterable<T> iterable,
            final int size,
            Function<? super Iterable<T>, ? extends Iterable<R>> function) {
        Iterable<Iterable<T>> partOf = subCycle(iterable, size);
        return of(partOf)
                .map(function::apply)
                .flatMap(CollectionUtil::of)
                .collect(Collectors.toList());
    }

    public static <T> void subCycle(
            final Iterable<T> iterable,
            final int size,
            Consumer<? super Iterable<T>> consumer) {
        Iterable<Iterable<T>> partOf = subCycle(iterable, size);
        of(partOf).forEach(consumer::accept);
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return of(iterable)
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    public static <T> Iterable<T> flatten(final Iterable<Iterable<T>>... flattenIterable) {
        return Stream.of(flattenIterable)
                .flatMap(CollectionUtil::of)
                .flatMap(CollectionUtil::of)
                .collect(Collectors.toList());
    }

    public static <T> Iterable<T> flatten(final Iterable<Iterable<T>> flattenIterable) {
        return of(flattenIterable)
                .flatMap(CollectionUtil::of)
                .collect(Collectors.toList());
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return iterable == null || !iterable.iterator().hasNext();
    }

    public static boolean isNotEmpty(Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    public static <T> Iterable<T> safeNull(final Iterable<T> iterable) {
        return iterable != null ? iterable : emptyIterable();
    }

    public static <T> T getOrDefault(Iterable<T> iterable, int position, T defaultT) {
        return Iterables.get(safeNull(iterable), position, defaultT);
    }

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> emptyIterable() {
        return (Iterable<T>) EMPTY_ITERABLE;
    }

    private static Iterable<Object> EMPTY_ITERABLE = new FluentIterable<Object>() {
        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyIterator();
        }
    };

    private static class SubIterator<T> extends AbstractIterator<Iterable<T>> {
        private final Iterable<T> iterable;
        private final int perSize;
        private int index;

        SubIterator(Iterable<T> iterable, int perSize) {
            this.iterable = iterable;
            this.perSize = perSize;
            this.index = 0;
        }

        @Override
        protected Iterable<T> computeNext() {
            Iterable<T> sub = sub(iterable, index, perSize);
            if (sub.iterator().hasNext()) {
                index += perSize;
                return sub;
            }
            return endOfData();
        }
    }
}
