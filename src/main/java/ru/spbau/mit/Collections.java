package ru.spbau.mit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public class Collections {

    public static <T, R> void map(Function1<? super T, ? extends R> func,
            Iterable<T> collection, Collection<R> result) {
        for (T obj : collection) {
            result.add(func.run(obj));
        }
    }

    public static <T, R> Iterable<R> map(
            Function1<? super T, ? extends R> func, Iterable<T> collection) {
        ArrayList<R> result = new ArrayList<R>();
        map(func, collection, result);
        return result;
    }

    public static <T> void filter(Predicate<? super T> predicate,
            Iterable<T> collection, Collection<T> result) {
        for (T obj : collection) {
            if (predicate.run(obj)) {
                result.add(obj);
            }
        }
    }

    public static <T> Iterable<T> filter(Predicate<? super T> predicate,
            Iterable<T> collection) {
        ArrayList<T> result = new ArrayList<T>();
        filter(predicate, collection, result);
        return result;
    }

    public static <T, C extends Collection<T>> void takeWhile(
            Predicate<? super T> predicate, Iterable<T> collection,
            Collection<T> result) {
        for (T obj : collection) {
            if (!predicate.run(obj)) {
                break;
            }
            result.add(obj);
        }
    }

    public static <T> Iterable<T> takeWhile(Predicate<? super T> predicate,
            Iterable<T> collection) {
        ArrayList<T> result = new ArrayList<T>();
        takeWhile(predicate, collection, result);
        return result;
    }

    public static <T, C extends Collection<T>> void takeUnless(
            Predicate<? super T> predicate, Iterable<T> collection,
            Collection<T> result) {
        takeWhile(predicate.not(), collection, result);
    }

    public static <T> Iterable<T> takeUnless(Predicate<? super T> predicate,
            Iterable<T> collection) {
        ArrayList<T> result = new ArrayList<T>();
        takeUnless(predicate, collection, result);
        return result;
    }

    public static <L, R> L foldl(
            Function2<? super L, ? super R, ? extends L> func, L value,
            Iterable<R> collection) {
        for (R obj : collection) {
            value = func.run(value, obj);
        }
        return value;
    }

    public static <L, R> R foldr(
            final Function2<? super L, ? super R, ? extends R> func,
            final R value, Iterable<L> collection) {
        return Function1.runRecursion(new Function1<Function1<Iterator<L>, R>, Function1<Iterator<L>, R>>() {

            @Override
            public Function1<Iterator<L>, R> run(
                    final Function1<Iterator<L>, R> input) {
                return new Function1<Iterator<L>, R>() {
                    @Override
                    public R run(Iterator<L> inputFirst) {
                        if (!inputFirst.hasNext()) {
                            return value;
                        }
                        L next = inputFirst.next();
                        return func.run(next, input.run(inputFirst));
                    }
                };
            }

        }, collection.iterator());
    }

    public static <L, R> R foldrNotRecursive(
            Function2<? super L, ? super R, ? extends R> func, R value,
            Collection<L> collection) {
        ArrayList<L> list = new ArrayList<L>(collection);
        ListIterator<L> iterator = list.listIterator(list.size());
        while (iterator.hasPrevious()) {
            value = func.run(iterator.previous(), value);
        }
        return value;
    }
}
