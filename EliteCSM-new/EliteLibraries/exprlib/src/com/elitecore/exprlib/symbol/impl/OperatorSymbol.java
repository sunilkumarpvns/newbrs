package com.elitecore.exprlib.symbol.impl;

import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.SymbolImpl;

public abstract class  OperatorSymbol extends SymbolImpl {
	
	private static final long serialVersionUID = 1L;
	public static final int otherOperator=0;
	public static final int logicalOperator=1;
	public static final int comparisionOperator=2;
	
	public int getType(){
		return Symbol.operator;
	}
	public abstract Operator getOperatorType();
	
	public abstract int getOperatorCategory();
	
}
