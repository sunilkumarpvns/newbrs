package com.elitecore.diameterapi.core.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;

public class MappingParser {
	private static final String MODULE = "MAPPNG-PRSR";
	public static final int END_OF_STRING = -1;
	private static final boolean END_OF_LIST=true;
	
	static boolean isWhitespace(char ch) {
        return (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n'  || ch == '\f' || ch == ',');
    }
	private static boolean isOperator(char ch1) {
		String ch=String.valueOf(ch1);
		return(ch .equals("=") || ch.equals("{") || ch.equals("}")  );
	}
	
	private static boolean isEscape(char currentChar) {
		return (currentChar == '\\');
	}
	
	private static List<String> parse(String expression){
		StringReader stringReader = new StringReader(expression);
		int currentChar = 0;
		List<String> list = new ArrayList<String>();
		StringBuilder currentSymbol = new StringBuilder(10);
		try {
			while((currentChar=stringReader.read())!= END_OF_STRING)
			{
					if(isEscape((char)currentChar)){
						try {
							currentChar = stringReader.read();
							currentSymbol.append((char) currentChar);
							continue;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(isWhitespace((char)currentChar)){
						if(currentSymbol.length() > 0 ){
							String symbol = currentSymbol.toString();
							currentSymbol = new StringBuilder(10);
							list.add(symbol);
						}
						continue;
					}
					if(isOperator((char)currentChar)){
						if(currentSymbol.length() > 0 ){
							String symbol = currentSymbol.toString();
							currentSymbol = new StringBuilder(10);
							list.add(symbol);
						}
						String operatorSymbol = String.valueOf((char)currentChar);
						list.add(operatorSymbol);
						continue;
					}
					if(((char)currentChar) == '"') {
						String symbol = readLiteral(stringReader);
						list.add(symbol);
						continue;
					}
					if(((char)currentChar) == '$') {
						String symbol = readKeyword(stringReader);
						list.add(symbol);
						continue;
					}
					currentSymbol.append((char) currentChar);
				} 
			//if still the current Symbol has some values
			if(currentSymbol.length() > 0 ){
				String symbol = currentSymbol.toString();
				currentSymbol = new StringBuilder(10);
				list.add(symbol);
			}
		}catch(Exception e){
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, e.getMessage());
		}
		return list;
	}
	
	private static String readLiteral(StringReader stringReader) {
		int currentChar = END_OF_STRING;
		StringBuilder literal = new StringBuilder();
		try {
			literal.append('"');
			currentChar = stringReader.read();
		}catch (IOException e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, e.getMessage());
		}
		while((char)currentChar != '"' && currentChar != END_OF_STRING){
			if(isEscape((char)currentChar)){
				try {
					currentChar = stringReader.read();
					literal.append((char)currentChar);
				} catch (IOException e) {
					LogManager.getLogger().trace(MODULE, e);
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, e.getMessage());
				}
			}else{
				literal.append((char)currentChar);
			}
			try{
				currentChar = stringReader.read();
			}catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, e.getMessage());
			}
		}
		if((char)currentChar == '"')
			literal.append('"');
		return literal.toString(); 
	}
	
	private static String readKeyword(StringReader stringReader) {
		int currentChar = END_OF_STRING;
		StringBuilder literal = new StringBuilder();
		try {
			literal.append('$');
			currentChar = stringReader.read();
		}catch (IOException e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, e.getMessage());
		}
	
		while(currentChar != END_OF_STRING && !isWhitespace((char)currentChar)){
			if((char)currentChar == '{'){
				literal.append(readToken(stringReader));
			}else {
				literal.append((char)currentChar);
			}
			
			try{
				currentChar = stringReader.read();
			}catch (IOException e) {
				LogManager.getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, e.getMessage());
			}
		}
		return literal.toString(); 
	}
	public static LinkedHashMap<String, String>getParsedMap(String expression){
		List<String> symbolList = MappingParser.parse(expression);
		return getMap(symbolList);
		
	}
	private static LinkedHashMap<String, String>getMap(List<String> symbolList){
		
		LinkedHashMap<String,String> map=new LinkedHashMap<String, String>();
		Iterator<String> iterator= symbolList.iterator();
		String previous,current,next;
		previous=current=next=null;
		
		while(iterator.hasNext()==END_OF_LIST){
			current=iterator.next();
			
			if(current.equals("=")){
				next=iterator.next();
				map.put(previous, next);
				continue;
			}else if(current.equals("{")){
				map=getGroupedAttribute(previous,map,iterator);
			}
			previous=current;
		}
		
		return map;
	}
	
	private static LinkedHashMap<String,String>getGroupedAttribute(String head,LinkedHashMap<String,String>map,Iterator<String> iterator){
		String previous,current,next;
		previous=current=next=null;
	
		while(iterator.hasNext() && !(current=iterator.next()).equals("}")){
			
			
			if(current.equals("=")){
				next=iterator.next();
				map.put(previous, next);
				continue;
			}else if(current.equals("{")){
				map=getGroupedAttribute(previous,map,iterator);
			}
			previous=head+"."+current;
		}
		
		return map;
	}
	

	public static void main(String args[]){
		
		BufferedReader read = null;
		try{
			read = new BufferedReader(new FileReader(new File("src/parameter.txt")));
			String expression = "";
			
			while((expression = read.readLine())!=null ){
				System.out.println("Line: "+expression);
				List<String> symbolList = MappingParser.parse(expression);
				System.out.println("Size: "+symbolList.size());
				System.out.println("Symbols:");
				for(String symbol:symbolList){
					System.out.println(symbol);
				}
				LinkedHashMap<String,String> map=MappingParser.getMap(symbolList);
			
				Iterator<String> iterator =map.keySet().iterator();  
				       
				while (iterator.hasNext()) {  
					String key = iterator.next().toString();  
					String value = map.get(key).toString();   
					System.out.println(key + " = " + value);  
				   }  
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(read);
		}
	}
	
	private static String readToken(StringReader stringReader){
		int currentChar = END_OF_STRING;
		StringBuilder literal = new StringBuilder();
		try {
			literal.append('{');
			currentChar = stringReader.read();
		}catch (IOException e) {
			
		}
		while((char)currentChar!='}' && currentChar != END_OF_STRING){
			literal.append((char)currentChar);
			try {
				currentChar = stringReader.read();
			} catch (IOException e) {
			}
		}
		if((char)currentChar == '}')
			literal.append('}');
		return literal.toString(); 
	
		
	}
}
