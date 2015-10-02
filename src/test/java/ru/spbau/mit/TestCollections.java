package ru.spbau.mit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestCollections {

    @Test
    public void testFilter() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public Boolean run(Integer input) {
                return input > 3;
            }
        };
        List<Integer> target = Arrays.asList(4, 5, 6, 7);
        assertEquals(target, Collections.filter(predicate, a));
        ArrayList<Integer> res = new ArrayList<Integer>();
        Collections.filter(predicate, a, res);
        assertEquals(target, res);
    }

    @Test
    public void testFoldl() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4);
        Function2<String, Integer, String> func = new Function2<String, Integer, String>() {

            @Override
            public String run(String inputFirst, Integer inputSecond) {
                StringBuilder builder = new StringBuilder(inputFirst);
                for (int i = 0; i < inputSecond; i++) {
                    builder.append((char) (i + 'a'));
                }
                return builder.toString();
            }
        };
        assertEquals("BEGINS aababcabcd", Collections.foldl(func, "BEGINS ", a));
    }

    @Test
    public void testFoldr() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4);
        Function2<Integer, String, String> func = new Function2<Integer, String, String>() {

            @Override
            public String run(Integer inputFirst, String inputSecond) {
                StringBuilder builder = new StringBuilder(inputSecond);
                for (int i = 0; i < inputFirst; i++) {
                    builder.append((char) (i + 'a'));
                }
                return builder.toString();
            }
        };
        assertEquals("BEGINS abcdabcaba", Collections.foldr(func, "BEGINS ", a));
        assertEquals("BEGINS abcdabcaba",
                Collections.foldrNotRecursive(func, "BEGINS ", a));
    }

    @Test
    public void testMap() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4);
        Function1<Integer, String> func = new Function1<Integer, String>() {

            @Override
            public String run(Integer inputFirst) {
                StringBuilder builder = new StringBuilder("(");
                for (int i = 0; i < inputFirst; i++) {
                    builder.append("a");
                }
                builder.append(")");
                return builder.toString();
            }
        };
        List<String> target = Arrays.asList("(a)", "(aa)", "(aaa)", "(aaaa)");
        assertEquals(target, Collections.map(func, a));
        ArrayList<String> result = new ArrayList<String>();
        Collections.map(func, a, result);
        assertEquals(target, result);
    }

    @Test
    public void testUnless() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4);
        Predicate<Integer> func = new Predicate<Integer>() {

            @Override
            public Boolean run(Integer input) {
                return input > 3;
            }
        };
        List<Integer> target = Arrays.asList(1, 2, 3);
        assertEquals(target, Collections.takeUnless(func, a));
        ArrayList<Integer> result = new ArrayList<Integer>();
        Collections.takeUnless(func, a, result);
        assertEquals(target, result);
    }

    @Test
    public void testWhile() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4);
        Predicate<Integer> func = new Predicate<Integer>() {

            @Override
            public Boolean run(Integer input) {
                return input <= 3;
            }
        };
        List<Integer> target = Arrays.asList(1, 2, 3);
        assertEquals(target, Collections.takeWhile(func, a));
        ArrayList<Integer> result = new ArrayList<Integer>();
        Collections.takeWhile(func, a, result);
        assertEquals(target, result);
    }
}
