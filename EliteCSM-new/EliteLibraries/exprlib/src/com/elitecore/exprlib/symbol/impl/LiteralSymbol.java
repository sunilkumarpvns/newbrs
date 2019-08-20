package com.elitecore.exprlib.symbol.impl;

import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.SymbolImpl;

public class LiteralSymbol extends SymbolImpl {
	
	private static final long serialVersionUID = 1L;
	private String symbol;
	
	
	public LiteralSymbol(String symbol) {
		this.symbol=symbol;
		
	}
	public int getType(){
		return Symbol.literal;
	}
	
	public String toString(){
		return this.symbol;
	}

	public String getName() {
		return this.symbol;
	}
}
