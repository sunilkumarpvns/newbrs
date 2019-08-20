package com.elitecore.exprlib.symbol.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.symbol.SymbolFactoryConfiguration;

/**
 * @author milan paliwal
 * It maintains the cofiguration of various symbols
 */
public class SymbolFactoryConfigurationImpl implements SymbolFactoryConfiguration {
	
	private List<String> identifierList = new ArrayList<String>(); 
	private List<String> functionList = new ArrayList<String>();
	
	public SymbolFactoryConfigurationImpl(){
			
	}
	public void addFunction(String functionName) {
		functionList.add(functionName);
	}

	
	public void addIdentifier(String identifier) {
		identifierList.add(identifier);
	}
	
	@Override
	public Operator getOperatorType(String symbol) {
		return Operator.getOperatorType(symbol); 
	}

	public int getSymbolType(String symbol) {
		if(symbol.startsWith("\"")){
			return Symbol.literal;
		}else if(getOperatorType(symbol)!=null){
			return Symbol.operator;
		}else if(functionList.contains(symbol)){
			return Symbol.function;
		}else{
			return Symbol.identifier;
		}
	}

}
