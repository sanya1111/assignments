package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestPredicate {

    @Test
    public void test() {
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 10;
            }
        };
        assertTrue(predicate.run(4292));
        assertFalse(predicate.run(4));
    }

    @Test
    public void testAnd() {
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 10;
            }
        };
        Predicate<Integer> predicate2 = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input % 2 == 0;
            }
        };
        Predicate<Integer> result = predicate.and(predicate2);
        assertFalse(result.run(15));
        assertFalse(result.run(2));
        assertTrue(result.run(16));
        assertFalse(result.run(3));
    }

    @Test
    public void testOr() {
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 10;
            }
        };
        Predicate<Integer> predicate2 = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input % 2 == 0;
            }
        };
        Predicate<Integer> result = predicate.or(predicate2);
        assertTrue(result.run(15));
        assertTrue(result.run(2));
        assertTrue(result.run(16));
        assertFalse(result.run(3));
    }

    @Test
    public void testLazy() {
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 10;
            }
        };
        Predicate<Integer> predicate2 = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                fail();
                return true;
            }
        };
        Predicate<Integer> result = predicate.and(predicate2);
        result.run(1);
        result = predicate.or(predicate2);
        result.run(11);
    }

    @Test
    public void testNot() {
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 10;
            }
        };
        Predicate<Integer> result = predicate.not();
        assertFalse(result.run(15));
        assertTrue(result.run(2));
        assertTrue(result.run(10));
    }

    @Test
    public void testConsts() {
        assertTrue((Predicate.ALWAYS_TRUE).run(0));
        assertFalse((Predicate.ALWAYS_FALSE).run(0));
    }
}
