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
        Function1<Integer, List<Integer>> func = new Function1<Integer, List<Integer>>() {
            @Override
            public List<Integer> run(Integer input) {
                return Arrays.asList(input, input, input);

            }
        };
        int value = 2;
        assertTrue(Arrays.asList(value, value, value).equals(func.run(value)));
    }

    @Test
    public void testCompose() {
        Function1<Integer, List<Integer>> func1 = new Function1<Integer, List<Integer>>() {
            @Override
            public List<Integer> run(Integer input) {
                return Arrays.asList(input, input, input);
            }
        };
        Function1<Collection<Integer>, ArrayList<String>> func2 = new Function1<Collection<Integer>, ArrayList<String>>() {
            @Override
            public ArrayList<String> run(Collection<Integer> input) {
                ArrayList<String> result = new ArrayList<String>();
                for (Integer obj : input) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < obj; i++) {
                        builder.append("a");
                    }
                    result.add(builder.toString());
                }
                return result;
            }
        };
        Function1<Integer, ArrayList<String>> result = func1.compose(func2);
        int value = 3;
        assertTrue(Arrays.asList("aaa", "aaa", "aaa").equals(result.run(value)));
    }
    
    @Test
    public void testRecursion(){
        Function1<Function1<Integer, Integer>, Function1<Integer, Integer> > rec = new Function1<Function1<Integer, Integer>, Function1<Integer, Integer>>(){

            @Override
            public Function1<Integer, Integer> run(final Function1<Integer, Integer> input) {
                return new Function1<Integer, Integer>(){

                    @Override
                    public Integer run(Integer inputValue) {
                        if (inputValue < 2)
                            return 1;
                        return inputValue * input.run(inputValue - 1);
                    }
                    
                };
            }
            
        };
        assertEquals(120, rec.runRecursion(5));
    }
}
