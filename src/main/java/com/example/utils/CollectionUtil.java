package com.example.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * @author qianliao.zhuang
 */
public final class CollectionUtil {

    private CollectionUtil() {}

    public static <F, T> Iterable<T> map(final Iterable<F> fromIterable, final Function<F, T> function) {
        return FluentIterable
                .from(fromIterable)
                .transform(function);
    }

    public static <F, T> List<T> mapList(final List<F> list, final Function<F, T> function) {
        return toList(map(list, function));
    }

    public static <T> List<T> toList(final Iterable<T> iterable) {
        return Lists.newArrayList(iterable);
    }

    public static <T> Iterable<T> nullFilter(final Iterable<T> unfiltered) {
        return filter(unfiltered, Predicates.notNull());
    }

    public static <T> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<? super T> predicate) {
        return FluentIterable
                .from(unfiltered)
                .filter(predicate);
    }

    public static <T> List<T> subList(final List<T> list, final int offset, final int limitSize) {
        return toList(
                sub(list, offset, limitSize)
        );
    }

    public static <T> Iterable<Iterable<T>> subCycle(final Iterable<T> iterable, final int limitSize) {
        return new FluentIterable<Iterable<T>>() {
            @Override
            public Iterator<Iterable<T>> iterator() {
                return new SubIterator<>(iterable, limitSize);
            }
        };
    }

    public static <T> Iterable<T> sub(final Iterable<T> iterable, final int offset, final int limitSize) {
        return FluentIterable
                .from(iterable)
                .skip(offset)
                .limit(limitSize);
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int size) {
        return FluentIterable
                .from(Iterables.partition(iterable, size));
    }

    public static <F, T> Iterable<T> flatten(final Iterable<F>... flattenIterable) {
        return flatten(Iterables.concat(flattenIterable));
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Iterable<T> flatten(final Iterable<F> flattenIterable) {
        Iterable<F> iterable = new FluentIterable<F>() {
            @Override
            public Iterator<F> iterator() {
                return new FlattenIterator<>(flattenIterable.iterator());
            }
        };
        return Iterables.transform(iterable, new Function<F, T>() {
            @Override
            public T apply(F input) {
                return (T) input;
            }
        });
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

    private static class FlattenIterator<T> extends AbstractIterator<T> {
        private Stack<Iterator> stack = new Stack<>();

        FlattenIterator(Iterator<T> iterator) {
            stack.add(iterator);
        }

        @Override
        protected T computeNext() {
            while (!stack.isEmpty()) {
                Iterator<T> it = stack.peek();
                if (it.hasNext()) {
                    T result = it.next();
                    if (result instanceof Iterable) {
                        stack.add(((Iterable) result).iterator());
                        result = computeNext();
                    }
                    return result;
                }
                stack.pop();
            }
            return endOfData();
        }
    }
}
