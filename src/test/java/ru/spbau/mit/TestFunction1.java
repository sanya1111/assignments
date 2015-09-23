package ru.spbau.mit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import org.junit.Test;


public class TestFunction1 {

    @Test
    public void test() {
        Function1<Integer, ArrayList<Integer>> func = new Function1<Integer, ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> run(Integer input) {
                return new ArrayList<Integer>(
                        Arrays.asList(input, input, input));

            }
        };
        int value = 2;
        assertTrue((new ArrayList<Integer>(Arrays.asList(value, value, value)))
                .equals(func.run(value)));
    }

    @Test
    public void testCompose() {
        Function1<Integer, List<Integer>> func1 = new Function1<Integer, List<Integer>>() {
            @Override
            public List<Integer> run(Integer input) {
                return Arrays.asList(input, input, input);
            }
        };
        Function1<Collection<Integer>, ArrayList<Integer>> func2 = new Function1<Collection<Integer>, ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> run(Collection<Integer> input) {
                return new ArrayList<Integer>(input);
            }
        };
        Function1<Integer, ArrayList<Integer>> result = func1.compose(func2);
        int value = 3;
        assertTrue((new ArrayList<Integer>(Arrays.asList(value, value, value)))
                .equals(result.run(value)));
    }
}
