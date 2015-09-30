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

        });
    }
}
