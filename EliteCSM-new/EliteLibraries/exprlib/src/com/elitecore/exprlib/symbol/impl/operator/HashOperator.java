package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.Operator;


public class HashOperator extends OtherOperator {

	private static final long serialVersionUID = 1L;
	String symbol;
	
	public HashOperator(String symbol){
		this.symbol=symbol;
	}
	
	public String toString(){
		return this.symbol;
	}
	
	public Operator getOperatorType() {
		return Operator.HASH;
	}
	
	public String getName() {
		return this.symbol;
	}

}
