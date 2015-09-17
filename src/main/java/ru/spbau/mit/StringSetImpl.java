package ru.spbau.mit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;


public class StringSetImpl implements StringSet, StreamSerializable{
	
	private class Node{
		static final int ROUTES_SIZE = 200;
		private Node []routes = new Node[ROUTES_SIZE];
		private int prefCount = 0;
		boolean end = false;
		
		public void push(Character simbol){
			routes[simbol] = new Node();
		}
		
		public void remove(Character simbol){
			routes[simbol] = null;
		}
		
		public boolean haveNext(Character simbol){
			return routes[simbol] != null;
		}
		
		
		public Node next(Character simbol){
			return routes[simbol];
		}
		
		public void incPref(){
			this.prefCount++;
		}
		
		public void decPref(){
			this.prefCount--;
		}
		
		public int getPref(){
			return this.prefCount;
		}
		
		public void markEnd(){
			end = true;
		}
		public void markNotEnd(){
			end = false;
		}
		
		public boolean isEnd(){
			return end;
		}
		
		public void dfs(StringBuilder builder, ArrayList<String> result){
			if(this.isEnd()){
				result.add(builder.toString());
			}
			
			for(int i = 0; i < ROUTES_SIZE; i++){
				Node node = routes[i];
				if(node != null){
					builder.append((char)i);
					node.dfs(builder, result);
					builder.deleteCharAt(builder.length() - 1);
				}
			}
		}
		
		public void clean(){
			prefCount = 0;
			end = false;
			for(int i = 0; i < ROUTES_SIZE; i++){
				Node node = routes[i];
				if(node != null){
					node.clean();
					routes[i] =  null;
				}
			}
		}
	}
	
	private Node head = new Node();
	@Override
	public void serialize(OutputStream out) {
		ArrayList<String> result = new ArrayList<String>();
		head.dfs(new StringBuilder(), result);
		
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))){
			for(String str : result){
				writer.write(str);
				writer.newLine();
			}
		} catch (IOException e) {
			throw new SerializationException();
		}
	}

	@Override
	public void deserialize(InputStream in) {
		head.clean();
		String str;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
			while((str = reader.readLine()) != null){
				add(str);
			}
		} catch (IOException e) {
			throw new SerializationException();
		}
	}

	@Override
	public boolean add(String element) {
		if(this.contains(element)){
			return false;
		}
		Node current = head;
		for(int i = 0; i < element.length(); i++){
			current.incPref();
			Character simbol = element.charAt(i);
			if(!current.haveNext(simbol)){
				current.push(simbol);
			}
			current = current.next(simbol);
		}
		current.incPref();
		current.markEnd();
		return true;
	}

	@Override
	public boolean contains(String element) {
		Node current = head;
		for(int i = 0; i < element.length(); i++){
			Character simbol = element.charAt(i);
			if(!current.haveNext(simbol)){
				return false;
			}
			current = current.next(simbol);
		}
		return current.isEnd();
	}

	@Override
	public boolean remove(String element) {
		if(!this.contains(element)){
			return false;
		}
		Node current = head;
		current.decPref();
		for(int i = 0; i < element.length(); i++){
			Character simbol = element.charAt(i);
			Node next = current.next(simbol);
			next.decPref();
			if(next.getPref() == 0){
				current.remove(simbol);
			}
			current = next;
		}
		current.markNotEnd();
		return true;
	}

	@Override
	public int size() {
		return head.getPref();
	}

	@Override
	public int howManyStartsWithPrefix(String prefix) {
		Node current = head;
		for(int i = 0; i < prefix.length(); i++){
			Character simbol = prefix.charAt(i);
			if(!current.haveNext(simbol)){
				return 0;
			}
			current = current.next(simbol);
		}
		return current.getPref();
	}

}
