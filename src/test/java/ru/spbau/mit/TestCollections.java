package ru.spbau.mit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class TestCollections {

    @SuppressWarnings("unchecked")
    @Test
    public void testFilter() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7));
        Predicate<Integer> predicate= new Predicate<Integer>(){
            @Override
            public Boolean run(Integer input) {
                return input.intValue() > 3;
            }
        };
        ArrayList<Integer> target = new ArrayList<Integer>(Arrays.asList(4, 5, 6, 7));
        assertEquals(target, Collections.filter(predicate, a, a.getClass()));
        ArrayList<Integer> res = new ArrayList<Integer>();
        Collections.filter(predicate, a, res);
        assertEquals(target, res);
    }
    
    @Test
    public void testFoldl() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Function2<String, Integer, String> func = new Function2<String, Integer, String>() {
            
            @Override
            public String run(String inputFirst, Integer inputSecond) {
                StringBuilder builder = new StringBuilder(inputFirst);
                for(int i = 0; i < inputSecond; i++){
                    builder.append((char)(i + 'a'));
                }
                return builder.toString();
            }
        };
        assertEquals("BEGINS aababcabcd", Collections.foldl(func, "BEGINS ", a));
    }
    
    @Test
    public void testFoldr() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Function2<Integer, String, String> func = new Function2<Integer, String, String>() {
            
            @Override
            public String run(Integer inputFirst, String inputSecond) {
                StringBuilder builder = new StringBuilder(inputSecond);
                for(int i = 0; i < inputFirst; i++){
                    builder.append((char)(i + 'a'));
                }
                return builder.toString();
            }
        };
        assertEquals("BEGINS abcdabcaba", Collections.foldr(func, "BEGINS ", a));
        assertEquals("BEGINS abcdabcaba", Collections.foldrNotRecursive(func, "BEGINS ", a));
    }    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMap() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Function1<Integer, String> func = new Function1<Integer, String>() {
            
            @Override
            public String run(Integer inputFirst) {
                StringBuilder builder = new StringBuilder("(");
                for(int i = 0; i < inputFirst; i++){
                    builder.append("a");
                }
                builder.append(")");
                return builder.toString();
            }
        };
        ArrayList<String> target = new ArrayList<String>(Arrays.asList("(a)", "(aa)", "(aaa)", "(aaaa)"));
        assertEquals(target, Collections.map(func, a, ArrayList.class));
        ArrayList<String> result = new ArrayList<String>();
        Collections.map(func, a, result);
        assertEquals(target, result);
    }   
    
    @SuppressWarnings("unchecked")
    @Test
    public void testUnless() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Predicate<Integer> func = new Predicate<Integer>() {
            
            @Override
            public Boolean run(Integer input) {
                return input > 3;
            }
        };
        ArrayList<Integer> target = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        assertEquals(target, Collections.takeUnless(func, a, ArrayList.class));
        ArrayList<Integer> result = new ArrayList<Integer>();
        Collections.takeUnless(func, a, result);
        assertEquals(target, result);
    }   
    
    @SuppressWarnings("unchecked")
    @Test
    public void testWhile() throws InstantiationException, IllegalAccessException {
        ArrayList<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,4));
        Predicate<Integer> func = new Predicate<Integer>() {
            
            @Override
            public Boolean run(Integer input) {
                return input <= 3;
            }
        };
        ArrayList<Integer> target = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        assertEquals(target, Collections.takeWhile(func, a, ArrayList.class));
        ArrayList<Integer> result = new ArrayList<Integer>();
        Collections.takeWhile(func, a, result);
        assertEquals(target, result);
    } 
}


