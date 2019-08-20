package com.elitecore.exprlib.symbol.impl;

import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.SymbolImpl;

public class IdentifierSymbol extends SymbolImpl {
	
	private static final long serialVersionUID = 1L;
	private String symbol;
	
	
	public IdentifierSymbol(String symbol) {
		this.symbol=symbol;
	}
	public int getType(){
		return Symbol.identifier;
	}
	
	public String toString(){
		return this.symbol;
	}
	
	public String getName() {
		return this.symbol;
	}
}
