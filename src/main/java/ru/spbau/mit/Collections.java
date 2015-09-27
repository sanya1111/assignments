package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;


/*
 * Use Collection instead of Iterable because inner Iterator interface doen't implement write function 
 * (only remove and read)
 */
public class Collections {

    public static class CollectionsException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }

    private static <C extends Collection<?>> C createInstanceCollectionRuntime(
            Class<C> clazz, Class<?>[] argsTypes, Object[] args)
            throws CollectionsException {
        C result = null;
        try {
            result = clazz.getConstructor(argsTypes).newInstance(args);
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new CollectionsException();
        }
        return result;
    }

    public static <T, R> void map(Function1<? super T, ? extends R> func,
            Iterable<T> collection, Collection<R> result) {
        for (T obj : collection) {
            result.add(func.run(obj));
        }
    }

    public static <T, R, C extends Collection<R>> C map(
            Function1<? super T, ? extends R> func, Iterable<T> collection,
            Class<C> clazz, Class<?>[] argsTypes, Object[] args)
            throws CollectionsException {
        C result = createInstanceCollectionRuntime(clazz, argsTypes, args);
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

    public static <T, C extends Collection<T>> C filter(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz, Class<?>[] argsTypes, Object[] args)
            throws CollectionsException {
        C result = createInstanceCollectionRuntime(clazz, argsTypes, args);
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

    public static <T, C extends Collection<T>> C takeWhile(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz, Class<?>[] argsTypes, Object[] args)
            throws CollectionsException {
        C result = createInstanceCollectionRuntime(clazz, argsTypes, args);
        takeWhile(predicate, collection, result);
        return result;
    }

    public static <T, C extends Collection<T>> void takeUnless(
            Predicate<? super T> predicate, Iterable<T> collection,
            Collection<T> result) {
        takeWhile(predicate.not(), collection, result);
    }

    public static <T, C extends Collection<T>> C takeUnless(
            Predicate<? super T> predicate, Iterable<T> collection,
            Class<C> clazz, Class<?>[] argsTypes, Object[] args)
            throws CollectionsException {
        C result = createInstanceCollectionRuntime(clazz, argsTypes, args);
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
