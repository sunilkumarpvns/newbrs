package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;

/**
 * @author milan paliwal
 */

public class ClosingBraces extends OtherOperator {
	
	private static final long serialVersionUID = 1L;
	String symbol;
	
	public ClosingBraces(String symbol){
		this.symbol=symbol;
	}
	public String toString(){
		return this.symbol;
	}
	
	public Operator getOperatorType(){
		return Operator.CLOSINGBRACES;
	}

	public String getName() {
		return this.symbol;
	}
}
