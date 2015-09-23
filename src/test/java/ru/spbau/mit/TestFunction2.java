package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestFunction2 {

    @Test
    public void test() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            
            @Override
            public Integer run(Integer inputFirst, Integer inputSecond) {
                return inputFirst + inputSecond;
            }
        };
        assertEquals(func.run(1 , 2), Integer.valueOf(3));
    }
    
    @Test
    public void testCompose() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            
            @Override
            public Integer run(Integer inputFirst, Integer inputSecond) {
                return inputFirst + inputSecond;
            }
        };
        
        Function1<Integer, Integer> func2 = new Function1<Integer, Integer>() {
            
            @Override
            public Integer run(Integer input) {
                return -input;
            }
        };
        
        Function2<Integer, Integer, Integer> result = func.compose(func2);
        assertEquals(result.run(200, 100), Integer.valueOf(-300));
    }
    
    @Test
    public void testCurry() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            
            @Override
            public Integer run(Integer inputFirst, Integer inputSecond) {
                return inputFirst + inputSecond;
            }
        };
        
        Function1<Integer, Function1<Integer, Integer>> result = func.curry();
        assertEquals(result.run(1).run(2), Integer.valueOf(3));
    }
    
    @Test
    public void testBind1() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            
            @Override
            public Integer run(Integer inputFirst, Integer inputSecond) {
                return inputFirst - inputSecond;
            }
        };
        
        Function1<Integer, Integer> result = func.bind1(11);
        assertEquals(result.run(20), Integer.valueOf(-9));
    }
    
    @Test
    public void testBind2() {
        Function2<Integer, Integer, Integer> func = new Function2<Integer, Integer, Integer>() {
            
            @Override
            public Integer run(Integer inputFirst, Integer inputSecond) {
                return inputFirst - inputSecond;
            }
        };
        
        Function1<Integer, Integer> result = func.bind2(20);
        assertEquals(result.run(11), Integer.valueOf(-9));
    }
}
