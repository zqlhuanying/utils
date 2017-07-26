package com.example.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by qianliao.zhuang on 2017/7/8.
 */
public class CollectionUtils {
    private CollectionUtils(){}

    public static <F, T> Iterable<T> map(final Iterable<F> fromIterable, final Function<F, T> function) {
        return FluentIterable
                .from(fromIterable)
                .transform(function);
    }

    public static <F, T> List<T> mapList(final List<F> list, final Function<F, T> function) {
        return toList(map(list, function));
    }

    public static <T> List<T> toList(final Iterable<T> iterable){
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

    public static <T> List<T> subList(final List<T> list, final int limitSize){
        return toList(sub(list, limitSize));
    }

    public static <T> List<T> subList(final List<T> list, final int offset, final int limitSize){
        return toList(sub(list, offset, limitSize));
    }

    public static <T> Iterable<T> sub(final Iterable<T> iterable, final int limitSize){
        return sub(iterable, 0, limitSize);
    }

    public static <T> Iterable<T> sub(final Iterable<T> iterable, final int offset, final int limitSize){
        return FluentIterable
                .from(iterable)
                .skip(offset)
                .limit(limitSize);
    }

    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int size){
        return FluentIterable
                .from(Iterables.partition(iterable, size));
    }

    public static <F, T> Iterable<T> flatten(final Iterable<F>... flattenIterable){
        return flatten(Iterables.concat(flattenIterable));
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Iterable<T> flatten(final Iterable<F> flattenIterable){
        Iterable<F> iterable = new FluentIterable<F>(){
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

    private static class FlattenIterator<T> extends AbstractIterator<T>{
        private Stack<Iterator> stack = new Stack<>();

        public FlattenIterator(Iterator<T> iterator) {
            stack.add(iterator);
        }

        @Override
        protected T computeNext() {
            while (!stack.isEmpty()){
                Iterator<T> it = stack.peek();
                if(it.hasNext()){
                    T result = it.next();
                    if(result instanceof Iterable){
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
