package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;



public class NotOperator extends LogicalOperator {
	
	private static final long serialVersionUID = 1L;
	String symbol;
	
	public NotOperator(String symbol){
		this.symbol=symbol;
	}
	
	public Operator getOperatorType() {
		return Operator.NOT;
	}
	
	public String toString(){
		return this.symbol;
	}

	public String getName() {
		return this.symbol;
	}

}
