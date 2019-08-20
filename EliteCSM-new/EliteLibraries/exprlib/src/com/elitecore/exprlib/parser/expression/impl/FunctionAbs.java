package com.elitecore.exprlib.parser.expression.impl;

import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;

public class FunctionAbs  extends AbstractIntegerFunctionExpression {

	private static final long serialVersionUID = 1L;


	public int getFunctionType() {
		return 0;
	}
	@Override
	public long getLongValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException{
		if(argumentList.size()!=1)
			throw new IllegalArgumentException("Number of parameter mismatch ,abs function has 1 arguments 1)value ");
	
		return Math.abs(argumentList.get(0).getLongValue(valueProvider));
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)throws InvalidTypeCastException, IllegalArgumentException,MissingIdentifierException{
 		return String.valueOf(getLongValue(valueProvider));
	}

	
	public String getName() {
		return "abs";
	}


}
