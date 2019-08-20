package com.elitecore.exprlib.symbol.impl.operator;

import com.elitecore.exprlib.symbol.impl.OperatorSymbol;

public abstract class ComparisionOperator extends OperatorSymbol {

	private static final long serialVersionUID = 1L;
	public int getOperatorCategory(){
		return OperatorSymbol.comparisionOperator;
	}
}