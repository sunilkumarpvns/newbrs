package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;

/**
 * @author milan paliwal
 */

public class AndOperator extends LogicalOperator {
	
	private static final long serialVersionUID = 1L;
	String symbol;
	
	public AndOperator(String symbol){
		this.symbol=symbol;
	}
	public String toString(){
		return this.symbol;
	}
	
	public Operator getOperatorType(){
		return Operator.AND;
	}
	
	public String getName() {
		return this.symbol;
	}
}
