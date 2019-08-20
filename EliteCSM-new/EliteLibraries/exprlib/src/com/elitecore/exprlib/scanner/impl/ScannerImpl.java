
package com.elitecore.exprlib.scanner.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.io.Closeables;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.symbol.SymbolFactory;
import com.elitecore.exprlib.symbol.impl.SymbolFactoryConfigurationImpl;
import com.elitecore.exprlib.symbol.impl.SymbolFactoryImpl;

/**
 * @author milan paliwal
 * this class generate different symbols from expression
 */
public class ScannerImpl implements Scanner {
	
	private SymbolFactory factory;
	private SymbolFactoryConfigurationImpl symbolConfiguration;
	private List<Character> whitespaces;
	private List<Character> operators;
	
	private static final int END_OF_STRING = -1;
	private static final char DOUBLE_QUOTE = '"';
	public ScannerImpl() {
		symbolConfiguration = new SymbolFactoryConfigurationImpl();
		factory = new SymbolFactoryImpl(symbolConfiguration);
		operators = getDefaultOperatorList();
		whitespaces = getDefaultWhitespaceList();		
	}
	
	/**
	 * Creates scanner with configurable whitespace and operators
	 * if whitespace or operators is null then
	 * 	it will take default whitespace and operators
	 * 
	 */
	public ScannerImpl(List<Character> whitespaces, List<Character> operators) {
		this();
		if(whitespaces == null){
			this.whitespaces = getDefaultWhitespaceList();
		}else {
			this.whitespaces = new ArrayList<Character>(whitespaces);
		}
		
		if(operators == null){
			operators = getDefaultOperatorList();
		}else {
			this.operators = new ArrayList<Character>(operators);
		}
	}
	
	private List<Character> getDefaultWhitespaceList(){
		List<Character> whitespaces = new ArrayList<Character>();
		whitespaces.add(' ');
		whitespaces.add('\t');
		whitespaces.add('\r');
		whitespaces.add('\n');
		whitespaces.add('\f');
		whitespaces.add(',');
		return whitespaces;
	}
	
	private List<Character> getDefaultOperatorList(){
		List<Character> operators = new ArrayList<Character>();
		operators.add('=');
		operators.add('<');
		operators.add('>');
		operators.add('(');
		operators.add(')');
		operators.add('$');
		operators.add('{');
		operators.add('}');
		return operators;
	}
	
	protected boolean isWhitespace(char ch) {
		return whitespaces.contains(ch);
	}
	protected boolean isOperator(char ch) {
		return operators.contains(ch);
	}
	
	private boolean isEscape(char currentChar) {
		return (currentChar == '\\');
	}
	
	private List<Symbol> parse(String expression)throws InvalidSymbolException{
		if(expression==null || expression.trim().isEmpty()){
			throw new InvalidSymbolException("Symbol can't be generated from empty expression string");
		}
		StringReader stringReader = new StringReader(expression);
		int currentChar = 0;
		
		List<Symbol> symbols = new ArrayList<Symbol>();
		StringBuilder currentSymbol = new StringBuilder();
		try {
			while((currentChar=stringReader.read())!= END_OF_STRING){
				
					if(isEscape((char)currentChar)){
						if((currentChar=stringReader.read())!= END_OF_STRING){
							currentSymbol.append((char) currentChar);
						}else{
							//eg. expression = "A\" 
							throw new InvalidSymbolException("unexpected token at end of expression : '\'");
						}
					}else if(isWhitespace((char)currentChar)){
						if(currentSymbol.toString().trim().length() > 0 ){
							createAndADDsymbol(currentSymbol.toString(), symbols);
							currentSymbol = new StringBuilder();
						}
					} else if(isOperator((char)currentChar)){
						if(currentSymbol.length() > 0 ){
							createAndADDsymbol(currentSymbol.toString(), symbols);
							currentSymbol = new StringBuilder();
						}
						createAndADDsymbol(String.valueOf((char)currentChar), symbols);
					}else if(((char)currentChar) == DOUBLE_QUOTE) {
						if(currentSymbol.length() > 0 ){
							createAndADDsymbol(currentSymbol.toString(), symbols);
							currentSymbol = new StringBuilder();
						}
						createAndADDsymbol(readLiteral(stringReader), symbols);
					}else{
						currentSymbol.append((char) currentChar);
					}
				} 
			//if still the current Symbol has some values
			if(currentSymbol.length() > 0 ){
				createAndADDsymbol(currentSymbol.toString(), symbols);
				currentSymbol = new StringBuilder();
			}
		}catch(InvalidSymbolException invalidSymbolEx){
			throw invalidSymbolEx;
		}catch(Exception e){
			throw new InvalidSymbolException("Failed to parse Expression: " + expression + ", Reason: " + e.getMessage(), e);
		}
		return symbols;
	}
	


	private String readLiteral(StringReader stringReader)throws InvalidSymbolException {
		int currentChar = END_OF_STRING;
		StringBuilder literal = new StringBuilder(Character.toString(DOUBLE_QUOTE));
		currentChar = stringReader.read();
		while((char)currentChar != DOUBLE_QUOTE){
			if(currentChar == END_OF_STRING){
				throw new InvalidSymbolException("Literal not properly ended");
			}
			
			if(isEscape((char)currentChar)){
				currentChar = stringReader.read();
			}
			
			literal.append((char)currentChar);
			
			currentChar = stringReader.read();			
		}
		literal.append(Character.toString(DOUBLE_QUOTE));
		return literal.toString();
	}

	private void createAndADDsymbol(String symbolStr, List<Symbol> symbols) {
		Symbol symbol = factory.createSymbol(symbolStr);
		symbols.add(symbol);
	}
	
	public List<Symbol> getSymbols(String expression)throws InvalidSymbolException {
		List<Symbol> list = new ArrayList<Symbol>();
		list = parse(expression);
		return list;
	}
	
	public static void main(String args[]){
		BufferedReader read = null;
		try{
			read = new BufferedReader(new FileReader(new File("src/com/elitecore/exprlib/scanner/impl/parameter.txt"))); //NOSONAR - Reason: Resources should be closed
			String expression = "";
			
			while((expression = read.readLine())!=null ){
				System.out.println(expression);
				ScannerImpl s=new ScannerImpl();
				List<Symbol> symbolList = s.getSymbols(expression);
				System.out.println(symbolList.size());
				for(Symbol symbol:symbolList){
					System.out.println(symbol);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(read);
		}
	}

	
	public void addFunction(String functionName) {
		symbolConfiguration.addFunction(functionName);
	}
	
	/**
	 * StringReader reads from String and never throw any exception
	 * @author harsh.patel
	 */
	private class StringReader extends java.io.StringReader{

		public StringReader(String s) {
			super(s);
		}
		
		@Override
		public int read(){
			try{
				return super.read();
			}catch(Exception ex){
				// never throw exception
			}
			return END_OF_STRING;
		}
	}
}





