package ru.spbau.mit;

import org.junit.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public abstract class TestsBase extends Assert {

    protected Random random = new Random();

    protected TreeSetImpl<Integer> buildSet() {
        return new TreeSetImpl<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }

            @Override
            public Comparator<Integer> reversed() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Comparator<Integer> thenComparing(
                    Comparator<? super Integer> other) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <U> Comparator<Integer> thenComparing(
                    Function<? super Integer, ? extends U> keyExtractor,
                    Comparator<? super U> keyComparator) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <U extends Comparable<? super U>> Comparator<Integer> thenComparing(
                    Function<? super Integer, ? extends U> keyExtractor) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Comparator<Integer> thenComparingInt(
                    ToIntFunction<? super Integer> keyExtractor) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Comparator<Integer> thenComparingLong(
                    ToLongFunction<? super Integer> keyExtractor) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Comparator<Integer> thenComparingDouble(
                    ToDoubleFunction<? super Integer> keyExtractor) {
                // TODO Auto-generated method stub
                return null;
            }


            
        });
    }
}
