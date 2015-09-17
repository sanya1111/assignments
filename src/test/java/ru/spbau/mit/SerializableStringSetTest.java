package ru.spbau.mit;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerializableStringSetTest {

    @Test
    public void testSimple() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
    }
    
    @Test
    public void testAll() {
    	StringSet stringSet = instance();
    	assertTrue(stringSet.add("abc"));
    	assertTrue(stringSet.add("bca"));
    	assertTrue(stringSet.add("abcda"));
    	assertEquals(3, stringSet.size());
    	assertEquals(2, stringSet.howManyStartsWithPrefix("ab"));
    	assertTrue(stringSet.remove("abcda"));
    	assertTrue(stringSet.contains("abc"));
    	assertEquals(1, stringSet.howManyStartsWithPrefix("ab"));
    }

    @Test
    public void testSimpleSerialization() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.add("cde"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) stringSet).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ((StreamSerializable) stringSet).deserialize(inputStream);

        assertTrue(stringSet.contains("abc"));
        assertTrue(stringSet.contains("cde"));
        assertEquals(2, stringSet.size());
    }
    
    
    @Test
    public void testSimple2(){
    	StringSet stringSet = instance();

        assertTrue(stringSet.add("abcd"));
        assertTrue(stringSet.add("abcg"));
        assertTrue(stringSet.add("abckk"));
        assertTrue(stringSet.add("abf"));
        assertEquals(4, stringSet.howManyStartsWithPrefix("a"));
        assertTrue(stringSet.remove("abckk"));
        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(0, stringSet.howManyStartsWithPrefix("abcadfadf"));
    }
    
    @Test
    public void testSimple3(){
    	StringSet stringSet = instance();

    	StringBuilder builder = new StringBuilder();
    	for(int i = 0; i < 100; i++){
    		builder.append("a");
//    		assertTrue(stringSet.add(builder.toString()));
    		assertFalse(stringSet.contains(builder.toString()));
    	}
    	for(int i = 0; i < 100; i++){
    		assertFalse(stringSet.remove(builder.toString()));
    		assertTrue(stringSet.add(builder.toString()));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	assertEquals(100, stringSet.size());
    }
    
    


    @Test(expected=SerializationException.class)
    public void testSimpleSerializationFails() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.add("cde"));

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Fail");
            }
        };

        ((StreamSerializable) stringSet).serialize(outputStream);
    }

    public static StringSet instance() {
        try {
            return (StringSet) Class.forName("ru.spbau.mit.StringSetImpl").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Error while class loading");
    }
}
