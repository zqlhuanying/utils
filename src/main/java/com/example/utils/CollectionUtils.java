package com.example.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import java.util.Iterator;
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

    public static <T> Iterable<T> nullFilter(final Iterable<T> unfiltered) {
        return filter(unfiltered, Predicates.notNull());
    }

    public static <T> Iterable<T> filter(final Iterable<T> unfiltered, final Predicate<? super T> predicate) {
        return FluentIterable
                .from(unfiltered)
                .filter(predicate);
    }

    public static <T> Iterable<T> flatten(final Iterable<T>... flattenIterable){
        return flatten(Iterables.concat(flattenIterable));
    }

    public static <T> Iterable<T> flatten(final Iterable<T> flattenIterable){
        return new FluentIterable<T>(){
            @Override
            public Iterator<T> iterator() {
                return new FlattenIterator<>(flattenIterable.iterator());
            }
        };
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
