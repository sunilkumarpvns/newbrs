package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;

/**
 * @author milan paliwal
 */

public class Equals extends ComparisionOperator {
	
	private static final long serialVersionUID = 1L;
	String symbol;
	
	public Equals(String symbol){
		this.symbol=symbol;
	}
	public String toString(){
		return this.symbol;
	}
	
	public Operator getOperatorType(){
		return Operator.EQUALS;
	}
	
	public String getName() {
		return this.symbol;
	}
}
