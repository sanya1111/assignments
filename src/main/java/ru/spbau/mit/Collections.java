package ru.spbau.mit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/*
 * Use Collection instead of Iterable because inner Iterator interface doen't implement write function 
 * (only remove and read)
 */
public class Collections {
    public static <T, R> void map(Function1<? super T, ? extends R> func,
            Iterable<T> collection, Collection<R> result) {
        for (T obj : collection) {
            result.add(func.run(obj));
        }
    }

    public static <T, R, C extends Collection<R> > C map(
            Function1<? super T, ? extends R> func, Iterable<T> collection,
            Class<C> clazz) throws InstantiationException,
            IllegalAccessException {
        C result = clazz.newInstance();
        map(func, collection, result);
        return result;
    }

    public static <T> void filter(Predicate<? super T> predicate,
            Iterable<T> collection, Collection<T> result) {
        for (T obj : collection) {
            if (predicate.run(obj).equals(true)) {
                result.add(obj);
            }
        }
    }

    public static <T, C extends Collection<T>> C filter(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz) throws InstantiationException,
            IllegalAccessException {
        C result = clazz.newInstance();
        filter(predicate, collection, result);
        return result;
    }

    private static <T> void takeWhileBoolean(Predicate<? super T> predicate,
            Iterable<T> collection, Collection<T> result, boolean bool) {
        for (T obj : collection) {
            if (!predicate.run(obj).equals(bool)) {
                break;
            }
            result.add(obj);
        }
    }

    public static <T, C extends Collection<T>> void takeWhile(
            Predicate<? super T> predicate, Iterable<T> collection,
            Collection<T> result) throws InstantiationException,
            IllegalAccessException {
        takeWhileBoolean(predicate, collection, result, true);
    }

    public static <T, C extends Collection<T>> C takeWhile(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz) throws InstantiationException,
            IllegalAccessException {
        C result = clazz.newInstance();
        takeWhile(predicate, collection, result);
        return result;
    }

    public static <T, C extends Collection<T>> void takeUnless(
            Predicate<? super T> predicate, Iterable<T> collection,
            Collection<T> result) throws InstantiationException,
            IllegalAccessException {
        takeWhileBoolean(predicate, collection, result, false);
    }

    public static <T, C extends Collection<T>> C takeUnless(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz) throws InstantiationException,
            IllegalAccessException {
        C result = clazz.newInstance();
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
        Function1<Iterator<L>, R> recursion = new Function1<Iterator<L>, R>() {
            @Override
            public R run(Iterator<L> inputFirst) {
                if (!inputFirst.hasNext()) {
                    return value;
                }
                L next = inputFirst.next();
                return func.run(next, run(inputFirst));
            }
        };
        return recursion.run(collection.iterator());
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
