package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;

public class OROperator extends LogicalOperator {
	
	private static final long serialVersionUID = 1L;
	String symbol;
	
	public OROperator(String symbol){
		this.symbol=symbol;
	}
	public String toString(){
		return this.symbol;
	}

	public Operator getOperatorType(){
		return Operator.OR;
	}
	
	public String getName() {
		return this.symbol;
	}
}
